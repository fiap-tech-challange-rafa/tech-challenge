package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.cliente.ClienteRepository;
import br.com.fiap.techchallange.infrastructure.cliente.SpringDataClienteRepository;
import br.com.fiap.techchallange.domain.cliente.Documento;
import br.com.fiap.techchallange.domain.cliente.Email;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CadastrarClienteService {
    private final ClienteRepository repository;

    public CadastrarClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Cliente executar(String nome, String documentoStr, String telefone, String emailStr) {
        Documento doc = new Documento(documentoStr);
        repository.buscarPorDocumento(doc.valor()).ifPresent(c -> {
            throw new IllegalStateException("Documento já cadastrado");
        });

        Email email = (emailStr == null || emailStr.isBlank()) ? null : new Email(emailStr);
        Cliente cliente = new Cliente(nome, doc, telefone, email);
        return repository.salvar(cliente);
    }
}
