package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AtualizarServicoService {

    private final ServicoRepository repository;

    public AtualizarServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Servico executar(Long id, String novaDescricao, BigDecimal novoPreco) {
        Servico existente = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));

        existente.atualizar(novaDescricao, novoPreco);
        return repository.salvar(existente);
    }
}
