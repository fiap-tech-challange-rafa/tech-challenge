package br.com.fiap.techchallange.interfaces.rest.peca;

import java.math.BigDecimal;

public record PecaUpdateRequest(String nome, BigDecimal preco) {}
