package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.Placa;
import br.com.fiap.techchallange.domain.veiculo.Veiculo;
import br.com.fiap.techchallange.domain.veiculo.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarVeiculoService {

    private final VeiculoRepository repository;

    public AtualizarVeiculoService(VeiculoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Veiculo executar(Long id, String placaStr, String marca, String modelo, Integer ano) {
        Veiculo existente = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        Placa novaPlaca = placaStr != null ? new Placa(placaStr) : null;


        if (novaPlaca != null && !novaPlaca.equals(existente.getPlaca())) {
            repository.buscarPorPlaca(novaPlaca.valor()).ifPresent(v -> {
                throw new IllegalStateException("Placa já cadastrada: " + novaPlaca.valor());
            });
        }

        existente.atualizarDados(novaPlaca, marca, modelo, ano);
        return repository.salvar(existente);
    }
}
