package br.com.fiap.techchallange.domain.peca;

import java.util.List;
import java.util.Optional;

public interface PecaRepository {
    Peca salvar(Peca peca);
    Optional<Peca> buscarPorId(Long id);
    Optional<Peca> buscarPorSku(String sku);
    List<Peca> listarTodos();
    void remover(Long id);
    void removerTodos();
}
