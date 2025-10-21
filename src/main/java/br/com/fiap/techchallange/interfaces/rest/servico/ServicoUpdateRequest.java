package br.com.fiap.techchallange.interfaces.rest.servico;

import java.math.BigDecimal;

public record ServicoUpdateRequest(String descricao, BigDecimal preco) {}
