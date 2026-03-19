# ADR (Architecture Decision Records) — Tech Challenge

## ADR-001: Arquitetura Hexagonal (Ports & Adapters)

### Status: ACEITO (Síncrono) ✅

### Contexto
Comunicação entre componentes
- Seja independente de frameworks
- Facilite testes unitários
- Separe claramente domínio de negócio da infraestrutura
- Permita trocar implementações (ex: JPA por MongoDB)

### Decisão
Usar **Arquitetura Hexagonal (Ports & Adapters)** proposta por Alistair Cockburn.

### Implementação

```
┌─────────────────────────────────────────────────┐
│           Adapters In (Controllers)             │
│          (REST API, CLI, mensagens)             │
└─────────────────────────────────────────────────┘
                        ▲
                        │
        ┌───────────────┴───────────────┐
        │                               │
    ┌───▼──────┐               ┌───────▼───┐
    │  Ports   │               │  Ports    │
    │   In     │               │   Out     │
    └───┬──────┘               └───┬───────┘
        │                           │
        │  CriarOrdemPort           │  RepositoryPort
        │  AprovarOrcamentoPort     │  NotificationPort
        │  FinalizarOrdemPort       │
        │                           │
        └───────────────┬───────────┘
                        │
            ┌───────────▼───────────┐
            │   Application Layer   │
            │   (Use Cases/Services)│
            │                       │
            │  CriarOrdemService    │
            │  AprovarOrcamento     │
            │  FinalizarOrdem       │
            └───────────┬───────────┘
                        │
            ┌───────────▼───────────┐
            │    Domain Layer       │
            │   (Business Rules)    │
            │                       │
            │  OrdemServico         │
            │  StatusOS             │
            │  Regras de Negócio    │
            └───────────┬───────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
    ┌───▼──────────────┐    ┌──────────▼────┐
    │  JPA Adapter     │    │  Email Adapter │
    │  (Implementa     │    │  (Implementa   │
    │   RepositoryPort)    NotificationPort)
    │                      │
    └───┬──────────────┘    └──────────┬────┘
        │                             │
    ┌───▼──────┐                 ┌───▼────┐
    │PostgreSQL│                 │SendGrid│
    └──────────┘                 └────────┘
```

### Estrutura de Pastas
```
src/main/java/br/com/fiap/techchallange/
├── domain/                      # 🎯 Núcleo (sem dependências)
│   ├── entities/
│   │   ├── OrdemServico.java
│   │   ├── Cliente.java
│   │   └── ...
│   └── valueobjects/
│       ├── StatusOS.java
│       ├── Orcamento.java
│       └── ...
│
├── application/                 # 📋 Casos de Uso
│   ├── port/
│   │   ├── in/
│   │   │   ├── CriarOrdemServicoPort.java
│   │   │   ├── AprovarOrcamentoPort.java
│   │   │   └── ...
│   │   └── out/
│   │       ├── OrdemServicoRepositoryPort.java
│   │       ├── NotificationPort.java
│   │       └── ...
│   │
│   └── service/
│       ├── CriarOrdemServicoService.java
│       ├── AprovarOrcamentoService.java
│       └── ...
│
├── interfaces/                  # 🔌 Adapters In
│   ├── rest/
│   │   ├── OrdemServicoController.java
│   │   ├── ClienteController.java
│   │   └── ...
│   ├── dto/
│   │   ├── CriarOrdemRequest.java
│   │   ├── OrdemServicoResponse.java
│   │   └── ...
│   └── ...
│
└── infrastructure/              # 🔌 Adapters Out
    ├── persistence/
    │   ├── adapter/
    │   │   └── JpaOrdemServicoRepositoryAdapter.java
    │   ├── repository/
    │   │   └── SpringDataOrdemServicoRepository.java
    │   └── entity/
    │       ├── OrdemServicoEntity.java
    │       ├── ClienteEntity.java
    │       └── ...
    │
    ├── notification/
    │   └── adapter/
    │       └── SendGridEmailNotificationAdapter.java
    │
    └── ...
```

### Benefícios
1. ✅ **Independência de Framework**: Domain não conhece Spring, JPA, etc
2. ✅ **Testabilidade**: Fácil fazer testes unitários com mocks
3. ✅ **Flexibilidade**: Trocar JPA por MongoDB sem alterar domínio
4. ✅ **Separação de Responsabilidades**: Claro onde cada coisa fica
5. ✅ **Comunicação**: Ports deixam claro os contracts

### Exemplo de Implementação

**Port de Entrada**:
```java
// application/port/in/CriarOrdemServicoPort.java
public interface CriarOrdemServicoPort {
  OrdemServico criarOrdem(String cpf, String descricao, 
                          String clienteId, String veiculoId);
}
```

**Service (Implementa Port)**:
```java
// application/service/CriarOrdemServicoService.java
@Service
public class CriarOrdemServicoService implements CriarOrdemServicoPort {
  
  private final OrdemServicoRepositoryPort repository;
  
  @Override
  public OrdemServico criarOrdem(String cpf, String descricao, 
                                 String clienteId, String veiculoId) {
    // Lógica de negócio (sem detalhes de persistência)
    OrdemServico ordem = new OrdemServico(cpf, descricao, 
                                          clienteId, veiculoId);
    repository.salvar(ordem);
    return ordem;
  }
}
```

**Port de Saída**:
```java
// application/port/out/OrdemServicoRepositoryPort.java
public interface OrdemServicoRepositoryPort {
  void salvar(OrdemServico ordem);
  OrdemServico buscarPorId(String id);
  List<OrdemServico> listarTodas();
}
```

**Adapter (Implementa Port)**:
```java
// infrastructure/persistence/adapter/JpaOrdemServicoRepositoryAdapter.java
@Component
public class JpaOrdemServicoRepositoryAdapter 
    implements OrdemServicoRepositoryPort {
  
  private final SpringDataOrdemServicoRepository springDataRepo;
  
  @Override
  public void salvar(OrdemServico ordem) {
    OrdemServicoEntity entity = toEntity(ordem);
    springDataRepo.save(entity);
  }
  
  @Override
  public OrdemServico buscarPorId(String id) {
    OrdemServicoEntity entity = springDataRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException());
    return toModel(entity);
  }
  
  private OrdemServicoEntity toEntity(OrdemServico modelo) {
    // Conversão model → entity
  }
  
  private OrdemServico toModel(OrdemServicoEntity entity) {
    // Conversão entity → model
  }
}
```

**Controller (Adapter In)**:
```java
// interfaces/rest/OrdemServicoController.java
@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoController {
  
  private final CriarOrdemServicoPort criarOrdemPort;
  
  @PostMapping
  public ResponseEntity<OrdemServicoResponse> criar(
      @RequestBody CriarOrdemRequest request) {
    
    OrdemServico ordem = criarOrdemPort.criarOrdem(
        getCpfFromContext(),
        request.getDescricao(),
        request.getClienteId(),
        request.getVeiculoId()
    );
    
    return ResponseEntity
        .created(URI.create("/api/ordens/" + ordem.getId()))
        .body(toResponse(ordem));
  }
  
  private OrdemServicoResponse toResponse(OrdemServico ordem) {
    // Conversão model → response DTO
  }
}
```

### Testes (Benefício Principal)

```java
@Test
public void testCriarOrdemComSucesso() {
  // Arrange
  OrdemServicoRepositoryPort repositoryMock = 
      mock(OrdemServicoRepositoryPort.class);
  
  CriarOrdemServicoService service = 
      new CriarOrdemServicoService(repositoryMock);
  
  // Act
  OrdemServico resultado = service.criarOrdem(
      "12345678900", "Diagnóstico", "CLI-001", "VEI-001"
  );
  
  // Assert
  assertThat(resultado.getStatus()).isEqualTo(StatusOS.RECEBIDA);
  verify(repositoryMock, times(1)).salvar(resultado);
}
```

### Próximos Passos
- [x] Implementar estrutura base
- [x] Criar ports de entrada
- [x] Criar ports de saída
- [ ] Adicionar mais adapters (Email, SMS)
- [ ] Melhorar cobertura de testes

---

## ADR-002: Máquina de Estados no Domain

### Status: ACEITO ✅


### Contexto
A Ordem de Serviço passa por múltiplos estados (RECEBIDA → EM_DIAGNOSTICO → AGUARDANDO_APROVACAO → EM_EXECUCAO → FINALIZADA → ENTREGUE).

É necessário garantir que:
- Transições inválidas sejam impossíveis
- Regras de negócio sejam centralizadas
- Estado seja sempre válido

### Decisão
Implementar **Máquina de Estados (State Pattern)** no Domain Layer.

### Implementação

**Estado como Enum**:
```java
// domain/valueobjects/StatusOS.java
public enum StatusOS {
  RECEBIDA,
  EM_DIAGNOSTICO,
  AGUARDANDO_APROVACAO,
  EM_EXECUCAO,
  FINALIZADA,
  ENTREGUE,
  CANCELADA
}
```

**Regras de Transição no Domain**:
```java
// domain/entities/OrdemServico.java
public class OrdemServico {
  
  private String id;
  private StatusOS status;
  private LocalDateTime dataAbertura;
  private LocalDateTime dataFinalizacao;
  private LocalDateTime dataEntrega;
  private String motivoCancelamento;
  
  // Private constructor (apenas factory methods criam)
  private OrdemServico(String cpf, String descricao, 
                       String clienteId, String veiculoId) {
    this.id = UUID.randomUUID().toString();
    this.status = StatusOS.RECEBIDA;
    this.dataAbertura = LocalDateTime.now();
    // ... outros atributos
  }
  
  // Factory Method
  public static OrdemServico criar(String cpf, String descricao, 
                                    String clienteId, String veiculoId) {
    return new OrdemServico(cpf, descricao, clienteId, veiculoId);
  }
  
  // Transições de Estado
  public void incluirServico(Servico servico) {
    if (status != StatusOS.RECEBIDA && status != StatusOS.EM_DIAGNOSTICO) {
      throw new IllegalStateException(
          String.format("Não é possível incluir serviço em status %s", status)
      );
    }
    this.status = StatusOS.EM_DIAGNOSTICO;
    // ... lógica para adicionar serviço
  }
  
  public void incluirPeca(Peca peca) {
    if (status != StatusOS.RECEBIDA && status != StatusOS.EM_DIAGNOSTICO) {
      throw new IllegalStateException(
          String.format("Não é possível incluir peça em status %s", status)
      );
    }
    this.status = StatusOS.EM_DIAGNOSTICO;
    // ... lógica para adicionar peça
  }
  
  public void gerarOrcamento() {
    if (status != StatusOS.EM_DIAGNOSTICO) {
      throw new IllegalStateException(
          "Só é possível gerar orçamento em status EM_DIAGNOSTICO"
      );
    }
    this.status = StatusOS.AGUARDANDO_APROVACAO;
    // ... lógica para calcular orçamento
  }
  
  public void aprovarOrcamento() {
    if (status != StatusOS.AGUARDANDO_APROVACAO) {
      throw new IllegalStateException(
          "Só é possível aprovar orçamento em status AGUARDANDO_APROVACAO"
      );
    }
    this.status = StatusOS.EM_EXECUCAO;
  }
  
  public void rejeitarOrcamento(String motivo) {
    if (status != StatusOS.AGUARDANDO_APROVACAO) {
      throw new IllegalStateException(
          "Só é possível rejeitar orçamento em status AGUARDANDO_APROVACAO"
      );
    }
    this.status = StatusOS.CANCELADA;
    this.motivoCancelamento = motivo;
  }
  
  public void finalizar() {
    if (status != StatusOS.EM_EXECUCAO) {
      throw new IllegalStateException(
          "Só é possível finalizar em status EM_EXECUCAO"
      );
    }
    this.status = StatusOS.FINALIZADA;
    this.dataFinalizacao = LocalDateTime.now();
  }
  
  public void entregar() {
    if (status != StatusOS.FINALIZADA) {
      throw new IllegalStateException(
          "Só é possível entregar em status FINALIZADA"
      );
    }
    this.status = StatusOS.ENTREGUE;
    this.dataEntrega = LocalDateTime.now();
  }
  
  // Getters
  public StatusOS getStatus() { return status; }
  public String getId() { return id; }
  // ... outros getters
}
```

### Benefícios
1. ✅ **Impossível ter estado inválido**: Compilação protege contra transições erradas
2. ✅ **Regras centralizadas**: Toda lógica de transição em um lugar
3. ✅ **Testável**: Fácil testar transições inválidas
4. ✅ **Segurança**: Não permite salvar ordem em estado intermediário

### Testes

```java
@Test
public void testNaoPodeGerarOrcamentoEmStatusRecebida() {
  // Arrange
  OrdemServico ordem = OrdemServico.criar("CPF", "Desc", "CLI", "VEI");
  
  // Act & Assert
  assertThrows(IllegalStateException.class, () -> {
    ordem.gerarOrcamento();  // Ainda está em RECEBIDA
  });
}

@Test
public void testTransicaoValida() {
  // Arrange
  OrdemServico ordem = OrdemServico.criar("CPF", "Desc", "CLI", "VEI");
  
  // Act
  ordem.incluirServico(servicoMock);  // RECEBIDA → EM_DIAGNOSTICO
  ordem.gerarOrcamento();              // EM_DIAGNOSTICO → AGUARDANDO_APROVACAO
  ordem.aprovarOrcamento();            // AGUARDANDO_APROVACAO → EM_EXECUCAO
  ordem.finalizar();                   // EM_EXECUCAO → FINALIZADA
  ordem.entregar();                    // FINALIZADA → ENTREGUE
  
  // Assert
  assertThat(ordem.getStatus()).isEqualTo(StatusOS.ENTREGUE);
}
```

### Diagrama de Estados
```
[RECEBIDA] ─(incluir serviço/peça)─→ [EM_DIAGNOSTICO]
                                            │
                                   (gerar orçamento)
                                            │
                                            ▼
                                   [AGUARDANDO_APROVACAO]
                                      │          │
                         (aprovar)    │          │ (rejeitar)
                                      │          │
                                      ▼          ▼
                                 [EM_EXECUCAO]  [CANCELADA]
                                      │
                              (finalizar)
                                      │
                                      ▼
                                 [FINALIZADA]
                                      │
                              (entregar)
                                      │
                                      ▼
                                 [ENTREGUE]
```

### Próximos Passos
- [x] Implementar machine de estados
- [x] Adicionar validações de transição
- [x] Criar testes para transições inválidas
- [ ] Adicionar eventos de domínio (Domain Events)
- [ ] Implementar auditoria (quem e quando fez transição)

---

## ADR-003: HTTP Methods Corretos (REST)

### Status: ACEITO ✅


### Contexto
Feedback do professor: "Aprovação de orçamento usa GET, violando princípios REST".

Necessário corrigir:
- GET deve ser idempotente e não alterar estado
- POST cria novo recurso
- PUT altera recurso existente
- DELETE remove recurso

### Decisão
Usar HTTP Methods semanticamente corretos:
- **POST**: Criar novo recurso
- **GET**: Recuperar recurso (sem efeitos colaterais)
- **PUT**: Atualizar estado do recurso
- **DELETE**: Remover recurso

### Implementação

**ANTES (Incorreto)**:
```java
@GetMapping("/ordens/{id}/aprovar")  // ❌ GET não deve alterar estado
public ResponseEntity<OrdemServicoResponse> aprovarOrcamento(
    @PathVariable String id) {
  // ...
}
```

**DEPOIS (Correto)**:
```java
@PutMapping("/ordens/{id}/aprovar")  // ✅ PUT altera estado
public ResponseEntity<OrdemServicoResponse> aprovarOrcamento(
    @PathVariable String id) {
  // ...
}
```

### Mapeamento Completo de Endpoints

| Método | Endpoint | O que faz | Idempotente |
|--------|----------|-----------|-------------|
| **POST** | `/api/ordens` | Criar ordem | ❌ Não |
| **GET** | `/api/ordens` | Listar ordens | ✅ Sim |
| **GET** | `/api/ordens/{id}` | Buscar ordem | ✅ Sim |
| **PUT** | `/api/ordens/{id}/servicos` | Incluir serviço | ❌ Não (usa POST melhor) |
| **PUT** | `/api/ordens/{id}/pecas` | Incluir peça | ❌ Não (usa POST melhor) |
| **POST** | `/api/ordens/{id}/orcamento` | Gerar orçamento | ❌ Não |
| **PUT** | `/api/ordens/{id}/aprovar` | Aprovar orçamento | ✅ Sim* |
| **PUT** | `/api/ordens/{id}/rejeitar` | Rejeitar orçamento | ✅ Sim* |
| **PUT** | `/api/ordens/{id}/finalizar` | Finalizar OS | ✅ Sim* |
| **PUT** | `/api/ordens/{id}/entregar` | Entregar OS | ✅ Sim* |

*Idempotente porque a operação é a mesma (mudar status), não cria duplicatas.

### Código Corrigido

```java
@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoController {
  
  @PostMapping
  public ResponseEntity<OrdemServicoResponse> criar(
      @RequestBody CriarOrdemRequest request) {
    // Criar nova ordem
  }
  
  @GetMapping
  public ResponseEntity<List<OrdemServicoResponse>> listar() {
    // Listar ordens (sem alterar nada)
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<OrdemServicoResponse> buscar(
      @PathVariable String id) {
    // Buscar ordem (sem alterar nada)
  }
  
  @PostMapping("/{id}/servicos")
  public ResponseEntity<OrdemServicoResponse> incluirServico(
      @PathVariable String id,
      @RequestBody IncluirServicoRequest request) {
    // Inclui serviço (novo sub-recurso, usa POST)
  }
  
  @PostMapping("/{id}/pecas")
  public ResponseEntity<OrdemServicoResponse> incluirPeca(
      @PathVariable String id,
      @RequestBody IncluirPecaRequest request) {
    // Inclui peça (novo sub-recurso, usa POST)
  }
  
  @PostMapping("/{id}/orcamento")
  public ResponseEntity<OrdemServicoResponse> gerarOrcamento(
      @PathVariable String id) {
    // Gera novo orçamento (POST porque cria novo estado/documento)
  }
  
  @PutMapping("/{id}/aprovar")
  public ResponseEntity<OrdemServicoResponse> aprovarOrcamento(
      @PathVariable String id) {
    // ✅ PUT: Altera o estado da ordem
    // Idempotente: chamar 2x resulta no mesmo estado
  }
  
  @PutMapping("/{id}/rejeitar")
  public ResponseEntity<OrdemServicoResponse> rejeitarOrcamento(
      @PathVariable String id,
      @RequestBody RejeitarOrcamentoRequest request) {
    // ✅ PUT: Altera o estado da ordem
  }
  
  @PutMapping("/{id}/finalizar")
  public ResponseEntity<OrdemServicoResponse> finalizar(
      @PathVariable String id) {
    // ✅ PUT: Altera o estado da ordem
  }
  
  @PutMapping("/{id}/entregar")
  public ResponseEntity<OrdemServicoResponse> entregar(
      @PathVariable String id) {
    // ✅ PUT: Altera o estado da ordem
  }
}
```

### HTTP Status Codes

```java
public class OrdemServicoController {
  
  @PostMapping
  public ResponseEntity<OrdemServicoResponse> criar(...) {
    // 201 Created: Novo recurso criado
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header("Location", "/api/ordens/" + ordem.getId())
        .body(response);
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<OrdemServicoResponse> buscar(...) {
    // 200 OK ou 404 Not Found
    return ResponseEntity.ok(response);
  }
  
  @PutMapping("/{id}/aprovar")
  public ResponseEntity<OrdemServicoResponse> aprovar(...) {
    // 200 OK: Atualizado com sucesso
    return ResponseEntity.ok(response);
  }
  
  @PutMapping("/{id}/aprovar")
  public ResponseEntity<OrdemServicoResponse> aproveLocked(...) {
    // 400 Bad Request: Tentou aprovar em status inválido
    return ResponseEntity
        .badRequest()
        .body(response);
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletar(...) {
    // 204 No Content: Deletado com sucesso
    return ResponseEntity.noContent().build();
  }
}
```

### Benefícios
1. ✅ **Conformidade REST**: Segue padrão REST
2. ✅ **Previsível**: Desenvolvedores sabem o que esperar
3. ✅ **Ferramentas**: Ferramentas HTTP entendem semanticamente
4. ✅ **Cache**: GET pode ser cacheado automaticamente
5. ✅ **Segurança**: Menos risco de mudanças acidentais com GET

### Referências
- REST Constraints: https://restfulapi.net/
- HTTP Methods: https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods

---

## ADR-005: Padrão de Comunicação (Síncrono vs Assíncrono)

### Status: ACEITO (Síncrono) ✅

### Contexto
Aplicação no Kubernetes precisa escalar automaticamente baseado em carga.

### Decisão
Implementar **HPA (Horizontal Pod Autoscaler)** com métricas de CPU e memória.

### Implementação

**k8s/hpa.yaml**:
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: tech-challenge-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: tech-challenge-app
  
  minReplicas: 2
  maxReplicas: 10
  
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
    
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100
        periodSeconds: 30
```

### Métricas
- **CPU**: Escala quando > 70%
- **Memória**: Escala quando > 80%
- **Min Replicas**: 2 (sempre rodando 2 pods)
- **Max Replicas**: 10 (limite de custo)

### Cálculo
```
Se CPU médio > 70%:
  Novas Replicas = Replicas * (CPU Atual / 70)
  
Exemplo: 3 replicas, CPU = 105%
  Novas Replicas = 3 * (105 / 70) = 3 * 1.5 = 4.5 → 5 replicas
```

### Monitoramento

```bash
# Visualizar HPA em tempo real
kubectl get hpa -w

# Nome: tech-challenge-hpa
# REFERENCE: Deployment/tech-challenge-app
# TARGETS: 45%/70% (CPU atual / limite)
# MINPODS: 2
# MAXPODS: 10
# REPLICAS: 3
# AGE: 5d
```

### Benefícios
1. ✅ **Escalabilidade**: Aplicação escala automaticamente com carga
2. ✅ **Custo**: Não roda pods desnecessários
3. ✅ **Disponibilidade**: Mais pods quando necessário
4. ✅ **Sem Intervenção Manual**: Automático

### Próximos Passos
- [ ] Testar scale up com load test
- [ ] Testar scale down em horários vazios
- [ ] Ajustar métricas baseado em uso real
- [ ] Implementar KEDA para métricas customizadas

---

## ADR-005: Padrão de Comunicação (Síncrono vs Assíncrono)

### Status: ACEITO (Síncrono) ✅


### Contexto
Comunicação entre componentes (API → Banco, API → Notificações).

### Opções
1. **Síncrono (REST/gRPC)**: Cliente espera resposta
2. **Assíncrono (Message Queue)**: Cliente não espera

### Decisão
**Síncrono (REST)** para principal (API ↔ Banco).  
**Assíncrono (SQS/Kafka)** para notificações (future).

### Implementação

**Síncrono (Atualmente)**:
```java
// Chamada síncrona
@PostMapping("/ordens")
public ResponseEntity<OrdemServicoResponse> criar(...) {
  // Espera banco responder
  ordem = criarOrdemService.executar(...);
  return ResponseEntity.created(...).body(response);
}
```

**Assíncrono (Future)**:
```java
// Enviar notificação para fila
@PostMapping("/ordens")
public ResponseEntity<OrdemServicoResponse> criar(...) {
  ordem = criarOrdemService.executar(...);
  
  // Não espera resposta da notificação
  notificationQueue.send(new OrdemCriadaEvent(ordem));
  
  return ResponseEntity.created(...).body(response);
}
```

### Benefícios
- **Síncrono**: Simples, fácil de testar, garante execução
- **Assíncrono**: Não bloqueia, escalável, resiliente

### Próximos Passos
- [ ] Implementar AWS SQS para notificações
- [ ] Implementar consumer assíncrono
- [ ] Dead Letter Queue para erros

---

## Matriz de ADRs

| ADR | Título | Status | Data | Revisor |
|---|---|---|---|---|
| ADR-001 | Arquitetura Hexagonal | ✅ Aceito | 2026-03-18 | Eu |
| ADR-002 | Máquina de Estados | ✅ Aceito | 2026-03-18 | Eu |
| ADR-003 | HTTP Methods Corretos | ✅ Aceito | 2026-03-18 | Eu |
| ADR-004 | HPA | ✅ Aceito | 2026-03-18 | Eu |
| ADR-005 | Padrão de Comunicação | ✅ Aceito | 2026-03-18 | Eu |

---

**Versão**: 1.0  
**Data**: 2026-03-18  
**Autores**: Eu  
**Próxima Revisão**: 2026-06-18

