package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.in.IncluirPecaNaOSPort;
import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.ItemPeca;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class IncluirPecaNaOSService implements IncluirPecaNaOSPort {

    private final OrdemServicoRepositoryPort repository;
    private final PecaRepository pecaRepository;

    public IncluirPecaNaOSService(OrdemServicoRepositoryPort repository, PecaRepository pecaRepository) {
        this.repository = repository;
        this.pecaRepository = pecaRepository;
    }

    @Override
    @Transactional
    public OrdemServico executar(Long osId, Long pecaId, Integer quantidade) {
        OrdemServico os = repository.buscarPorId(osId)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada: " + osId));

        Peca peca = pecaRepository.buscarPorId(pecaId).orElseThrow(() -> new IllegalArgumentException("Peça não encontrada: " + pecaId));

        os.incluirPeca(new ItemPeca(pecaId, peca.getNome(), peca.getPreco(), quantidade));
        return repository.salvar(os);
    }
}
