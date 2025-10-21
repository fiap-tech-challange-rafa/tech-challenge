package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.Placa;
import br.com.fiap.techchallange.domain.veiculo.Veiculo;
import br.com.fiap.techchallange.domain.veiculo.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarVeiculoService {

    private final VeiculoRepository repository;

    public CadastrarVeiculoService(VeiculoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Veiculo executar(Long clienteId, String placaStr, String marca, String modelo, Integer ano) {
        Placa placa = new Placa(placaStr);
        // exemplo: garantir unicidade de placa
        repository.buscarPorPlaca(placa.valor()).ifPresent(v -> {
            throw new IllegalStateException("Placa já cadastrada: " + placa.valor());
        });

        Veiculo veiculo = new Veiculo(clienteId, placa, marca, modelo, ano);
        return repository.salvar(veiculo);
    }
}
