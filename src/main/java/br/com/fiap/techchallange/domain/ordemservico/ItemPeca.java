package br.com.fiap.techchallange.domain.ordemservico;

import jakarta.persistence.*;

import java.math.BigDecimal;

public class ItemPeca {
    private Long id;

    private OrdemServico ordemServico;

    private String nome;
    private BigDecimal precoUnitario;
    private Integer quantidade;

    public ItemPeca(Long id, String nome, BigDecimal precoUnitario, Integer quantidade) {
        this.id = id;
        this.nome = nome;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }

    public ItemPeca() {

    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public Integer getQuantidade() { return quantidade; }
    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
