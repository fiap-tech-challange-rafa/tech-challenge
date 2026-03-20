[CmdletBinding()]
param(
    [switch]$ApplyInfra,
    [switch]$SkipInfra,
    [int]$LambdaPort = 3001,
    [string]$AuthPgContainer = "auth-pg",
    [int]$AuthPgPort = 5432
)

$ErrorActionPreference = "Stop"

$baseDir = "C:\Users\Rafaella\IdeaProjects"
$repos = @{
    tech = Join-Path $baseDir "tech-challange"
    auth = Join-Path $baseDir "auth-lambda"
    dbTf = Join-Path $baseDir "infra-database\terraform"
    k8sTf = Join-Path $baseDir "infra-kubernetes\terraform"
}

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "=== $Message ===" -ForegroundColor Cyan
}

function Assert-Path {
    param([string]$PathToCheck)
    if (-not (Test-Path $PathToCheck)) {
        throw "Path not found: $PathToCheck"
    }
}

function Assert-Command {
    param([string]$CommandName)
    if (-not (Get-Command $CommandName -ErrorAction SilentlyContinue)) {
        throw "Command '$CommandName' not found in PATH."
    }
}

function Invoke-External {
    param(
        [scriptblock]$CommandBlock,
        [string]$ErrorMessage
    )

    & $CommandBlock
    if ($LASTEXITCODE -ne 0) {
        throw "$ErrorMessage (exit code $LASTEXITCODE)"
    }
}

function Wait-HttpOk {
    param(
        [string]$Url,
        [int]$MaxAttempts = 30,
        [int]$DelaySeconds = 2
    )

    for ($attempt = 1; $attempt -le $MaxAttempts; $attempt++) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
                return $true
            }
        } catch {
        }

        Start-Sleep -Seconds $DelaySeconds
    }

    return $false
}

function Reset-GrafanaAdminPassword {
    param(
        [string]$ContainerName = "grafana",
        [string]$Password = "admin"
    )

    for ($attempt = 1; $attempt -le 20; $attempt++) {
        docker exec $ContainerName grafana cli admin reset-admin-password $Password *> $null
        if ($LASTEXITCODE -eq 0) {
            return $true
        }
        Start-Sleep -Seconds 2
    }

    return $false
}

function Set-AuthLambdaDependencyVersion {
    param([string]$AuthRepoDir)

    $packageJsonPath = Join-Path $AuthRepoDir "package.json"
    if (-not (Test-Path $packageJsonPath)) {
        return
    }

    $raw = Get-Content $packageJsonPath -Raw
    $package = $raw | ConvertFrom-Json

    if ($null -eq $package.dependencies) {
        return
    }

    $currentVersion = $package.dependencies.jsonwebtoken
    if ($currentVersion -ne "^9.0.2") {
        $package.dependencies.jsonwebtoken = "^9.0.2"
        $package | ConvertTo-Json -Depth 50 | Set-Content $packageJsonPath -Encoding UTF8
        Write-Host "Ajustado auth-lambda/package.json: jsonwebtoken => ^9.0.2" -ForegroundColor Yellow
    }
}

function Set-AuthLambdaTemplatePaths {
    param([string]$AuthRepoDir)

    $templatePath = Join-Path $AuthRepoDir "template.yaml"
    if (-not (Test-Path $templatePath)) {
        return
    }

    $content = Get-Content $templatePath -Raw
    $content = $content -replace 'CodeUri:\s*src/', 'CodeUri: .'
    $content = $content -replace 'Handler:\s*handlers/authenticate.lambdaHandler', 'Handler: src/handlers/authenticate.lambdaHandler'
    Set-Content -Path $templatePath -Value $content -Encoding UTF8
}

function Ensure-AuthPostgres {
    param(
        [string]$ContainerName,
        [int]$Port
    )

    $exists = ((docker ps -a --filter "name=^$ContainerName$" --format "{{.Names}}") -eq $ContainerName)
    if (-not $exists) {
        Invoke-External {
            docker run --name $ContainerName -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=tech_challenge -p "${Port}:5432" -d postgres:15
        } "Failed to create auth postgres container"
    } else {
        Invoke-External { docker start $ContainerName } "Failed to start auth postgres container"
    }

    $ready = $false
    for ($attempt = 1; $attempt -le 30; $attempt++) {
        docker exec $ContainerName pg_isready -U postgres -d tech_challenge *> $null
        if ($LASTEXITCODE -eq 0) {
            $ready = $true
            break
        }
        Start-Sleep -Seconds 2
    }

    if (-not $ready) {
        throw "Auth postgres did not become ready in time."
    }

    Invoke-External {
        docker exec -i $ContainerName psql -U postgres -d tech_challenge -c "CREATE TABLE IF NOT EXISTS clientes (id BIGINT PRIMARY KEY, cpf VARCHAR(11) NOT NULL, nome VARCHAR(120), status VARCHAR(20));"
    } "Failed to create clientes table for auth-lambda"

    Invoke-External {
        docker exec -i $ContainerName psql -U postgres -d tech_challenge -c "INSERT INTO clientes (id, cpf, nome, status) VALUES (1,'12345678909','Cliente Demo','ATIVO') ON CONFLICT (id) DO NOTHING;"
    } "Failed to seed cliente demo for auth-lambda"
}

function Start-AuthLambdaLocal {
    param(
        [string]$AuthRepoDir,
        [int]$Port,
        [int]$DbPort
    )

    $samCommand = "Set-Location '$AuthRepoDir'; sam local start-api --port $Port --parameter-overrides DatabaseHost=host.docker.internal DatabasePort=$DbPort DatabaseName=tech_challenge DatabaseUser=postgres"
    Start-Process -FilePath "powershell" -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-Command", $samCommand | Out-Null
}

function Prepare-TerraformVars {
    param([string]$TerraformDir)

    Push-Location $TerraformDir
    try {
        if (-not (Test-Path "terraform.tfvars")) {
            Copy-Item "terraform.tfvars.example" "terraform.tfvars"
            Write-Host "Arquivo terraform.tfvars criado em $TerraformDir. Ajuste os valores antes do apply." -ForegroundColor Yellow
            return $false
        }

        return $true
    } finally {
        Pop-Location
    }
}

Assert-Path $repos.tech
Assert-Path $repos.auth
Assert-Path $repos.dbTf
Assert-Path $repos.k8sTf

Assert-Command docker
Assert-Command npm
Assert-Command sam

$terraformAvailable = [bool](Get-Command terraform -ErrorAction SilentlyContinue)
$awsAvailable = [bool](Get-Command aws -ErrorAction SilentlyContinue)
$kubectlAvailable = [bool](Get-Command kubectl -ErrorAction SilentlyContinue)

if ($ApplyInfra -and $SkipInfra) {
    throw "Voce passou -ApplyInfra e -SkipInfra ao mesmo tempo. Use apenas um."
}

if ($ApplyInfra -and -not $terraformAvailable) {
    throw "Terraform nao encontrado no PATH. Instale Terraform para usar -ApplyInfra."
}

if (-not $terraformAvailable -and -not $SkipInfra) {
    Write-Host "Terraform nao encontrado no PATH. Etapas de infra serao puladas." -ForegroundColor Yellow
    $SkipInfra = $true
}

if ($ApplyInfra -and (-not $awsAvailable -or -not $kubectlAvailable)) {
    throw "Comandos aws e kubectl sao obrigatorios para -ApplyInfra."
}

Write-Host "Iniciando subida dos 4 repositorios para o video..." -ForegroundColor Green

Write-Step "1/4 - Subindo tech-challange (Docker Compose)"
Push-Location $repos.tech
try {
    Invoke-External { docker compose up -d --build db redis prometheus grafana app } "Failed to start Docker Compose services"
    Invoke-External { docker compose ps } "Failed to list Docker Compose services"
} finally {
    Pop-Location
}

if (Wait-HttpOk -Url "http://localhost:8080/actuator/health") {
    Write-Host "tech-challange ativo em http://localhost:8080" -ForegroundColor Green
} else {
    Write-Host "tech-challange iniciado, mas healthcheck ainda nao respondeu. Veja logs com: docker logs -f tech-challange-app" -ForegroundColor Yellow
}

if (Wait-HttpOk -Url "http://localhost:3000") {
    if (Reset-GrafanaAdminPassword -ContainerName "grafana" -Password "admin") {
        Write-Host "Senha do Grafana sincronizada para admin/admin" -ForegroundColor Green
    } else {
        Write-Host "Nao foi possivel resetar senha do Grafana automaticamente. Tente manualmente: docker exec grafana grafana cli admin reset-admin-password admin" -ForegroundColor Yellow
    }
    Write-Host "Grafana ativo em http://localhost:3000" -ForegroundColor Green
} else {
    Write-Host "Grafana iniciado, mas ainda nao respondeu. Veja logs com: docker logs -f grafana" -ForegroundColor Yellow
}

Write-Step "2/4 - Subindo auth-lambda (SAM Local + PostgreSQL)"
Push-Location $repos.auth
try {
    if (-not (Test-Path ".env") -and (Test-Path ".env.example")) {
        Copy-Item ".env.example" ".env"
        Write-Host ".env criado a partir de .env.example" -ForegroundColor Yellow
    }

    Set-AuthLambdaDependencyVersion -AuthRepoDir $repos.auth
    Set-AuthLambdaTemplatePaths -AuthRepoDir $repos.auth

    Invoke-External { npm install } "Failed to install npm dependencies"

    if (Test-Path ".aws-sam") {
        Remove-Item ".aws-sam" -Recurse -Force
    }

    Invoke-External { sam build --no-cached } "Failed to build SAM project"
} finally {
    Pop-Location
}

Ensure-AuthPostgres -ContainerName $AuthPgContainer -Port $AuthPgPort
Start-AuthLambdaLocal -AuthRepoDir $repos.auth -Port $LambdaPort -DbPort $AuthPgPort
Write-Host "auth-lambda iniciado em nova janela do PowerShell na porta $LambdaPort" -ForegroundColor Green

if (-not $SkipInfra) {
    Write-Step "3/4 - Infra Database (Terraform)"
    $dbVarsReady = Prepare-TerraformVars -TerraformDir $repos.dbTf
    Push-Location $repos.dbTf
    try {
        Invoke-External { terraform init -upgrade } "Terraform init failed (infra-database)"
        Invoke-External { terraform validate } "Terraform validate failed (infra-database)"
        Invoke-External { terraform plan -out=tfplan } "Terraform plan failed (infra-database)"

        if ($ApplyInfra) {
            if ($dbVarsReady) {
                Invoke-External { terraform apply -auto-approve tfplan } "Terraform apply failed (infra-database)"
            } else {
                Write-Host "Apply pulado no infra-database: preencha terraform.tfvars e execute novamente com -ApplyInfra." -ForegroundColor Yellow
            }
        } else {
            Write-Host "Apply pulado no infra-database (execute com -ApplyInfra para provisionar)." -ForegroundColor Yellow
        }
    } finally {
        Pop-Location
    }

    Write-Step "4/4 - Infra Kubernetes (Terraform)"
    $k8sVarsReady = Prepare-TerraformVars -TerraformDir $repos.k8sTf
    Push-Location $repos.k8sTf
    try {
        Invoke-External { terraform init -upgrade } "Terraform init failed (infra-kubernetes)"
        Invoke-External { terraform validate } "Terraform validate failed (infra-kubernetes)"
        Invoke-External { terraform plan -out=tfplan } "Terraform plan failed (infra-kubernetes)"

        if ($ApplyInfra) {
            if ($k8sVarsReady) {
                Invoke-External { terraform apply -auto-approve tfplan } "Terraform apply failed (infra-kubernetes)"

                Write-Host "Atualizando kubeconfig do EKS..." -ForegroundColor Cyan
                Invoke-External { aws eks update-kubeconfig --region us-east-1 --name tech-challenge-cluster } "Failed to update kubeconfig"
                Invoke-External { kubectl get nodes } "Failed to query kubernetes nodes"
            } else {
                Write-Host "Apply pulado no infra-kubernetes: preencha terraform.tfvars e execute novamente com -ApplyInfra." -ForegroundColor Yellow
            }
        } else {
            Write-Host "Apply pulado no infra-kubernetes (execute com -ApplyInfra para provisionar)." -ForegroundColor Yellow
        }
    } finally {
        Pop-Location
    }
} else {
    Write-Step "3/4 e 4/4 - Infra pulada"
    Write-Host "Etapas de infra nao executadas (-SkipInfra ativo ou Terraform ausente)." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Concluido." -ForegroundColor Green
Write-Host "URLs para o video:" -ForegroundColor Green
Write-Host "- App: http://localhost:8080/swagger-ui.html"
Write-Host "- Health: http://localhost:8080/actuator/health"
Write-Host "- Prometheus: http://localhost:9090"
Write-Host "- Grafana: http://localhost:3000 (admin/admin)"
Write-Host "- Lambda local: http://127.0.0.1:$LambdaPort/auth/authenticate"
Write-Host ""
Write-Host "Para subir com provisionamento de infra AWS: .\subir-4-aplicacoes.ps1 -ApplyInfra" -ForegroundColor Cyan
Write-Host "Para subir somente ambiente local (app + lambda): .\subir-4-aplicacoes.ps1 -SkipInfra" -ForegroundColor Cyan
