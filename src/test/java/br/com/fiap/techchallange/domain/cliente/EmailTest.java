package br.com.fiap.techchallange.domain.cliente;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class EmailTest {

    @Test
    void deveCriarEmailValido() {
        Email email = new Email("rafaella@example.com");
        assertEquals("rafaella@example.com", email.getValor());
    }

    @Test
    void deveRemoverEspacosExtras() {
        Email email = new Email("   rafaella@example.com   ");
        assertEquals("rafaella@example.com", email.getValor());
    }

    @Test
    void deveLancarExcecaoParaEmailNuloOuSemArroba() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
        assertThrows(IllegalArgumentException.class, () -> new Email("semarroba"));
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }
}
