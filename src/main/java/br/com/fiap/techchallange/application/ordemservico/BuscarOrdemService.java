package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarOrdemService {

    private final OrdemServicoRepository repository;

    public BuscarOrdemService(OrdemServicoRepository repository) {
        this.repository = repository;
    }

    public OrdemServico porId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada: " + id));
    }

    public List<OrdemServico> listarTodos() {
        return repository.listarTodos();
    }

    public void remover(Long id) {
        repository.remover(id);
    }
}