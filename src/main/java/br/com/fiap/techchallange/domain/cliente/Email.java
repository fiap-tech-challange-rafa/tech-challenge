package br.com.fiap.techchallange.domain.cliente;

import jakarta.persistence.Embeddable;

@Embeddable
public class Email {

    private  String valor;

    public Email(String valor) {
        if (valor == null || valor.isBlank()) {
            this.valor = null;
            throw new IllegalArgumentException("Email inválido");
        }
        String trimmed = valor.trim();
        if (!trimmed.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.valor = trimmed;
    }

    public Email() {

    }

    public String getValor() {
        return valor;
    }
}
