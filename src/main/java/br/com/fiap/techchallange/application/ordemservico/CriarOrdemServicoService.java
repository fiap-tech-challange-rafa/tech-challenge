package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.in.CriarOrdemServicoPort;
import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarOrdemServicoService implements CriarOrdemServicoPort {

    private final OrdemServicoRepositoryPort repository;

    public CriarOrdemServicoService(OrdemServicoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public OrdemServico executar(Long clienteId, Long veiculoId) {
        OrdemServico os = new OrdemServico(clienteId, veiculoId);
        return repository.salvar(os);
    }
}
