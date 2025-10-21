package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CadastrarServicoService {

    private final ServicoRepository repository;

    public CadastrarServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Servico executar(String codigo, String descricao, BigDecimal preco) {
        repository.buscarPorCodigo(codigo).ifPresent(s -> {
            throw new IllegalStateException("Código do serviço já cadastrado: " + codigo);
        });

        Servico servico = new Servico(codigo, descricao, preco);
        return repository.salvar(servico);
    }
}
