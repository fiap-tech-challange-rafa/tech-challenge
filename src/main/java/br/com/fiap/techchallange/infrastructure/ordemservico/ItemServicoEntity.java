package br.com.fiap.techchallange.infrastructure.ordemservico;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "os_item_servico")
public class ItemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "servico_id", nullable = false)
    private Long servicoId;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_id")
    private OrdemServicoEntity ordem;

    public ItemServicoEntity() {}

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getServicoId() { return servicoId; }
    public void setServicoId(Long servicoId) { this.servicoId = servicoId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public OrdemServicoEntity getOrdem() { return ordem; }
    public void setOrdem(OrdemServicoEntity ordem) { this.ordem = ordem; }
}
