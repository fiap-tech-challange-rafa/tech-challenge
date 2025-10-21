package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AjustarEstoqueService {

    private final PecaRepository repository;

    public AjustarEstoqueService(PecaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Peca ajustar(Long id, int quantidade) {
        if (quantidade == 0) throw new IllegalArgumentException("Quantidade de ajuste não pode ser zero");
        Peca p = repository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Peça não encontrada: " + id));
        if (quantidade > 0) {
            p.creditarEstoque(quantidade);
        } else {
            p.debitarEstoque(Math.abs(quantidade));
        }
        return repository.salvar(p);
    }
}
