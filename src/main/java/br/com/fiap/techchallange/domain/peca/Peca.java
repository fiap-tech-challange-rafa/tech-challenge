package br.com.fiap.techchallange.domain.peca;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

public class Peca {

    private Long id;
    private String sku;
    private String nome;
    private BigDecimal preco;
    private Integer quantidadeEstoque;

    public Peca(String sku, String nome, BigDecimal preco, Integer quantidadeEstoque) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU é obrigatório");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Preço inválido");
        if (quantidadeEstoque == null || quantidadeEstoque < 0)
            throw new IllegalArgumentException("Quantidade de estoque inválida");
        this.sku = sku.trim();
        this.nome = nome.trim();
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Peca() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void atualizarDados(String novoNome, BigDecimal novoPreco) {
        if (novoNome != null && !novoNome.isBlank()) this.nome = novoNome.trim();
        if (novoPreco != null) {
            if (novoPreco.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Preço inválido");
            this.preco = novoPreco;
        }
    }

    public void creditarEstoque(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade a creditar deve ser > 0");
        this.quantidadeEstoque = this.quantidadeEstoque + qtd;
    }

    public void debitarEstoque(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade a debitar deve ser > 0");
        if (this.quantidadeEstoque < qtd) throw new IllegalStateException("Estoque insuficiente");
        this.quantidadeEstoque = this.quantidadeEstoque - qtd;
    }
}
