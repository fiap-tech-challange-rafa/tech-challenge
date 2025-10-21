package br.com.fiap.techchallange.infrastructure.peca;

import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaPecaRepositoryAdapter implements PecaRepository {

    private final SpringDataPecaRepository jpa;

    public JpaPecaRepositoryAdapter(SpringDataPecaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Peca salvar(Peca peca) {
        PecaEntity e = toEntity(peca);
        try {
            PecaEntity saved = jpa.save(e);
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Violação de integridade ao salvar peça: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Peca> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Peca> buscarPorSku(String sku) {
        if (sku == null) return Optional.empty();
        return jpa.findBySku(sku.trim()).map(this::toDomain);
    }

    @Override
    public List<Peca> listarTodos() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    private PecaEntity toEntity(Peca p) {
        PecaEntity e = new PecaEntity();
        e.setId(p.getId());
        e.setSku(p.getSku());
        e.setNome(p.getNome());
        e.setPreco(p.getPreco());
        e.setQuantidadeEstoque(p.getQuantidadeEstoque());
        return e;
    }

    private Peca toDomain(PecaEntity e) {
        Peca p = new Peca(e.getSku(), e.getNome(), e.getPreco(), e.getQuantidadeEstoque());
        p.setId(e.getId());
        return p;
    }

    @Override
    public void removerTodos() {
        jpa.deleteAll();
    }
}
