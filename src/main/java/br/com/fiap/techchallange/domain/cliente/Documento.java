package br.com.fiap.techchallange.domain.cliente;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

public class Documento {

    private String valor;

    public Documento(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }
        String digits = valor.replaceAll("\\D", "");
        if (digits.length() != 11 && digits.length() != 14) {
            throw new IllegalArgumentException("Documento deve ter 11 (CPF) ou 14 (CNPJ) dígitos");
        }
        if (digits.length() == 11 && !validarCPF(digits)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        if (digits.length() == 14 && !validarCNPJ(digits)) {
            throw new IllegalArgumentException("CNPJ inválido");
        }
        this.valor = digits;
    }

    protected Documento() { }



    public String valor() {
        return valor;
    }

    public boolean isCPF() {
        return valor.length() == 11;
    }

    public boolean isCNPJ() {
        return valor.length() == 14;
    }

    private boolean validarCPF(String cpf) {
        if (cpf.chars().distinct().count() == 1) return false; // all digits equal

        int[] digits = cpf.chars().map(c -> c - '0').toArray();

        int sum = 0;
        for (int i = 0; i < 9; i++) sum += digits[i] * (10 - i);
        int mod = sum % 11;
        int dv1 = (mod < 2) ? 0 : 11 - mod;
        if (dv1 != digits[9]) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += digits[i] * (11 - i);
        mod = sum % 11;
        int dv2 = (mod < 2) ? 0 : 11 - mod;
        return dv2 == digits[10];
    }

    private boolean validarCNPJ(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) return false; // all digits equal

        int[] digits = cnpj.chars().map(c -> c - '0').toArray();

        int[] weight1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weight2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 12; i++) sum += digits[i] * weight1[i];
        int mod = sum % 11;
        int dv1 = (mod < 2) ? 0 : 11 - mod;
        if (dv1 != digits[12]) return false;

        sum = 0;
        for (int i = 0; i < 13; i++) sum += digits[i] * weight2[i];
        mod = sum % 11;
        int dv2 = (mod < 2) ? 0 : 11 - mod;
        return dv2 == digits[13];
    }


}
