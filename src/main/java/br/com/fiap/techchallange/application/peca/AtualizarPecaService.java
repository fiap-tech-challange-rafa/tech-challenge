package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AtualizarPecaService {

    private final PecaRepository repository;

    public AtualizarPecaService(PecaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Peca executar(Long id, String novoNome, BigDecimal novoPreco) {
        Peca existente = repository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Peça não encontrada: " + id));
        existente.atualizarDados(novoNome, novoPreco);
        return repository.salvar(existente);
    }
}
