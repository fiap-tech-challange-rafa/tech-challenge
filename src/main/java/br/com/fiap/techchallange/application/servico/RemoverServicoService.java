package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoverServicoService {

    private final ServicoRepository repository;

    public RemoverServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void executar(Long id) {
        repository.remover(id);
    }
}
