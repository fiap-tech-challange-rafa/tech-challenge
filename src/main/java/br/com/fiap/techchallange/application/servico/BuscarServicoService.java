package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarServicoService {

    private final ServicoRepository repository;

    public BuscarServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public Servico porId(Long id) {
        return repository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
    }

    public List<Servico> listarTodos() {
        return repository.listarTodos();
    }
}
