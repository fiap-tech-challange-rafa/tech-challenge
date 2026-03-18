package br.com.fiap.techchallange.application.ordemservico.port.in;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;

public interface RejeitarOrcamentoPort {
    OrdemServico executar(Long osId);
}
