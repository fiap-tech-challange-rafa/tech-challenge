package br.com.fiap.techchallange.infrastructure.ordemservico;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "os_item_peca")
public class ItemPecaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "peca_id", nullable = false)
    private Long pecaId;

    @Column(nullable = false)
    private String nome;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private Integer quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_id")
    private OrdemServicoEntity ordem;

    public ItemPecaEntity() {}

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPecaId() { return pecaId; }
    public void setPecaId(Long pecaId) { this.pecaId = pecaId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public OrdemServicoEntity getOrdem() { return ordem; }
    public void setOrdem(OrdemServicoEntity ordem) { this.ordem = ordem; }
}
