package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoverPecaService {

    private final PecaRepository repository;

    public RemoverPecaService(PecaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void executar(Long id) {
        repository.remover(id);
    }
}
