package br.com.fiap.techchallange.domain.veiculo;

import jakarta.persistence.*;

public class Veiculo {

    private Long id;

    private Long clienteId;

    private Placa placa;

    private String marca;
    private String modelo;
    private Integer ano;

    public Veiculo(Long clienteId, Placa placa, String marca, String modelo, Integer ano) {
        if (clienteId == null) throw new IllegalArgumentException("clienteId é obrigatório");
        if (placa == null) throw new IllegalArgumentException("placa é obrigatória");
        // ...validações simples...
        this.clienteId = clienteId;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
    }

    public Veiculo() {

    }

    public void setId(Long id){ this.id = id; }
    public Long getId(){ return id; }
    public Long getClienteId(){ return clienteId; }
    public Placa getPlaca(){ return placa; }
    public String getMarca(){ return marca; }
    public String getModelo(){ return modelo; }
    public Integer getAno(){ return ano; }

    public void atualizarDados(Placa novaPlaca, String novaMarca, String novoModelo, Integer novoAno) {
        if (novaPlaca != null) this.placa = novaPlaca;
        if (novaMarca != null && !novaMarca.isBlank()) this.marca = novaMarca;
        if (novoModelo != null && !novoModelo.isBlank()) this.modelo = novoModelo;
        if (novoAno != null && novoAno >= 1886) this.ano = novoAno;
    }
}
