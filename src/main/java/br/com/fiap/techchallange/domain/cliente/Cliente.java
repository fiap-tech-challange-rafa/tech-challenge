package br.com.fiap.techchallange.domain.cliente;


import jakarta.persistence.*;

public class Cliente {

    private Long id;

    private String nome;

    private Documento documento;

    private String telefone;

    private Email email;

    public Cliente(String nome, Documento documento, String telefone, Email email) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (documento == null) throw new IllegalArgumentException("Documento é obrigatório");
        this.nome = nome.trim();
        this.documento = documento;
        this.telefone = (telefone == null || telefone.isBlank()) ? null : telefone.trim();
        this.email = email;
    }

    public Cliente() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Documento getDocumento() { return documento; }
    public String getTelefone() { return telefone; }
    public Email getEmail() { return email; }

    public void atualizarDados(String novoNome, String novoTelefone, Email novoEmail) {
        if (novoNome != null && !novoNome.isBlank()) this.nome = novoNome.trim();
        if (novoTelefone != null) this.telefone = novoTelefone.isBlank() ? null : novoTelefone.trim();
        if (novoEmail != null) this.email = novoEmail;
    }
}
