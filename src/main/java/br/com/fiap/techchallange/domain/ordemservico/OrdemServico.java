package br.com.fiap.techchallange.domain.ordemservico;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdemServico {

    private Long id;

    private Long clienteId;

    private Long veiculoId;

    private StatusOS status = StatusOS.RECEBIDA;

    private List<ItemServico> servicos = new ArrayList<>();

    private List<ItemPeca> pecas = new ArrayList<>();

    private BigDecimal totalOrcamento = BigDecimal.ZERO;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    public OrdemServico(Long clienteId, Long veiculoId) {
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
    }

    public OrdemServico() {

    }

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public Long getVeiculoId() { return veiculoId; }
    public StatusOS getStatus() { return status; }
    public List<ItemServico> getServicos() { return servicos; }
    public List<ItemPeca> getPecas() { return pecas; }
    public BigDecimal getTotalOrcamento() { return totalOrcamento; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    public void incluirServico(ItemServico s) {
        servicos.add(s);
        status = StatusOS.EM_DIAGNOSTICO;
        recalcularOrcamento();
    }

    public void incluirPeca(ItemPeca p) {
        pecas.add(p);
        status = StatusOS.EM_DIAGNOSTICO;
        recalcularOrcamento();
    }

    public void gerarOrcamento() {
        recalcularOrcamento();
        status = StatusOS.AGUARDANDO_APROVACAO;
    }

    public void aprovarOrcamento() {
        status = StatusOS.EM_EXECUCAO;
    }

    public void rejeitarOrcamento() {
        status = StatusOS.CANCELADA;
    }

    public void finalizar() {
        status = StatusOS.FINALIZADA;
    }

    public void entregar() {
        status = StatusOS.ENTREGUE;
    }

    private void recalcularOrcamento() {
        BigDecimal totalServicos = servicos.stream()
                .map(ItemServico::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPecas = pecas.stream()
                .map(ItemPeca::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalOrcamento = totalServicos.add(totalPecas);
        dataAtualizacao = LocalDateTime.now();
    }
}
