package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarOrdemServicoService {

    private final OrdemServicoRepository repository;

    public CriarOrdemServicoService(OrdemServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OrdemServico executar(Long clienteId, Long veiculoId) {
        OrdemServico os = new OrdemServico(clienteId, veiculoId);
        return repository.salvar(os);
    }
}
