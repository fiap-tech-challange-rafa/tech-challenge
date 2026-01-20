package br.com.fiap.techchallange.application.ordemservico.port.in;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;

import java.util.List;

public interface BuscarOrdemPort {
    OrdemServico porId(Long id);
    List<OrdemServico> listarTodos();
    void remover(Long id);
}
