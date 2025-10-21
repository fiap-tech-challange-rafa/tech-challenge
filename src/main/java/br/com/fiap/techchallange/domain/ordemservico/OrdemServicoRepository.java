package br.com.fiap.techchallange.domain.ordemservico;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository {
    OrdemServico salvar(OrdemServico os);
    Optional<OrdemServico> buscarPorId(Long id);
    List<OrdemServico> listarTodos();
    void remover(Long id);
    void removerTodos();
}
