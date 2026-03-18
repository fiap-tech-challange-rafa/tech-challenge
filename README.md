# Tech Challenge — Fase 2 (Evolução)

Este repositório contém a evolução da aplicação da Fase 1 para a Fase 2 do POS TECX Tech Challenge. O objetivo desta fase é tornar a aplicação mais resiliente, testável e escalável, aplicando arquitetura hexagonal, testes automatizados, containerização, manifestos Kubernetes, IaC (Terraform) e CI/CD.

---

## 1. Descrição da solução e objetivos

Solução: uma API REST para gestão de oficina (clientes, veículos, serviços, peças e ordens de serviço) refatorada com Arquitetura Hexagonal (Ports & Adapters). A aplicação expõe APIs para criar e acompanhar Ordens de Serviço (OS), gerar e aprovar orçamentos, realizar serviços, controlar peças e registrar entrega.

Principais objetivos atendidos nesta fase:
- Aplicar Clean Code e Arquitetura Hexagonal (separação clara entre domain, application e adapters).
- Implementar ports (interfaces) e adapters (JPA, controllers) para facilitar testes e manutenção.
- Cobertura de testes automatizados (unitários e integração) para fluxos críticos.
- Containerização via Docker e execução local via docker-compose.
- Manifestos Kubernetes (Deployment, Service, ConfigMap, Secret, HPA) para orquestração.
- Skeleton de Terraform para provisionamento (local/cloud) e pipeline CI/CD (GitHub Actions) para build/test/deploy.

---

## 2. Desenho da arquitetura proposta

Arquitetura: Hexagonal (Ports & Adapters)

**[→ Visualizar documento técnico completo: ARCHITECTURE.md](./ARCHITECTURE.md)**

- Controllers (adapters in) → Ports (application.port.in) → Application Services (casos de uso) → Ports (application.port.out) → Repositories / Adapters out (JPA, external clients)
- Domain (pure business rules) no centro (entidades e regras de negócio).

Diagrama (Mermaid):

```mermaid
flowchart LR
  subgraph Adapter_In
    Controller["REST Controllers\n(api/ordem-servico, api/admin/...)"]
  end
  subgraph Application
    PortsIn["Ports (in)\nCriarOrdem, BuscarOrdem,..."]
    Services["Use-Cases / Services"]
    PortsOut["Ports (out)\nRepositoryPort, NotifierPort"]
  end
  subgraph Domain
    Domain["Entities & Rules\nOrdemServico, ItemServico, ItemPeca..."]
  end
  subgraph Adapter_Out
    Jpa["JPA Adapter\n(OrdemServicoEntity, SpringData) "]
    Email["Email/Webhook Adapter"]
  end

  Controller -->|chama| PortsIn
  PortsIn --> Services
  Services --> Domain
  Services -->|persiste| PortsOut
  PortsOut --> Jpa
  PortsOut --> Email
```

Componentes da aplicação
- Domain: entidades e regras (src/main/java/.../domain).
- Application: serviços e ports (src/main/java/.../application).
- Interfaces: controllers e DTOs (src/main/java/.../interfaces).
- Infrastructure: JPA entities, adapters, configuração externa (src/main/java/.../infrastructure).
- Security: JWT filter e configuração (src/main/java/.../security).

---

## 3. Infraestrutura provisionada (itens entregues)

- Dockerfile (raiz) — multi-stage build para a aplicação.
- docker-compose.yml — ambiente local (app + banco).
- k8s/ — manifests Kubernetes:
  - `deployment.yaml` (Deployment)
  - `service.yaml` (Service)
  - `configmap.yaml` (ConfigMap)
  - `secret.yaml` (Secret skeleton)
  - `hpa.yaml` (Horizontal Pod Autoscaler)
- infra/terraform/ — README e skeleton com instruções locais (kind) e recomendações cloud (EKS/GKE/AKS).
- .github/workflows/ci-cd.yml — pipeline skeleton (build, test, docker build, aplicar manifests opcional).

---

## 4. Fluxo de deploy

1. CI (GitHub Actions) executa build do projeto e testes automatizados.
2. Image Docker é construída e opcionalmente push para registry.
3. Em deploy automático/manual, manifestos em `k8s/` são aplicados ao cluster (kubectl apply -f k8s/).
4. HPA ajusta réplicas automaticamente conforme métricas (por ex. CPU).

---

## 5. Como executar localmente

Pré-requisitos mínimos:
- Java 17+ ou 21 (conforme seu ambiente), Maven, Docker e Docker Compose.

1) Build local (opcional):

```bash
mvn -B -DskipTests package
```

2) Rodar via Docker Compose (app + DB):

```bash
docker-compose up --build
```

A API ficará disponível em http://localhost:8080

Observações de profile:
- O projeto tem um profile `docker` e perfis de ambiente (ver `src/main/resources`).

---

## 6. Deploy em Kubernetes (local com kind/minikube)

Comandos resumidos:

```bash
# criar cluster local com kind
kind create cluster --name tech-challenge

# construir imagem e carregar no cluster kind
docker build -t tech-challenge:latest .
kind load docker-image tech-challenge:latest --name tech-challenge

# aplicar manifests
kubectl apply -f k8s/

# opcional: expor porta para acesso local
kubectl port-forward svc/tech-challenge 8080:8080
```

Notas:
- Use `ConfigMap` e `Secret` para configurar propriedades e segredos no cluster.
- Em cloud, substituir o Service por LoadBalancer/Ingress conforme provedor.

---

## 7. Provisionamento com Terraform

Local (recomendado para desenvolvimento):
- Use `kind` para criar cluster local; o diretório `infra/terraform/` traz um README com instruções.

Cloud (esqueleto):
- Para produção, crie módulos Terraform para EKS/GKE/AKS e bancos gerenciados (RDS/Cloud SQL). Não inclua credenciais no repositório — use variáveis/arquivos externos.

---

## 8. APIs, documentação e collection

- OpenAPI/Swagger: a aplicação expõe documentação automática (quando executada) via `/swagger-ui.html` ou `/swagger-ui/index.html`.
- Postman / Collection: inclua a collection exportada no repositório ou substitua o placeholder abaixo.

Postman Collection (placeholder): https://link-para-collection-exemplo

---

## 9. CI/CD

O workflow em `.github/workflows/ci-cd.yml` tem etapas para:
- Checkout do código, setup JDK
- Build e execução de testes (maven)
- Build da imagem Docker (sem push por padrão)
- Etapa opcional de deploy que aplica manifestos k8s quando `KUBE_CONFIG` estiver disponível como secret

---

## 10. Testes

- Testes unitários e de integração podem ser executados com:

```bash
./mvnw test
```

- Nos testes a segurança é suavizada pelo `TestSecurityConfig` (profile `test`) para permitir execução das rotas administrativas sem necessidade de JWT. Em ambiente real, use autenticação e tokens.

---

## 11. Próximos passos recomendados

- Melhorar cobertura de testes de integração usando Testcontainers (Postgres) na pipeline.
- Automatizar push de imagens para um registry e configurar deploy automático no cluster.
- Completar módulos Terraform para provedor cloud escolhido (EKS/GKE/AKS) e banco gerenciado.
- Gerar e versionar Postman collection e vídeo demonstrativo.

---

## 12. Melhorias Implementadas (Fase 2 - Revisão)

### 12.1 Validações de Transição de Estado
- ✅ Adicionado validação de pré-condições nos métodos `aprovarOrcamento()`, `rejeitarOrcamento()`, `finalizar()` e `entregar()` em `OrdemServico.java`
- ✅ Lança `IllegalStateException` se a transição não for válida
- ✅ Exemplos:
  - Só pode aprovar se status == `AGUARDANDO_APROVACAO`
  - Só pode rejeitar se status == `AGUARDANDO_APROVACAO`
  - Só pode finalizar se status == `EM_EXECUCAO`
  - Só pode entregar se status == `FINALIZADA`

### 12.2 HTTP Methods Corretos (REST)
- ✅ Endpoint de aprovação: `@PutMapping("/{id}/aprovar")` (era `@GetMapping`)
- ✅ Endpoint de rejeição (novo): `@PutMapping("/{id}/rejeitar")`
- ✅ Resposta melhorada: retorna `OrdemServicoResponse` completo em vez de string

### 12.3 Novo Endpoint de Rejeição
- ✅ Port: `RejeitarOrcamentoPort` 
- ✅ Service: `RejeitarOrcamentoService`
- ✅ Controller: `PUT /api/ordem-servico/{id}/rejeitar`
- ✅ Testes: `RejeitarOrcamentoServiceTest` com 3 casos de teste

### 12.4 Testes Corrigidos
- ✅ `AprovarOrcamentoServiceTest`: Coloca OS em `AGUARDANDO_APROVACAO` antes de testar
- ✅ `FinalizarOrdemServicoServiceTest`: Coloca OS em `EM_EXECUCAO` antes de testar
- ✅ `EntregarOrdemServicoServiceTest`: Coloca OS em `FINALIZADA` antes de testar
- ✅ `OrdemServicoTest`: Ajustado fluxo de transições
- ✅ `OrdemServicoControllerTest`: Cria OS com estado correto para cada operação
- ✅ `VeiculoControllerTest`: Limpa OrdenServicos antes de remover veículos (evita constraint violation)

### 12.5 Documentação Técnica
- ✅ Arquivo `ARCHITECTURE.md` com:
  - Diagrama de arquitetura hexagonal
  - Máquina de estados com transições válidas
  - Fluxo de deploy end-to-end
  - Detalhamento de Kubernetes manifests
  - Decisões arquiteturais
  - Próximos passos

---

Se quiser, eu posso:

---

Se quiser, eu posso:
- Gerar o `postman_collection.json` automaticamente a partir do OpenAPI (exigirá executar a aplicação localmente para exportar a spec), e adicionar ao repositório.
- Atualizar o README com o link do vídeo e da collection assim que você me fornecer os links.

<!-- Fim do README -->
