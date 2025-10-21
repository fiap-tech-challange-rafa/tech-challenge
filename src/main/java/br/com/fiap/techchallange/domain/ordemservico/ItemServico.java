package br.com.fiap.techchallange.domain.ordemservico;


import java.math.BigDecimal;

public class ItemServico {

    private Long id;

    private OrdemServico ordemServico;

    private Long servicoId;
    private String descricao;
    private BigDecimal preco;

    public ItemServico(Long servicoId, String descricao, BigDecimal preco) {
        this.servicoId = servicoId;
        this.descricao = descricao;
        this.preco = preco;
    }

    public ItemServico() {

    }

    public Long getServicoId() { return servicoId; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public OrdemServico getOrdemServico() {
        return ordemServico;
    }
}
