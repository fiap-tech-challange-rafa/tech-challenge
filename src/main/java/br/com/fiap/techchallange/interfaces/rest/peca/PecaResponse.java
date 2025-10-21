package br.com.fiap.techchallange.interfaces.rest.peca;


import br.com.fiap.techchallange.domain.peca.Peca;

import java.math.BigDecimal;

public record PecaResponse(Long id, String sku, String nome, BigDecimal preco, Integer quantidadeEstoque) {
    public static PecaResponse fromDomain(Peca p) {
        return new PecaResponse(p.getId(), p.getSku(), p.getNome(), p.getPreco(), p.getQuantidadeEstoque());
    }
}
