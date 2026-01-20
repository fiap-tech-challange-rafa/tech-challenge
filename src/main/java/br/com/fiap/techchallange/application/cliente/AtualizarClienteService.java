package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.cliente.ClienteRepository;
import br.com.fiap.techchallange.domain.cliente.Email;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AtualizarClienteService {

    private final ClienteRepository repository;

    public AtualizarClienteService(ClienteRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public Cliente executar(Long id, String novoNome, String novoTelefone, String novoEmail) {
        Cliente existente = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        Email email = (novoEmail == null || novoEmail.isBlank()) ? null : new Email(novoEmail);

        existente.atualizarDados(novoNome, novoTelefone, email);
        return repository.salvar(existente);
    }
}