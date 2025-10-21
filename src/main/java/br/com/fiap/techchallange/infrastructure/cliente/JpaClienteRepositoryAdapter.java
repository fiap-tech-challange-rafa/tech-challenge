package br.com.fiap.techchallange.infrastructure.cliente;

import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.cliente.ClienteRepository;
import br.com.fiap.techchallange.domain.cliente.Documento;
import br.com.fiap.techchallange.domain.cliente.Email;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaClienteRepositoryAdapter implements ClienteRepository {

    private final SpringDataClienteRepository jpa;

    public JpaClienteRepositoryAdapter(SpringDataClienteRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        ClienteEntity e = toEntity(cliente);
        try {
            ClienteEntity saved = jpa.save(e);
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            // por exemplo: violação de unique constraint
            throw new IllegalStateException("Dados já existem no banco: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        String digits = documento.replaceAll("\\D", "");
        return jpa.findByDocumento(digits).map(this::toDomain);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public void removerTodos() {
        jpa.deleteAll();
    }

    private ClienteEntity toEntity(Cliente c) {
        ClienteEntity e = new ClienteEntity();
        e.setId(c.getId());
        e.setNome(c.getNome());
        e.setDocumento(c.getDocumento().valor());
        e.setTelefone(c.getTelefone());
        e.setEmail(c.getEmail() != null ? c.getEmail().getValor() : null);
        return e;
    }

    private Cliente toDomain(ClienteEntity e) {
        Email email = (e.getEmail() == null) ? null : new Email(e.getEmail());
        Cliente c = new Cliente(e.getNome(), new Documento(e.getDocumento()), e.getTelefone(), email);
        c.setId(e.getId());
        return c;
    }
}
