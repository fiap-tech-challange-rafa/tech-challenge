package br.com.fiap.techchallange.interfaces.rest.peca;

import java.math.BigDecimal;

public record PecaRequest(String sku, String nome, BigDecimal preco, Integer quantidadeEstoque) {}
