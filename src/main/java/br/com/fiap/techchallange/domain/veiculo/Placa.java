package br.com.fiap.techchallange.domain.veiculo;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Placa {
    private static final Pattern PLACA_MERCOSUL = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");
    private static final Pattern PLACA_ANTIGA = Pattern.compile("^[A-Z]{3}-?\\d{4}$");

    private final String valor;

    public Placa(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Placa é obrigatória");
        }
        String cleaned = valor.replaceAll("\\s|-", "").toUpperCase();
        if (!PLACA_MERCOSUL.matcher(cleaned).matches() && !PLACA_ANTIGA.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Placa inválida: " + valor);
        }
        this.valor = cleaned;
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Placa)) return false;
        Placa placa = (Placa) o;
        return valor.equals(placa.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
