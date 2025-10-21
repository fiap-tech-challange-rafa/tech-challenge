package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoverVeiculoService {

    private final VeiculoRepository repository;

    public RemoverVeiculoService(VeiculoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void executar(Long id) {
        // aqui você poderia checar regras (ex: não remover se tiver OS ativas)
        repository.remover(id);
    }
}
