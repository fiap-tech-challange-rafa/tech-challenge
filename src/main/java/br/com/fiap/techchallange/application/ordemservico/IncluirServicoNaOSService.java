package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.ItemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServicoRepository;
import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class IncluirServicoNaOSService {

    private final OrdemServicoRepository repository;
    private final ServicoRepository servicoRepository;

    public IncluirServicoNaOSService(OrdemServicoRepository repository, ServicoRepository servicoRepository) {
        this.repository = repository;
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public OrdemServico executar(Long osId, Long servicoId, String descricao) {
        OrdemServico os = repository.buscarPorId(osId)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada: " + osId));

        Servico servico = servicoRepository.buscarPorId(servicoId).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + servicoId));

        os.incluirServico(new ItemServico(servicoId, descricao,servico.getPreco()));
        return repository.salvar(os);
    }
}
