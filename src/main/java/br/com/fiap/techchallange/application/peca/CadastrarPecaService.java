package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CadastrarPecaService {

    private final PecaRepository repository;

    public CadastrarPecaService(PecaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Peca executar(String sku, String nome, BigDecimal preco, Integer quantidadeEstoque) {
        String normalizedSku = sku == null ? null : sku.trim();
        repository.buscarPorSku(normalizedSku).ifPresent(p -> {
            throw new IllegalStateException("SKU já cadastrado: " + normalizedSku);
        });

        Peca peca = new Peca(normalizedSku, nome, preco, quantidadeEstoque);
        return repository.salvar(peca);
    }
}
