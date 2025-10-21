package br.com.fiap.techchallange.interfaces.rest.cliente;

import br.com.fiap.techchallange.domain.cliente.Cliente;

public record ClienteResponse(Long id, String nome, String documento, String telefone, String email) {
    public static ClienteResponse fromDomain(Cliente c) {
        return new ClienteResponse(
                c.getId(),
                c.getNome(),
                c.getDocumento().valor(),
                c.getTelefone(),
                c.getEmail() != null ? c.getEmail().getValor() : null
        );
    }
}
