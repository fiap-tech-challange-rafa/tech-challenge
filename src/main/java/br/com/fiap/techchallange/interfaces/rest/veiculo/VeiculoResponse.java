package br.com.fiap.techchallange.interfaces.rest.veiculo;

import br.com.fiap.techchallange.domain.veiculo.Veiculo;

public record VeiculoResponse(Long id, Long clienteId, String placa, String marca, String modelo, Integer ano) {
    public static VeiculoResponse fromDomain(Veiculo v) {
        return new VeiculoResponse(v.getId(), v.getClienteId(), v.getPlaca().valor(), v.getMarca(), v.getModelo(), v.getAno());
    }
}