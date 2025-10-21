package br.com.fiap.techchallange.interfaces.rest.ordemservico;


import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.StatusOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrdemServicoResponse(Long id,
                                   Long clienteId,
                                   Long veiculoId,
                                   StatusOS status,
                                   BigDecimal totalOrcamento,
                                   LocalDateTime dataCriacao,
                                   LocalDateTime dataAtualizacao,
                                   List<ItemServicoDto> servicos,
                                   List<ItemPecaDto> pecas) {

    public static OrdemServicoResponse fromDomain(OrdemServico os) {
        List<ItemServicoDto> s = os.getServicos().stream()
                .map(i -> new ItemServicoDto(i.getServicoId(), i.getDescricao(), i.getPreco()))
                .collect(Collectors.toList());
        List<ItemPecaDto> p = os.getPecas().stream()
                .map(i -> new ItemPecaDto(i.getId(), i.getNome(), i.getPrecoUnitario(), i.getQuantidade()))
                .collect(Collectors.toList());
        return new OrdemServicoResponse(os.getId(), os.getClienteId(), os.getVeiculoId(),
                os.getStatus(), os.getTotalOrcamento(), os.getDataCriacao(), os.getDataAtualizacao(), s, p);
    }

    public record ItemServicoDto(Long servicoId, String descricao, java.math.BigDecimal preco) {}
    public record ItemPecaDto(Long pecaId, String nome, java.math.BigDecimal precoUnitario, Integer quantidade) {}
}
