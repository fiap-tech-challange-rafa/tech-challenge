package br.com.fiap.techchallange.domain.veiculo;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepository {
    Veiculo salvar(Veiculo v);
    Optional<Veiculo> buscarPorId(Long id);
    Optional<Veiculo> buscarPorPlaca(String placa);
    List<Veiculo> listarPorClienteId(Long clienteId);
    List<Veiculo> listarTodos();
    void remover(Long id);
    void removerTodos();
}
