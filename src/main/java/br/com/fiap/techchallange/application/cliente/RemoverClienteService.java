package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.infrastructure.cliente.SpringDataClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RemoverClienteService {

    private final SpringDataClienteRepository repository;

    public RemoverClienteService(SpringDataClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void executar(Long id) {
        repository.deleteById(id);
    }
}
