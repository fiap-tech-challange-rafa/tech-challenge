package br.com.fiap.techchallange.application.ordemservico.port.in;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;

public interface IncluirServicoNaOSPort {
    OrdemServico executar(Long osId, Long servicoId, String descricao);
}
