package br.com.fiap.techchallange.interfaces.rest.servico;

import br.com.fiap.techchallange.domain.servico.Servico;

import java.math.BigDecimal;

public record ServicoResponse(Long id, String codigo, String descricao, BigDecimal preco) {
    public static ServicoResponse fromDomain(Servico s) {
        return new ServicoResponse(s.getId(), s.getCodigo(), s.getDescricao(), s.getPreco());
    }
}
