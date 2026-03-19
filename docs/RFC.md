# RFC (Request for Comments) — Tech Challenge

## RFC-001: Escolha da Nuvem (AWS vs GCP vs Azure)

### Status: ACEITO ✅


### Resumo
Avaliação de provedores de nuvem para hospedar o cluster Kubernetes da aplicação de gestão de oficina.

### Contexto
Necessário escolher um provedor de nuvem que ofereça:
- Serviço de Kubernetes gerenciado
- Banco de dados relacional como serviço (RDS/CloudSQL/Azure Database)
- Escalabilidade automática
- Bom custo-benefício
- Suporte à Lambda/Cloud Functions para autenticação

### Opções Avaliadas

#### 1. AWS (Amazon Web Services) ✅ **ESCOLHIDO**
- **Serviço K8s**: EKS (Elastic Kubernetes Service)
- **Banco de Dados**: RDS PostgreSQL (Multi-AZ, backups automáticos)
- **Serverless Auth**: AWS Lambda + API Gateway
- **Monitoramento**: CloudWatch integrado
- **Custo Estimado**: ~$500-800/mês (cluster + database)
- **Vantagens**:
  - Marketplace mais maduro
  - Melhor documentação e comunidade
  - EKS bem integrado com outros serviços AWS
  - RDS com replicação Multi-AZ nativa
  - Suporte a Lambda para autenticação serverless
- **Desvantagens**:
  - Pricing pode ser confuso
  - Requer compreensão de IAM Roles

#### 2. Google Cloud Platform (GCP)
- **Serviço K8s**: GKE (Google Kubernetes Engine)
- **Banco de Dados**: Cloud SQL PostgreSQL
- **Serverless Auth**: Cloud Functions
- **Custo Estimado**: ~$400-700/mês
- **Vantagens**:
  - Pricing mais transparente
  - GKE é o Kubernetes "puro" (mantido pelo Google)
  - Excelente integração com CI/CD (Cloud Build)
- **Desvantagens**:
  - Comunidade menor que AWS
  - Cloud SQL menos conhecido que RDS

#### 3. Microsoft Azure
- **Serviço K8s**: AKS (Azure Kubernetes Service)
- **Banco de Dados**: Azure Database for PostgreSQL
- **Serverless Auth**: Azure Functions
- **Custo Estimado**: ~$450-750/mês
- **Vantagens**:
  - Bom custo-benefício
  - Integração com Microsoft 365
- **Desvantagens**:
  - UI mais complexa
  - Comunidade menor para este uso case

### Decisão
**AWS foi escolhido** pelos seguintes motivos:
1. **Maturidade**: EKS + RDS são soluções estáveis e bem documentadas
2. **Ecossistema**: Lambda funciona perfeitamente para autenticação serverless
3. **Comunidade**: Maior base de conhecimento disponível
4. **Multi-AZ RDS**: Failover automático nativo
5. **Escalabilidade**: Auto-scaling automático em EKS

### Implementação

#### Terraform para Provisionar:
```hcl
# EKS Cluster
resource "aws_eks_cluster" "tech_challenge" {
  name            = "tech-challenge-prod"
  role_arn        = aws_iam_role.eks_role.arn
  vpc_config {
    subnet_ids = aws_subnet.private[*].id
  }
}

# RDS PostgreSQL
resource "aws_db_instance" "tech_challenge_db" {
  identifier       = "tech-challenge-prod"
  engine           = "postgres"
  engine_version   = "15"
  instance_class   = "db.t3.medium"
  allocated_storage = 100
  
  multi_az        = true
  backup_retention_period = 7
  
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
}

# Lambda para Auth
resource "aws_lambda_function" "auth_lambda" {
  filename      = "auth-lambda.zip"
  function_name = "tech-challenge-auth"
  role          = aws_iam_role.lambda_role.arn
  runtime       = "nodejs20.x"
  timeout       = 30
  memory_size   = 512
}
```

### Próximos Passos
- [ ] Criar conta AWS
- [ ] Configurar IAM Roles e Policies
- [ ] Provisionar EKS com Terraform
- [ ] Provisionar RDS com Terraform
- [ ] Testar failover automático
- [ ] Configurar backups automatizados

---

## RFC-002: Estratégia de Autenticação (JWT vs OAuth2 vs SAML)

### Status: ACEITO ✅


### Resumo
Avaliação de estratégias de autenticação para a API da aplicação.

### Requisitos
- Autenticar usuários por CPF
- Gerar token de acesso
- Validação de token em todas as requisições
- Escalável para múltiplos clientes
- Suporte a diferentes papéis (admin, técnico, cliente)

### Opções Avaliadas

#### 1. JWT (JSON Web Token) ✅ **ESCOLHIDO**
- **Definição**: Token auto-contido com payload codificado + assinatura
- **Fluxo**:
  1. POST /auth/login com CPF
  2. Backend valida CPF no RDS
  3. Gera JWT assinado (HS256 ou RS256)
  4. Cliente armazena JWT
  5. Envia JWT no header Authorization para cada requisição
  6. Backend valida assinatura + expiração

- **Vantagens**:
  - Stateless (não precisa de sessão no servidor)
  - Escalável (funciona bem com múltiplos servidores)
  - Suporta diferentes claims (CPF, roles, etc)
  - Simples de implementar
  - Reduz carga no banco de dados

- **Desvantagens**:
  - Revogação é complexa (token continua válido até expiração)
  - Token pode ser interceptado se não usar HTTPS
  - Payload não é criptografado (apenas assinado)

#### 2. OAuth2
- **Definição**: Protocolo de autorização que delega autenticação
- **Cenário**: Útil para integrar com provedores externos (Google, GitHub)
- **Problema**: Caso de uso é apenas CPF interno, OAuth2 é overhead
- **Conclusão**: Não necessário para esta aplicação

#### 3. SAML2
- **Definição**: Standard enterprise para Single Sign-On
- **Cenário**: Útil para integrar com Active Directory corporativo
- **Problema**: Aplicação não é enterprise, overhead complexo
- **Conclusão**: Não necessário para esta aplicação

#### 4. Session Cookies Tradicionais
- **Problema**: Não funciona bem com APIs REST e múltiplos servidores
- **Conclusão**: Rejeitado

### Decisão
**JWT foi escolhido** pelos seguintes motivos:
1. **Stateless**: Perfeito para arquitetura de microsserviços
2. **Escalável**: Funciona com múltiplos pods no Kubernetes
3. **Simples**: Fácil de implementar e manter
4. **Suporta Roles**: Claims customizados para autorização
5. **Compatível com Lambda**: Lambda pode validar JWT antes de encaminhar para K8s

### Implementação

#### Geração de JWT (Lambda):
```javascript
const jwt = require('jsonwebtoken');

async function authenticateUser(cpf, password) {
  // 1. Validar CPF no RDS
  const user = await db.query(
    'SELECT * FROM usuarios WHERE cpf = $1',
    [cpf]
  );
  
  if (!user) return { error: 'CPF não encontrado' };
  
  // 2. Validar senha (bcrypt)
  const isPasswordValid = await bcrypt.compare(password, user.senha_hash);
  if (!isPasswordValid) return { error: 'Senha incorreta' };
  
  // 3. Gerar JWT
  const token = jwt.sign(
    {
      cpf: user.cpf,
      userId: user.id,
      roles: user.roles,  // ['admin', 'technician']
    },
    process.env.JWT_SECRET,
    { expiresIn: '1h' }
  );
  
  return {
    accessToken: token,
    expiresIn: 3600,
    tokenType: 'Bearer',
  };
}
```

#### Validação de JWT (Spring Boot):
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
  @Override
  protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response,
                                  FilterChain filterChain) 
      throws ServletException, IOException {
    
    String header = request.getHeader("Authorization");
    
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    
    String token = header.substring(7);
    
    try {
      // Validar assinatura e expiração
      Claims claims = Jwts.parser()
          .setSigningKey(jwtSecret.getBytes())
          .parseClaimsJws(token)
          .getBody();
      
      String cpf = claims.getSubject();
      String roles = claims.get("roles", String.class);
      
      // Criar Authentication
      List<GrantedAuthority> authorities = 
          AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
      
      Authentication auth = new UsernamePasswordAuthenticationToken(
          cpf, null, authorities
      );
      
      SecurityContextHolder.getContext().setAuthentication(auth);
      
    } catch (JwtException | IllegalArgumentException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    
    filterChain.doFilter(request, response);
  }
}
```

#### Proteção de Endpoints:
```java
@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoController {
  
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
  public ResponseEntity<OrdemServicoResponse> criarOrdem(
      @RequestBody CriarOrdemRequest request) {
    // Lógica...
  }
}
```

### Tempo de Expiração
- **Curto prazo**: 1 hora
- **Refresh Token**: Opcional, 7 dias (para renovar sem fazer login novamente)

### Segurança
- ✅ HTTPS obrigatório (encriptação em trânsito)
- ✅ JWT assinado (RS256 em produção)
- ✅ Validação de expiração
- ✅ Validação de assinatura

### Próximos Passos
- [ ] Implementar refresh token (opcional)
- [ ] Implementar logout (revogação via blacklist)
- [ ] Adicionar suporte a 2FA (autenticação de dois fatores)
- [ ] Implementar audit trail de logins

---

## RFC-003: Estratégia de Banco de Dados (PostgreSQL vs MySQL vs NoSQL)

### Status: ACEITO ✅


### Resumo
Avaliação de tecnologias de banco de dados para persistência.

### Requisitos
- Dados relacionais (clientes, veículos, ordens)
- Transações ACID (garantir consistência)
- Consultas complexas (JOINs, aggregations)
- Escalabilidade leitura/escrita
- Backup automático
- Replicação para alta disponibilidade

### Opções Avaliadas

#### 1. PostgreSQL ✅ **ESCOLHIDO**
- **Engine**: Open Source SQL
- **Vantagens**:
  - Excelente suporte a relacionamentos
  - ACID completo (transações seguras)
  - Sem licença de uso
  - Ótimo com Spring Data JPA
  - RDS PostgreSQL no AWS é estável
  - Suporta JSON nativo (se necessário)
  - Window functions, Common Table Expressions (CTEs)

- **Caso de Uso**: Perfeito para dados estruturados com múltiplas entidades relacionadas

#### 2. MySQL
- **Problema**: Menos recursos avançados que PostgreSQL
- **Problema**: Spring Data JPA funciona, mas PostgreSQL é melhor

#### 3. MongoDB (NoSQL)
- **Problema**: Dados são relacionais (clientes ↔ ordens ↔ veículos)
- **Problema**: Transações distribuídas são complexas
- **Conclusão**: NoSQL não é apropriado

#### 4. DynamoDB (NoSQL AWS)
- **Problema**: Modelo de dados não é key-value simples
- **Problema**: Sem suporte a JOINs nativos
- **Conclusão**: Rejeitado

### Decisão
**PostgreSQL foi escolhido** pelos seguintes motivos:
1. **Modelo de Dados**: Perfeitamente relacional
2. **ACID**: Garantia de consistência em transações
3. **JPA**: Spring Data JPA + Hibernate funciona impecavelmente
4. **RDS**: AWS RDS PostgreSQL é estável e escalável
5. **Sem Licença**: Open source, sem custos de licença
6. **Comunidade**: Excelente comunidade e documentação

### Modelo de Dados

Ver documento separado: `BANCO_DE_DADOS.md`

### Configuração RDS (Terraform)

```hcl
resource "aws_db_instance" "postgres" {
  identifier                  = "tech-challenge-db"
  engine                      = "postgres"
  engine_version              = "15.3"
  instance_class              = "db.t3.medium"
  allocated_storage            = 100
  max_allocated_storage        = 500  # Auto-scaling
  
  # Multi-AZ para alta disponibilidade
  multi_az                     = true
  
  # Backups
  backup_retention_period      = 7
  backup_window                = "03:00-04:00"
  
  # Segurança
  publicly_accessible          = false
  skip_final_snapshot          = false
  final_snapshot_identifier    = "tech-challenge-snapshot"
  
  # Encryption
  storage_encrypted            = true
  kms_key_id                   = aws_kms_key.db.arn
  
  # Subnet Group
  db_subnet_group_name         = aws_db_subnet_group.private.name
  vpc_security_group_ids       = [aws_security_group.rds.id]
}
```

### Configuração Spring Boot

```yaml
# application.properties
spring.datasource.url=jdbc:postgresql://tech-challenge-db.xxxxx.amazonaws.com:5432/tech_challenge
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Connection Pooling (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### Performance & Indexação

```sql
-- Índices para Queries críticas
CREATE INDEX idx_ordens_cpf ON ordens(cpf);
CREATE INDEX idx_ordens_status ON ordens(status);
CREATE INDEX idx_ordens_data_abertura ON ordens(data_abertura DESC);
CREATE INDEX idx_clientes_cpf ON clientes(cpf UNIQUE);
CREATE INDEX idx_veiculos_cliente ON veiculos(cliente_id);
CREATE INDEX idx_itens_servico_ordem ON itens_servico(ordem_id);
CREATE INDEX idx_itens_peca_ordem ON itens_peca(ordem_id);
```

### Backup & Disaster Recovery

```bash
# Backup manual
pg_dump -h tech-challenge-db.xxxxx.amazonaws.com \
        -U postgres \
        -d tech_challenge \
        > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore
psql -h localhost -U postgres -d tech_challenge < backup.sql
```

### Próximos Passos
- [ ] Configurar backups automatizados no S3
- [ ] Testar disaster recovery
- [ ] Implementar read replicas (se necessário)
- [ ] Monitorar slow queries
- [ ] Otimizar índices baseado em uso real

---

## RFC-004: Estratégia de Logging e Observabilidade

### Status: ACEITO ✅


### Resumo
Estratégia centralizada de logging, métricas e tracing para observabilidade completa.

### Stack Escolhido

#### 1. Logging: Spring Boot + Logback
```yaml
logging.level.root=INFO
logging.level.br.com.fiap.techchallange=DEBUG
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=/var/log/app/application.log
logging.file.max-size=10MB
logging.file.max-history=30
```

#### 2. Métricas: Prometheus + Micrometer
```java
@Component
public class OrderMetrics {
  private final MeterRegistry meterRegistry;
  
  public OrderMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }
  
  public void recordOrderCreated() {
    Counter.builder("orders.created")
        .description("Total de ordens criadas")
        .register(meterRegistry)
        .increment();
  }
}
```

#### 3. Tracing: Spring Cloud Sleuth + Jaeger (futuro)

#### 4. Visualização: Grafana + Prometheus

### Próximos Passos
- [ ] Implementar ELK Stack (Elasticsearch, Logstash, Kibana) para logs centralizados
- [ ] Integrar Jaeger para distributed tracing
- [ ] Criar dashboards customizados no Grafana

---

## Matriz de Decisão

| RFC | Decisão | Status | Data | Revisor |
|---|---|---|---|---|
| RFC-001 | AWS (EKS + RDS) | ✅ Aceito | 2026-03-18 | Eu |
| RFC-002 | JWT (1h expiration) | ✅ Aceito | 2026-03-18 | Eu |
| RFC-003 | PostgreSQL 15 | ✅ Aceito | 2026-03-18 | Eu |
| RFC-004 | Prometheus + Grafana | ✅ Aceito | 2026-03-18 | Eu |

---

**Versão**: 1.0  
**Data**: 2026-03-18  
**Autores**: Eu  
**Próxima Revisão**: 2026-06-18

