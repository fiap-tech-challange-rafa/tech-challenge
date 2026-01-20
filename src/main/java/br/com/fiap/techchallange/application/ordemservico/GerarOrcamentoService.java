package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.in.GerarOrcamentoPort;
import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GerarOrcamentoService implements GerarOrcamentoPort {

    private final OrdemServicoRepositoryPort repository;

    public GerarOrcamentoService(OrdemServicoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public OrdemServico executar(Long osId) {
        OrdemServico os = repository.buscarPorId(osId)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada: " + osId));

        os.gerarOrcamento();
        return repository.salvar(os);
    }
}
