# Diagrama de Componentes — Tech Challenge

## 1. Arquitetura de Alto Nível (Visão em Nuvem)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                  CLIENTE                                         │
│                          (Browser / Mobile App)                                  │
└────────────────────────────────────┬────────────────────────────────────────────┘
                                     │
                                     │ HTTPS
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            AWS CLOUD (EKS)                                       │
│                                                                                   │
│  ┌────────────────────────────────────────────────────────────────────────────┐ │
│  │                    Kubernetes Cluster (EKS)                                 │ │
│  │                                                                             │ │
│  │  ┌──────────────────────────────────────────────────────────────────────┐  │ │
│  │  │                   Ingress / Load Balancer                            │  │ │
│  │  │        (AWS Application Load Balancer - ALB)                         │  │ │
│  │  └────────────────────────────┬─────────────────────────────────────────┘  │ │
│  │                               │                                            │ │
│  │                ┌──────────────┴──────────────┐                           │ │
│  │                │                             │                           │ │
│  │  ┌─────────────▼────────┐      ┌──────────────▼──────────┐             │ │
│  │  │  Service: API Gateway│      │  Service: Auth Service  │             │ │
│  │  │  (ClusterIP)         │      │  (NodePort)             │             │ │
│  │  └─────────────┬────────┘      └──────────────┬──────────┘             │ │
│  │                │                              │                         │ │
│  │   ┌────────────▼────────────┐                 │                         │ │
│  │   │   Deployment: App       │                 │                         │ │
│  │   │   (3 Replicas)          │                 │                         │ │
│  │   │                         │                 │                         │ │
│  │   │  ┌─────────────────┐    │                 │                         │ │
│  │   │  │  Pod 1: Spring  │    │                 │                         │ │
│  │   │  │  Boot App       │    │                 │                         │ │
│  │   │  │  Port: 8080     │    │                 │                         │ │
│  │   │  └─────────────────┘    │                 │                         │ │
│  │   │                         │                 │                         │ │
│  │   │  ┌─────────────────┐    │                 │                         │ │
│  │   │  │  Pod 2: Spring  │    │                 │                         │ │
│  │   │  │  Boot App       │    │                 │                         │ │
│  │   │  │  Port: 8080     │    │                 │                         │ │
│  │   │  └─────────────────┘    │                 │                         │ │
│  │   │                         │                 │                         │ │
│  │   │  ┌─────────────────┐    │                 │                         │ │
│  │   │  │  Pod 3: Spring  │    │                 │                         │ │
│  │   │  │  Boot App       │    │                 │                         │ │
│  │   │  │  Port: 8080     │    │                 │                         │ │
│  │   │  └─────────────────┘    │                 │                         │ │
│  │   └──────────┬───────────────┘                 │                         │ │
│  │              │                                 │                         │ │
│  │   ┌──────────▼──────┐  ┌──────────────────┐   │                         │ │
│  │   │ ConfigMap       │  │ Secret           │   │                         │ │
│  │   │                 │  │                  │   │                         │ │
│  │   │ • DB Host       │  │ • DB Password    │   │                         │ │
│  │   │ • DB Port       │  │ • JWT Secret     │   │                         │ │
│  │   │ • Logging Level │  │ • API Keys       │   │                         │ │
│  │   └─────────────────┘  └──────────────────┘   │                         │ │
│  │                                                │                         │ │
│  │  ┌────────────────────────────────────────┐   │                         │ │
│  │  │     HPA (Horizontal Pod Autoscaler)    │   │                         │ │
│  │  │                                        │   │                         │ │
│  │  │  Min: 2 replicas                      │   │                         │ │
│  │  │  Max: 10 replicas                     │   │                         │ │
│  │  │  Métrica: CPU > 70%                   │   │                         │ │
│  │  │  Métrica: Memória > 80%               │   │                         │ │
│  │  └────────────────────────────────────────┘   │                         │ │
│  │                                                │                         │ │
│  └────────────────────────────────────────────────┼─────────────────────────┘ │
│                                                   │                           │
│  ┌───────────────────────────────────────────────▼──────────────────────────┐ │
│  │                     AWS RDS PostgreSQL                                   │ │
│  │                                                                          │ │
│  │  • Multi-AZ (Failover Automático)                                       │ │
│  │  • Backup Automático (7 dias retenção)                                  │ │
│  │  • Port: 5432                                                           │ │
│  │  • Encryption at rest (AWS KMS)                                         │ │
│  │  • Security Group: Accept only from K8s pods                            │ │
│  │                                                                          │
│  │  Databases:                                                             │ │
│  │  • tech_challenge_prod (Produção)                                       │ │
│  │  • tech_challenge_staging (Homolog)                                     │ │
│  │                                                                          │ │
│  └──────────────────────────────────────────────────────────────────────────┘ │
│                                                                                │
└────────────────────────────────────────────────────────────────────────────────┘
         │                                                                    │
         │ HTTPS (Lambda OAuth2)                                             │
         │                                                                    │
         ▼                                                                    │
┌─────────────────────────────────────┐                                      │
│    AWS Lambda (Auth Service)        │                                      │
│                                     │                                      │
│  • Runtime: Node.js 20              │                                      │
│  • Função: Autenticar CPF           │                                      │
│  • Retorna: JWT Token               │                                      │
│  • Timeout: 30s                     │                                      │
│  • Memória: 512MB                   │                                      │
│                                     │                                      │
└─────────────────────────────────────┘                                      │
         │                                                                    │
         └────────────────────────────────────────────────────────────────────┘
                (Lookup de usuários para validação)

```

---

## 2. Componentes Internos da Aplicação

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    APLICAÇÃO SPRING BOOT (8080)                         │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                REST API (Adapters In)                           │   │
│  │                                                                  │   │
│  │  POST   /api/ordens                    → Criar Ordem            │   │
│  │  GET    /api/ordens/{id}               → Buscar Ordem           │   │
│  │  GET    /api/ordens                    → Listar Ordens          │   │
│  │  PUT    /api/ordens/{id}/servicos      → Incluir Serviço        │   │
│  │  PUT    /api/ordens/{id}/pecas         → Incluir Peça           │   │
│  │  POST   /api/ordens/{id}/orcamento     → Gerar Orçamento        │   │
│  │  PUT    /api/ordens/{id}/aprovar       → Aprovar Orçamento      │   │
│  │  PUT    /api/ordens/{id}/rejeitar      → Rejeitar Orçamento     │   │
│  │  PUT    /api/ordens/{id}/finalizar     → Finalizar OS           │   │
│  │  PUT    /api/ordens/{id}/entregar      → Entregar OS            │   │
│  │                                                                  │   │
│  │  GET    /api/clientes                  → Listar Clientes        │   │
│  │  POST   /api/clientes                  → Criar Cliente          │   │
│  │  PUT    /api/clientes/{id}             → Atualizar Cliente      │   │
│  │  DELETE /api/clientes/{id}             → Remover Cliente        │   │
│  │                                                                  │   │
│  │  GET    /swagger-ui.html               → Documentação OpenAPI   │   │
│  │                                                                  │   │
│  └─────────────────────┬──────────────────────────────────────────┘   │
│                        │                                                │
│  ┌─────────────────────▼──────────────────────────────────────────┐   │
│  │          SECURITY LAYER (JwtAuthenticationFilter)              │   │
│  │                                                                  │   │
│  │  • Intercepta requisições HTTP                                 │   │
│  │  • Valida JWT Token                                            │   │
│  │  • Extrai CPF e roles                                          │   │
│  │  • Passa context para controllers                              │   │
│  │                                                                  │   │
│  └─────────────────────┬──────────────────────────────────────────┘   │
│                        │                                                │
│  ┌─────────────────────▼──────────────────────────────────────────┐   │
│  │        APPLICATION LAYER (Services / Use Cases)                │   │
│  │                                                                  │   │
│  │  • CriarOrdemServicoService                                    │   │
│  │  • IncluirServicoNaOSService                                   │   │
│  │  • IncluirPecaNaOSService                                      │   │
│  │  • GerarOrcamentoService                                       │   │
│  │  • AprovarOrcamentoService                                     │   │
│  │  • RejeitarOrcamentoService                                    │   │
│  │  • FinalizarOrdemServicoService                                │   │
│  │  • EntregarOrdemServicoService                                 │   │
│  │  • BuscarOrdemService                                          │   │
│  │  • BuscarClienteService                                        │   │
│  │  • CadastrarClienteService                                     │   │
│  │  • AtualizarClienteService                                     │   │
│  │                                                                  │   │
│  └─────────────────────┬──────────────────────────────────────────┘   │
│                        │                                                │
│  ┌─────────────────────▼──────────────────────────────────────────┐   │
│  │          DOMAIN LAYER (Business Rules & Entities)              │   │
│  │                                                                  │   │
│  │  Aggregates:                                                    │   │
│  │  • OrdemServico (root)                                         │   │
│  │    ├── ItemServico (value object)                              │   │
│  │    ├── ItemPeca (value object)                                 │   │
│  │    ├── StatusOS (state machine)                                │   │
│  │    └── Orcamento (value object)                                │   │
│  │                                                                  │   │
│  │  • Cliente (root)                                              │   │
│  │  • Veiculo (root)                                              │   │
│  │  • Peca (root)                                                 │   │
│  │  • Servico (root)                                              │   │
│  │                                                                  │   │
│  └─────────────────────┬──────────────────────────────────────────┘   │
│                        │                                                │
│  ┌─────────────────────▼──────────────────────────────────────────┐   │
│  │    INFRASTRUCTURE LAYER (Persistence & External Services)      │   │
│  │                                                                  │   │
│  │  JPA Adapters (Adapters Out):                                  │   │
│  │  • JpaOrdemServicoRepositoryAdapter                            │   │
│  │  • JpaClienteRepositoryAdapter                                 │   │
│  │  • JpaVeiculoRepositoryAdapter                                 │   │
│  │  • JpaPecaRepositoryAdapter                                    │   │
│  │  • JpaServicoRepositoryAdapter                                 │   │
│  │                                                                  │   │
│  │  JPA Repositories (Spring Data):                               │   │
│  │  • SpringDataOrdemServicoRepository                            │   │
│  │  • SpringDataClienteRepository                                 │   │
│  │  • SpringDataVeiculoRepository                                 │   │
│  │  • SpringDataPecaRepository                                    │   │
│  │  • SpringDataServicoRepository                                 │   │
│  │                                                                  │   │
│  │  JPA Entities:                                                 │   │
│  │  • OrdemServicoEntity                                          │   │
│  │  • ClienteEntity                                               │   │
│  │  • VeiculoEntity                                               │   │
│  │  • PecaEntity                                                  │   │
│  │  • ServicoEntity                                               │   │
│  │                                                                  │   │
│  └─────────────────────┬──────────────────────────────────────────┘   │
│                        │                                                │
│  ┌─────────────────────▼──────────────────────────────────────────┐   │
│  │      MONITORING & OBSERVABILITY (Port 9090)                    │   │
│  │                                                                  │   │
│  │  • Spring Boot Actuator                                        │   │
│  │  • Prometheus Metrics Exporter                                 │   │
│  │  • Micrometer (abstração de métricas)                          │   │
│  │                                                                  │   │
│  │  Métricas:                                                     │   │
│  │  • HTTP Requests (latência, status, método)                    │   │
│  │  • JVM Metrics (heap, threads, GC)                             │   │
│  │  • Database Connections (HikariCP)                             │   │
│  │  • Business Metrics (ordens criadas, aprovadas, etc)           │   │
│  │                                                                  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
         │
         │ JDBC
         ▼
    ┌──────────────────────┐
    │  PostgreSQL (5432)   │
    │                      │
    │  Database Schema:    │
    │  • ordens            │
    │  • clientes          │
    │  • veiculos          │
    │  • pecas             │
    │  • servicos          │
    │  • itens_servico     │
    │  • itens_peca        │
    │                      │
    └──────────────────────┘
```

---

## 3. Stack de Monitoramento (Fora do K8s - Separado)

```
┌─────────────────────────────────────────────────────────────────────┐
│                   MONITORING STACK (On-Premises)                    │
│                                                                      │
│  ┌──────────────────────┐      ┌──────────────────────┐             │
│  │   Prometheus         │      │   AlertManager       │             │
│  │   (Port: 9090)       │      │   (Port: 9093)       │             │
│  │                      │      │                      │             │
│  │ Scrapes:             │      │ Envia alertas para:  │             │
│  │ • K8s metrics        │◄────►│ • Slack              │             │
│  │ • App metrics        │      │ • PagerDuty          │             │
│  │ • Node exporter      │      │ • Email              │             │
│  │ • Database metrics   │      │                      │             │
│  │                      │      └──────────────────────┘             │
│  └──────────┬───────────┘                                            │
│             │                                                        │
│  ┌──────────▼──────────────────────────────────────┐               │
│  │         Grafana (Port: 3000)                    │               │
│  │                                                  │               │
│  │  Dashboards:                                    │               │
│  │  • Application Health                          │               │
│  │  • HTTP Request Rate & Latency                 │               │
│  │  • JVM Memory & Threads                        │               │
│  │  • Database Connections                        │               │
│  │  • Pod Resource Usage                          │               │
│  │  • Business Metrics                            │               │
│  │                                                  │               │
│  │  Alertas Configurados:                          │               │
│  │  • High Error Rate (> 5%)                       │               │
│  │  • High Latency (> 1s)                         │               │
│  │  • Pod Restart Loop                            │               │
│  │  • Database Connection Issues                  │               │
│  │                                                  │               │
│  └──────────────────────────────────────────────────┘               │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
              ▲
              │ Scrape
              │
        ┌─────┴──────────────────────┐
        │ K8s Prometheus Exporter    │
        │ (ServiceMonitor)           │
        └────────────────────────────┘

```

---

## 4. Fluxo de Deploy End-to-End

```
┌─────────────────────────────────────────────────────────────────────┐
│                     DEVELOPER LOCAL ENVIRONMENT                     │
│                                                                      │
│  git push origin feature/x                                          │
│              │                                                       │
│              ▼                                                       │
└──────────────┬──────────────────────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    GITHUB (Repository)                               │
│                                                                      │
│  Trigger: Push to main / Pull Request                               │
│              │                                                       │
│              ▼                                                       │
└──────────────┬──────────────────────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────────────────────┐
│              GITHUB ACTIONS (CI/CD Pipeline)                         │
│              .github/workflows/ci-cd.yml                             │
│                                                                      │
│  1️⃣ Checkout Code                                                    │
│  2️⃣ Setup JDK 21 (Temurin)                                           │
│  3️⃣ Cache Maven Artifacts                                           │
│  4️⃣ Build Maven (mvn clean package)                                 │
│  5️⃣ Run Unit Tests (JUnit 5)                                        │
│  6️⃣ Run Integration Tests                                           │
│  7️⃣ Build Docker Image                                             │
│  8️⃣ Push to ECR (Amazon Container Registry)                         │
│  9️⃣ Deploy to EKS (kubectl apply -f k8s/)                           │
│  🔟 Health Check (curl /actuator/health)                            │
│                                                                      │
│  Status: ✅ Success / ❌ Failed (Slack notification)                 │
│                                                                      │
└──────────────┬──────────────────────────────────────────────────────┘
               │
               ├─────────────────────────────────┐
               │                                 │
               ▼                                 ▼
        ┌────────────────┐            ┌─────────────────┐
        │  AWS ECR       │            │  EKS Cluster    │
        │  Container     │            │  (Staging)      │
        │  Registry      │            │                 │
        └────────────────┘            │  Pod updated    │
                                      │  with new image │
                                      │                 │
                                      └─────────────────┘

```

---

## 5. Matriz de Responsabilidades (RACI)

| Componente | Dev | DevOps | SRE | Produto |
|---|---|---|---|---|
| Code (Java/Spring) | R, A | | | |
| Dockerfile | R, A | C | | |
| K8s Manifests | C | R, A | | |
| Terraform (IaC) | | R, A | C | |
| Monitoring | | C | R, A | |
| Database Design | C | R, A | | C |
| Security | R, A | C | | |
| API Design | R, A | | | C |

**Legenda**: R = Responsible, A = Accountable, C = Consulted, I = Informed

---

**Versão**: 1.0
