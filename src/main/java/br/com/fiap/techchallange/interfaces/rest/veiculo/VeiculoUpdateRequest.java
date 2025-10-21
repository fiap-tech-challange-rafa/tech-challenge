package br.com.fiap.techchallange.interfaces.rest.veiculo;

public record VeiculoUpdateRequest(String placa, String marca, String modelo, Integer ano) {}
