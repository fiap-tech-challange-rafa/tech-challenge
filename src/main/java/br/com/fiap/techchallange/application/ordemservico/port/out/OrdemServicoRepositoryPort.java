package br.com.fiap.techchallange.application.ordemservico.port.out;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepositoryPort {
    OrdemServico salvar(OrdemServico os);
    Optional<OrdemServico> buscarPorId(Long id);
    List<OrdemServico> listarTodos();
    void remover(Long id);
    void removerTodos();
}
