package br.com.fiap.techchallange.infrastructure.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.StatusOS;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordem_servico")
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="cliente_id", nullable = false)
    private Long clienteId;

    @Column(name="veiculo_id", nullable = false)
    private Long veiculoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOS status;

    @Column(name = "total_orcamento", precision = 12, scale = 2)
    private BigDecimal totalOrcamento;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "ordem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemServicoEntity> servicos = new ArrayList<>();

    @OneToMany(mappedBy = "ordem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPecaEntity> pecas = new ArrayList<>();

    public OrdemServicoEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public StatusOS getStatus() { return status; }
    public void setStatus(StatusOS status) { this.status = status; }

    public BigDecimal getTotalOrcamento() { return totalOrcamento; }
    public void setTotalOrcamento(BigDecimal totalOrcamento) { this.totalOrcamento = totalOrcamento; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public List<ItemServicoEntity> getServicos() { return servicos; }
    public void setServicos(List<ItemServicoEntity> servicos) { this.servicos = servicos; }

    public List<ItemPecaEntity> getPecas() { return pecas; }
    public void setPecas(List<ItemPecaEntity> pecas) { this.pecas = pecas; }
}
