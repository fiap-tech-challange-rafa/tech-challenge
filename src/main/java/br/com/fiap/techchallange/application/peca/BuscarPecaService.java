package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarPecaService {

    private final PecaRepository repository;

    public BuscarPecaService(PecaRepository repository) {
        this.repository = repository;
    }

    public Peca porId(Long id) {
        return repository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
    }

    public List<Peca> listarTodos() {
        return repository.listarTodos();
    }
}
