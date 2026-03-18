package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.in.BuscarOrdemPort;
import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.StatusOS;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BuscarOrdemService implements BuscarOrdemPort {

    private final OrdemServicoRepositoryPort repository;

    public BuscarOrdemService(OrdemServicoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(value = "ordem-servico", key = "#id", unless = "#result == null")
    public OrdemServico porId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada: " + id));
    }

    @Override
    @Cacheable(value = "ordem-servico-lista", unless = "#result == null || #result.isEmpty()")
    public List<OrdemServico> listarTodos() {
        List<OrdemServico> all = repository.listarTodos();
        // Excluir OS finalizadas e entregues da listagem (filtragem lógica)
        return all.stream()
                .filter(os -> os.getStatus() != StatusOS.FINALIZADA && os.getStatus() != StatusOS.ENTREGUE)
                .sorted(Comparator
                        .comparingInt((OrdemServico os) -> statusPriority(os.getStatus()))
                        .thenComparing(OrdemServico::getDataCriacao)) // mais antigas primeiro
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "ordem-servico", key = "#id")
    public void remover(Long id) {
        Optional<OrdemServico> maybe = repository.buscarPorId(id);
        OrdemServico os = maybe.orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada: " + id));
        // Remoção lógica: marcar como CANCELADA (não remove fisicamente)
        os.rejeitarOrcamento();
        repository.salvar(os);
    }

    private int statusPriority(StatusOS status) {
        // Prioridade: EM_EXECUCAO > AGUARDANDO_APROVACAO > EM_DIAGNOSTICO > RECEBIDA
        return switch (status) {
            case EM_EXECUCAO -> 0;
            case AGUARDANDO_APROVACAO -> 1;
            case EM_DIAGNOSTICO -> 2;
            case RECEBIDA -> 3;
            default -> 4;
        };
    }
}