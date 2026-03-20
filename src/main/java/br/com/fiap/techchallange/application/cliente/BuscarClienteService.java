package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.cliente.ClienteRepository;
import br.com.fiap.techchallange.infrastructure.cliente.SpringDataClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarClienteService {

    private final ClienteRepository repository;

    //para o video
    public BuscarClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente porId(Long id) {
        return repository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public Cliente porDocumento(String documento) {
        return repository.buscarPorDocumento(documento)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public List<Cliente> listarTodos() {
        return repository.listarTodos();
    }
}
