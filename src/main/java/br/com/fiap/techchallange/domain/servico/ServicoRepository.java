package br.com.fiap.techchallange.domain.servico;

import java.util.List;
import java.util.Optional;

public interface ServicoRepository {

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(Long id);

    Optional<Servico> buscarPorCodigo(String codigo);

    List<Servico> listarTodos();

    void remover(Long id);
    void removerTodos();
}
