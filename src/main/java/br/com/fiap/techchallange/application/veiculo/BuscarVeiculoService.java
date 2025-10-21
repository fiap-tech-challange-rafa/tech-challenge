package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.Veiculo;
import br.com.fiap.techchallange.domain.veiculo.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarVeiculoService {

    private final VeiculoRepository repository;

    public BuscarVeiculoService(VeiculoRepository repository) {
        this.repository = repository;
    }

    public Veiculo porId(Long id) {
        return repository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
    }

    public List<Veiculo> porCliente(Long clienteId) {
        return repository.listarPorClienteId(clienteId);
    }

    public List<Veiculo> listarTodos() {
        return repository.listarTodos();
    }
}
