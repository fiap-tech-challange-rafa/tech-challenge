package br.com.fiap.techchallange.interfaces.rest.veiculo;

public record VeiculoRequest(Long clienteId, String placa, String marca, String modelo, Integer ano) {}
