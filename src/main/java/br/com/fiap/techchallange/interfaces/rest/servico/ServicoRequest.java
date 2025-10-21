package br.com.fiap.techchallange.interfaces.rest.servico;

import java.math.BigDecimal;

public record ServicoRequest(String codigo, String descricao, BigDecimal preco) {}
