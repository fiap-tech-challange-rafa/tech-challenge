package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.in.EntregarOrdemServicoPort;
import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntregarOrdemServicoService implements EntregarOrdemServicoPort {

    private final OrdemServicoRepositoryPort repository;

    public EntregarOrdemServicoService(OrdemServicoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public OrdemServico executar(Long osId) {
        OrdemServico os = repository.buscarPorId(osId)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada: " + osId));
        os.entregar();
        return repository.salvar(os);
    }
}
