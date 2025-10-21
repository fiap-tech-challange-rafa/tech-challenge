package br.com.fiap.techchallange.domain.cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorDocumento(String documento); // valor sem pontuação
    List<Cliente> listarTodos();
    void remover(Long id);
    void removerTodos();
}