package br.com.fiap.techchallange.infrastructure.veiculo;

import br.com.fiap.techchallange.domain.veiculo.Placa;
import br.com.fiap.techchallange.domain.veiculo.Veiculo;
import br.com.fiap.techchallange.domain.veiculo.VeiculoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaVeiculoRepositoryAdapter implements VeiculoRepository {

    private final SpringDataVeiculoRepository jpa;

    public JpaVeiculoRepositoryAdapter(SpringDataVeiculoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        VeiculoEntity entity = toEntity(veiculo);
        VeiculoEntity salvo = jpa.save(entity);
        Veiculo domain = toDomain(salvo);
        return domain;
    }

    @Override
    public Optional<Veiculo> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return jpa.findByPlaca(placa).map(this::toDomain);
    }

    @Override
    public List<Veiculo> listarPorClienteId(Long clienteId) {
        return jpa.findByClienteId(clienteId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Veiculo> listarTodos() {
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

    private VeiculoEntity toEntity(Veiculo v) {
        VeiculoEntity e = new VeiculoEntity();
        e.setId(v.getId());
        e.setClienteId(v.getClienteId());
        e.setPlaca(v.getPlaca().valor());
        e.setMarca(v.getMarca());
        e.setModelo(v.getModelo());
        e.setAno(v.getAno());
        return e;
    }

    private Veiculo toDomain(VeiculoEntity e) {
        Veiculo v = new Veiculo(e.getClienteId(), new Placa(e.getPlaca()), e.getMarca(), e.getModelo(), e.getAno());
        v.setId(e.getId());
        return v;
    }
}
