package br.com.fiap.techchallange.infrastructure.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaOrdemServicoRepositoryAdapter implements OrdemServicoRepository {

    private final SpringDataOrdemServicoRepository jpa;

    public JpaOrdemServicoRepositoryAdapter(SpringDataOrdemServicoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public OrdemServico salvar(OrdemServico os) {
        OrdemServicoEntity e = toEntity(os);
        OrdemServicoEntity saved = jpa.save(e);
        return toDomain(saved);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<OrdemServico> listarTodos() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    private OrdemServicoEntity toEntity(OrdemServico os) {
        OrdemServicoEntity e = new OrdemServicoEntity();
        e.setId(os.getId());
        e.setClienteId(os.getClienteId());
        e.setVeiculoId(os.getVeiculoId());
        e.setStatus(os.getStatus());
        e.setTotalOrcamento(os.getTotalOrcamento());
        e.setDataCriacao(os.getDataCriacao());
        e.setDataAtualizacao(os.getDataAtualizacao());

        e.getServicos().clear();
        for (ItemServico s : os.getServicos()) {
            ItemServicoEntity se = new ItemServicoEntity();
            se.setServicoId(s.getServicoId());
            se.setDescricao(s.getDescricao());
            se.setPreco(s.getPreco());
            se.setOrdem(e);
            e.getServicos().add(se);
        }

        e.getPecas().clear();
        for (ItemPeca p : os.getPecas()) {
            ItemPecaEntity pe = new ItemPecaEntity();
            pe.setPecaId(p.getId());
            pe.setNome(p.getNome());
            pe.setPrecoUnitario(p.getPrecoUnitario());
            pe.setQuantidade(p.getQuantidade());
            pe.setOrdem(e);
            e.getPecas().add(pe);
        }

        return e;
    }

    @Override
    public void removerTodos() {
        jpa.deleteAll();
    }

    private OrdemServico toDomain(OrdemServicoEntity e) {
        OrdemServico os = new OrdemServico(e.getClienteId(), e.getVeiculoId());
        os.setId(e.getId());

        try {
            os.aprovarOrcamento();
        } catch (Exception ignored) {
        }


        os = new OrdemServico(e.getClienteId(), e.getVeiculoId());
        os.setId(e.getId());
        for (ItemServicoEntity se : e.getServicos()) {
            os.incluirServico(new ItemServico(se.getServicoId(), se.getDescricao(), se.getPreco()));
        }
        for (ItemPecaEntity pe : e.getPecas()) {
            os.incluirPeca(new ItemPeca(pe.getPecaId(), pe.getNome(), pe.getPrecoUnitario(), pe.getQuantidade()));
        }
        os.gerarOrcamento();
        StatusOS persisted = StatusOS.valueOf(e.getStatus().name());
        switch (persisted) {
            case RECEBIDA:
                break;
            case EM_DIAGNOSTICO:
                break;
            case AGUARDANDO_APROVACAO:
                break;
            case EM_EXECUCAO:
                os.aprovarOrcamento();
                break;
            case FINALIZADA:
                os.aprovarOrcamento();
                os.finalizar();
                break;
            case ENTREGUE:
                os.aprovarOrcamento();
                os.finalizar();
                os.entregar();
                break;
            case CANCELADA:
                os.rejeitarOrcamento();
                break;
        }
        return os;
    }
}