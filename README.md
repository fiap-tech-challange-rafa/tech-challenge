# Tech Challenge

Esse repositório é a aplicação principal de uma plataforma de gestão de oficina. Criei uma arquitetura bem pensada, escalável e pronta pra produção, separando responsabilidades em 4 repositórios diferentes.

---

## O que criei

Basicamente, construí uma plataforma completa de gestão de oficina com:

- **Autenticação serverless** (Lambda + Node.js) — valida CPF e gera token JWT
- **Aplicação principal** (Spring Boot) — expõe as APIs protegidas
- **Infraestrutura em Kubernetes** (EKS) — tudo escalável e resiliente
- **Database gerenciado** (RDS PostgreSQL) — Multi-AZ, backups automáticos, tudo securizado
- **Monitoramento** (Prometheus + Grafana) — pra saber o que tá acontecendo em tempo real

---

## Os 4 Repositórios

| Nome | O que faz |
|------|-----------|
| **auth-lambda** | Autentica usuários via CPF e gera JWT |
| **infra-kubernetes** | Provisiona cluster EKS com Terraform |
| **infra-database** | Cria RDS PostgreSQL com Terraform |
| **tech-challenge-app** | Aplicação principal em Spring Boot (este repositório) |

---

## Arquitetura Rápida

```
Cliente
   │
   ├─ POST /auth/authenticate (CPF)
   │  └─ Lambda (auth-lambda) → RDS lookup → JWT
   │
   └─ GET /api/ordens (com JWT)
      └─ Kubernetes (EKS)
         ├─ Spring Boot app (3 replicas)
         ├─ Auto-scaling (2-10 pods)
         └─ RDS PostgreSQL
```

A separação de responsabilidades foi feita usando Arquitetura Hexagonal. Regras de negócio no center, tudo else é plugável.

---

## Como rodar localmente

### Pre-requisitos
- Java 21
- Maven
- Docker + Docker Compose

### Opção 1: Docker Compose (mais fácil)

```bash
docker-compose up --build
```

Depois acessa:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Metrics: http://localhost:8080/actuator/prometheus

### Opção 2: Build manual

```bash
mvn clean package -DskipTests
java -jar target/tech-challenge-0.0.1-SNAPSHOT.jar
```

### Opção 3: Kubernetes local (kind)

```bash
kind create cluster --name tech-challenge
docker build -t tech-challenge:latest .
kind load docker-image tech-challenge:latest --name tech-challenge
kubectl apply -f k8s/
kubectl port-forward svc/tech-challenge-service 8080:8080
```

---

## Os Manifests do Kubernetes

Tá tudo em `k8s/`:
- `namespace.yaml` — cria namespace isolado
- `configmap.yaml` — configurações da app
- `secret.yaml` — credenciais (senha DB, JWT secret, etc)
- `deployment.yaml` — 3 replicas, com health checks
- `service.yaml` — expõe a app via LoadBalancer
- `ingress.yaml` — Nginx pra rotear requisições
- `hpa.yaml` — auto-scaling automático (2-10 pods)
- `rbac.yaml` — permissões dentro do K8s
- `servicemonitor.yaml` — Prometheus coleta métricas da app

---

## CI/CD

Usei GitHub Actions pra automatizar tudo:

1. **build-test.yml** — roda quando você faz push
   - Build da app
   - Executa testes
   - Análise de código

2. **build-push-docker.yml** — quando passa na main/develop
   - Faz build da imagem Docker
   - Push pro registry

3. **deploy-k8s.yml** — deploy automático
   - Aplica os manifests no cluster
   - Verifica se rolou tudo certo

---

## As APIs

### Autenticação

```bash
POST /auth/authenticate
{
  "cpf": "12345678901",
  "clientId": 123
}

Response:
{
  "token": "eyJhbGc...",
  "expiresIn": 3600,
  "type": "Bearer"
}
```

### Ordens (protegidas com JWT)

```bash
# Listar
GET /api/ordens-servico
Authorization: Bearer <seu-token>

# Criar
POST /api/ordens-servico
{
  "clienteId": 123,
  "veiculoId": 456,
  "descricaoProblema": "Pneu furado"
}

# Atualizar status
PUT /api/ordens-servico/{id}/aprovar
PUT /api/ordens-servico/{id}/rejeitar
```

Tá tudo documentado no Swagger — é só acessar http://localhost:8080/swagger-ui.html

---

## Testes

```bash
mvn test
```

Implementei testes unitários, de integração, validação de transições de estado e todo o necessário para garantir a qualidade da aplicação.

---

## Pontos Relevantes da Arquitetura

- **Separação clara de responsabilidades** — 4 repositórios, cada um com um propósito específico
- **Autenticação serverless** — escalabilidade automática sem provisionar infraestrutura
- **Infrastructure as Code** — toda a infraestrutura versionada via Terraform
- **Kubernetes production-ready** — manifestos prontos para produção
- **Monitoramento desde o início** — Prometheus + Grafana pré-configurado
- **Segurança em camadas** — JWT, VPC privada, KMS, IAM roles, RBAC
- **Escalabilidade automática** — HPA para pods + RDS scaling
- **Documentação técnica** — RFCs, ADRs, diagramas de componentes e sequência

---

## Para colocar em produção

1. Crie os 4 repositórios no GitHub
2. Configure as credenciais da AWS
3. Execute os scripts Terraform (banco de dados primeiro, depois Kubernetes)
4. Faça deploy do Lambda
5. Faça deploy da aplicação
6. Monitore tudo no Grafana

Para dúvidas específicas, consulte os repositórios complementares (`infra-kubernetes`, `infra-database`, `auth-lambda`) que contêm seus próprios READMEs.

---

## Stack Tecnológico

- Java 21 + Spring Boot 3.5.6
- PostgreSQL 15 (RDS)
- Redis (cache)
- Docker + Kubernetes
- Terraform
- GitHub Actions
- Prometheus + Grafana
- AWS (Lambda, EKS, RDS, VPC...)

---

## Documentação Técnica Completa

Para compreender melhor a solução:

- **[ARCHITECTURE.md](./ARCHITECTURE.md)** — Visão geral da arquitetura Hexagonal, componentes e fluxos
- **[docs/DIAGRAMA_COMPONENTES.md](./docs/DIAGRAMA_COMPONENTES.md)** — Diagrama de componentes com visão de nuvem, APIs, banco e monitoramento
- **[docs/DIAGRAMA_SEQUENCIA.md](./docs/DIAGRAMA_SEQUENCIA.md)** — Diagramas de sequência para autenticação e fluxos principais
- **[docs/RFC.md](./docs/RFC.md)** — Request for Comments com decisões técnicas (nuvem, autenticação, banco de dados)
- **[docs/ADR.md](./docs/ADR.md)** — Architecture Decision Records com decisões arquiteturais permanentes
- **[docs/BANCO_DE_DADOS.md](./docs/BANCO_DE_DADOS.md)** — Justificativa, diagrama ER, relacionamentos e schema SQL completo

Toda a documentação de API está disponível no Swagger: http://localhost:8080/swagger-ui.html

---

**Versão**: 1.0  
**Status**: Pronto para produção
