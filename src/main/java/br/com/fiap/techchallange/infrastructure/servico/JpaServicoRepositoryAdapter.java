package br.com.fiap.techchallange.infrastructure.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaServicoRepositoryAdapter implements ServicoRepository {

    private final SpringDataServicoRepository jpa;

    public JpaServicoRepositoryAdapter(SpringDataServicoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Servico salvar(Servico servico) {
        ServicoEntity e = toEntity(servico);
        try {
            ServicoEntity saved = jpa.save(e);
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Violação de integridade ao salvar serviço: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Servico> buscarPorCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(this::toDomain);
    }

    @Override
    public List<Servico> listarTodos() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }
    @Override
    public void removerTodos() {
        jpa.deleteAll();
    }
    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    private ServicoEntity toEntity(Servico s) {
        ServicoEntity e = new ServicoEntity();
        e.setId(s.getId());
        e.setCodigo(s.getCodigo());
        e.setDescricao(s.getDescricao());
        e.setPreco(s.getPreco());
        return e;
    }

    private Servico toDomain(ServicoEntity e) {
        Servico s = new Servico(e.getCodigo(), e.getDescricao(), e.getPreco());
        s.setId(e.getId());
        return s;
    }
}
