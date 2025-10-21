package br.com.fiap.techchallange.domain.servico;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

public class Servico {

    private Long id;
    private String codigo;
    private String descricao;
    private BigDecimal preco;

    public Servico(String codigo, String descricao, BigDecimal preco) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do serviço é obrigatório");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do serviço é obrigatória");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço do serviço é obrigatório e deve ser >= 0");
        }
        this.codigo = codigo.trim();
        this.descricao = descricao.trim();
        this.preco = preco;
    }

    public Servico() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void atualizar(String novaDescricao, BigDecimal novoPreco) {
        if (novaDescricao != null && !novaDescricao.isBlank()) {
            this.descricao = novaDescricao.trim();
        }
        if (novoPreco != null) {
            if (novoPreco.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Preço do serviço deve ser >= 0");
            }
            this.preco = novoPreco;
        }
    }
}

