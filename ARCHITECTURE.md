# Arquitetura Técnica — Tech Challenge Fase 2

## 1. Visão Geral

A aplicação segue o padrão **Arquitetura Hexagonal (Ports & Adapters)**, garantindo separação clara de responsabilidades, testabilidade e independência de frameworks.

```
┌─────────────────────────────────────────────────────────────────┐
│                      REST Controllers (Adapters In)             │
│  (api/ordem-servico, api/admin/clientes, api/admin/veiculos)   │
└──────────────────────────────────┬──────────────────────────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │  Ports (Application Interfaces)
                    │  - CriarOrdemServicoPort   │
                    │  - AprovarOrcamentoPort    │
                    │  - RejeitarOrcamentoPort   │
                    │  - FinalizarOrdemServicoPort
                    │  - EntregarOrdemServicoPort
                    │  - GerarOrcamentoPort      │
                    │  - IncluirServicoNaOSPort  │
                    │  - IncluirPecaNaOSPort     │
                    └──────────────┬──────────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │    Application Services     │
                    │   (Use Cases / Business     │
                    │      Logic Layer)           │
                    └──────────────┬──────────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │    Domain Layer (Núcleo)    │
                    │  - OrdemServico             │
                    │  - ItemServico              │
                    │  - ItemPeca                 │
                    │  - StatusOS (máquina estados)
                    │  - Regras de Negócio        │
                    └──────────────┬──────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         │                         │                         │
    ┌────▼────┐          ┌────────▼────────┐      ┌─────────▼────┐
    │ JPA      │          │ Ports Out       │      │ External     │
    │ Adapter  │          │ (Interfaces)    │      │ Services     │
    │          │          │ - Repository    │      │ - Email      │
    └────┬─────┘          │   Port          │      │ - SMS        │
         │                └────────┬────────┘      └──────────────┘
    ┌────▼──────────┐             │
    │ PostgreSQL /  │             │
    │ H2 (testes)   │             │
    └───────────────┘             │
                          ┌────────▼──────┐
                          │ Infrastructure│
                          │ Adapters      │
                          │ - JPA Adapter │
                          │ - Email Adapter
                          └───────────────┘
```

---

## 2. Componentes da Aplicação

### 2.1 Domain (Núcleo de Negócio)
**Localização**: `src/main/java/br/com/fiap/techchallange/domain/`

- **Entidades**: Conteêm apenas lógica de domínio, sem dependências de framework
  - `OrdemServico`: Agregação raiz com máquina de estados
  - `ItemServico`: Serviços incluídos na OS
  - `ItemPeca`: Peças incluídas na OS
  - `StatusOS`: Enum com possíveis estados de uma OS
  
- **Regras de Negócio**:
  - Transição de status validada (não permite transições inválidas)
  - Cálculo automático de orçamento
  - Validação de pré-condições para cada operação

### 2.2 Application (Casos de Uso)
**Localização**: `src/main/java/br/com/fiap/techchallange/application/`

#### Ports (Interfaces):
- **Portas de Entrada** (`port/in/`):
  - `CriarOrdemServicoPort`: Criar nova OS
  - `GerarOrcamentoPort`: Gerar orçamento
  - `AprovarOrcamentoPort`: Aprovar orçamento (PUT /api/ordem-servico/{id}/aprovar)
  - `RejeitarOrcamentoPort`: Rejeitar orçamento (PUT /api/ordem-servico/{id}/rejeitar)
  - `FinalizarOrdemServicoPort`: Finalizar OS
  - `EntregarOrdemServicoPort`: Entregar OS
  - `IncluirServicoNaOSPort`: Adicionar serviço
  - `IncluirPecaNaOSPort`: Adicionar peça
  - `BuscarOrdemPort`: Buscar/listar OS

- **Portas de Saída** (`port/out/`):
  - `OrdemServicoRepositoryPort`: Persistência
  - Interfaces para notificação (email, SMS)

#### Services (Implementações):
- `CriarOrdemServicoService`
- `AprovarOrcamentoService`
- `RejeitarOrcamentoService`
- `FinalizarOrdemServicoService`
- `EntregarOrdemServicoService`
- `GerarOrcamentoService`
- `IncluirServicoNaOSService`
- `IncluirPecaNaOSService`
- `BuscarOrdemService`

### 2.3 Interfaces (Adapters In)
**Localização**: `src/main/java/br/com/fiap/techchallange/interfaces/`

- **REST Controllers**: 
  - `OrdemServicoController`: Endpoints de ordens de serviço
  - `ClienteController`: Gerenciamento de clientes
  - `VeiculoController`: Gerenciamento de veículos
  - `PecaController`: Gerenciamento de peças
  - `ServicoController`: Gerenciamento de serviços

- **DTOs**: 
  - `CriarOrdemRequest`, `OrdemServicoResponse`
  - `IncluirServicoRequest`, `IncluirPecaRequest`
  - Etc.

### 2.4 Infrastructure (Adapters Out)
**Localização**: `src/main/java/br/com/fiap/techchallange/infrastructure/`

- **JPA Adapters**: Implementam os ports de saída
  - `JpaOrdemServicoRepositoryAdapter`: Persistência de OrdenServicos
  - `JpaClienteRepositoryAdapter`: Persistência de Clientes
  - Etc.

- **Entities JPA**: Mapeamento relacional
  - `OrdemServicoEntity`
  - `ClienteEntity`
  - `VeiculoEntity`
  - Etc.

- **Repositories Spring Data**: Interface com banco de dados
  - `SpringDataOrdemServicoRepository`
  - Etc.

---

## 3. Máquina de Estados — Transições de Status da OS

```
┌─────────────────────────────────────────────────────────────────┐
│                    ESTADOS DE ORDEM DE SERVIÇO                  │
└─────────────────────────────────────────────────────────────────┘

                            [RECEBIDA]
                                │
                                ├──(incluir serviço/peça)──► [EM_DIAGNOSTICO]
                                │
                                └──(gerar orçamento)──────► [AGUARDANDO_APROVACAO]
                                                                    │
                                    ┌───────────────────────────────┼────────────────────────────────┐
                                    │                               │                                │
                    (aprovar)◄──────┴─────────►[EM_EXECUCAO]        │     (rejeitar)
                                    │                               │          │
                                    │                        [CANCELADA] (finalizadas)
                                    │                               ▲
                                    │                               │
                                    └──────────(finalizar)────────────► [FINALIZADA]
                                                                            │
                                                    (entregar)
                                                            │
                                                            ▼
                                                    [ENTREGUE]
```

### Validações de Transição:

| De | Para | Validação |
|---|---|---|
| RECEBIDA | EM_DIAGNOSTICO | Ao incluir serviço/peça |
| EM_DIAGNOSTICO | AGUARDANDO_APROVACAO | Ao gerar orçamento |
| AGUARDANDO_APROVACAO | EM_EXECUCAO | Status == AGUARDANDO_APROVACAO |
| AGUARDANDO_APROVACAO | CANCELADA | Status == AGUARDANDO_APROVACAO (rejeição) |
| EM_EXECUCAO | FINALIZADA | Status == EM_EXECUCAO |
| FINALIZADA | ENTREGUE | Status == FINALIZADA |

**Transições Inválidas**: Lançam `IllegalStateException` com mensagem descritiva.

---

## 4. Fluxo de Deploy End-to-End

### 4.1 Local (Docker Compose)

```bash
# 1. Build da aplicação
mvn clean package -DskipTests

# 2. Build da imagem Docker
docker build -t tech-challenge:latest .

# 3. Executar com Docker Compose (inclui app + banco)
docker-compose up --build

# Aplicação disponível em: http://localhost:8080
# Swagger UI em: http://localhost:8080/swagger-ui.html
```

### 4.2 Kubernetes (Local com Kind)

```bash
# 1. Criar cluster local
kind create cluster --name tech-challenge

# 2. Build e carregar imagem
docker build -t tech-challenge:latest .
kind load docker-image tech-challenge:latest --name tech-challenge

# 3. Aplicar manifests
kubectl apply -f k8s/

# 4. Expor acesso local (opcional)
kubectl port-forward svc/tech-challenge 8080:8080

# Aplicação disponível em: http://localhost:8080
```

### 4.3 Pipeline CI/CD (GitHub Actions)

**Arquivo**: `.github/workflows/ci-cd.yml`

**Etapas**:
1. ✅ Checkout do código
2. ✅ Setup JDK 21
3. ✅ Build Maven + Testes automatizados
4. ✅ Build da imagem Docker
5. ✅ Push para registry (opcional)
6. ✅ Deploy no K8s (com verificação de saúde)

---

## 5. Kubernetes Manifests

### 5.1 Deployment (`k8s/deployment.yaml`)
- Replicação de 2 pods
- Probes de saúde (liveness, readiness)
- Limites de recursos (CPU, memória)
- Variáveis de ambiente configuráveis via ConfigMap/Secret

### 5.2 Service (`k8s/service.yaml`)
- ClusterIP para acesso interno
- NodePort para acesso local
- Load balancing entre replicas

### 5.3 ConfigMap (`k8s/configmap.yaml`)
- Variáveis de aplicação (profile Spring, logging level)
- Configurações não-sensíveis

### 5.4 Secret (`k8s/secret.yaml`)
- Credenciais do banco (usuario/senha em base64)
- JWT secret para autenticação

### 5.5 HPA (`k8s/hpa.yaml`)
- Escalamento automático baseado em CPU (>70%)
- Min: 2 replicas, Max: 5 replicas

---

## 6. Terraform (IaC)

**Localização**: `infra/terraform/`

### 6.1 Local (Kind)
- Manifests: criação de namespace, PVC para dados
- Instruções em: `infra/terraform/README.md`

### 6.2 Cloud (Esqueleto para expansão)
- Referências para EKS (AWS), GKE (GCP), AKS (Azure)
- Estrutura recomendada:
  ```
  infra/terraform/
  ├── main.tf
  ├── variables.tf
  ├── outputs.tf
  └── cloud/
      ├── eks/
      │   ├── main.tf
      │   └── variables.tf
      └── gke/
          ├── main.tf
          └── variables.tf
  ```

---

## 7. Segurança & Autenticação

### 7.1 JWT (JSON Web Token)
- Filter: `JwtAuthenticationFilter` em `src/main/java/br/com/fiap/techchallange/security/`
- Endpoints administrativos protegidos
- Login: `/api/admin/auth/login` (não implementado nesta versão, usar token mock)

### 7.2 CORS
- Configurado em `SecurityConfig`
- Permite requisições de origens específicas

### 7.3 Testes
- Profile `test` desativa autenticação para testes
- `TestSecurityConfig`: Configuração segura para testes

---

## 8. Testes Automatizados

### 8.1 Testes Unitários
- Domain: `OrdemServicoTest` (máquina de estados, regras)
- Services: `AprovarOrcamentoServiceTest`, `RejeitarOrcamentoServiceTest`, etc.

### 8.2 Testes de Integração
- Controllers: `OrdemServicoControllerTest`
- Usa banco H2 em memória
- Executa fluxo completo (HTTP → Service → Repository)

### 8.3 Cobertura
- 136 testes passando
- Cobertura de casos de uso críticos:
  - ✅ Criar OS
  - ✅ Incluir serviço/peça
  - ✅ Gerar orçamento
  - ✅ Aprovar orçamento (PUT, validado)
  - ✅ Rejeitar orçamento (PUT, validado)
  - ✅ Finalizar OS
  - ✅ Entregar OS
  - ✅ Buscar/listar OS

**Executar testes**:
```bash
mvn test
```

---

## 9. Decisões Arquiteturais

### 9.1 Ports & Adapters (Hexagonal)
**Benefício**: 
- Independência de framework (fácil trocar JPA por outro ORM)
- Testabilidade (mockar ports)
- Separação clara de responsabilidades

### 9.2 Máquina de Estados no Domain
**Benefício**:
- Regras de negócio centralizadas
- Validação de transições em um único lugar
- Segurança: impossível ter estado inválido

### 9.3 HTTP Methods Corretos (REST)
- **GET**: Leitura (sem efeitos colaterais)
- **POST**: Criação (novo recurso)
- **PUT**: Atualização (estado do recurso muda)
- ✅ Aprovação de orçamento: `PUT /api/ordem-servico/{id}/aprovar`
- ✅ Rejeição de orçamento: `PUT /api/ordem-servico/{id}/rejeitar`

### 9.4 Services Transacionais
- `@Transactional`: Garante atomicidade (tudo ou nada)
- Rollback automático em exceções

---

## 10. Dependências Principais

- **Spring Boot 3.5.6**: Framework web
- **Spring Data JPA**: Persistência
- **PostgreSQL / H2**: Banco de dados
- **JUnit 5 + Mockito**: Testes
- **OpenAPI 3.0**: Documentação automática

---

## 11. Próximos Passos Recomendados

1. ✅ Implementar validações de transição (FEITO)
2. ✅ Corrigir HTTP methods (FEITO)
3. ✅ Adicionar endpoint de rejeição (FEITO)
4. ⏳ Completar Terraform para cloud (EKS/GKE)
5. ⏳ Implementar autenticação JWT real
6. ⏳ Adicionar audit trail de mudanças
7. ⏳ Melhorar cobertura com Testcontainers (Postgres real)
8. ⏳ Documentação em Postman Collection (exportar OpenAPI)

---

**Versão**: 1.0  
**Data**: 2026-03-11  
**Arquitetor**: Tech Challenge - Fase 2
