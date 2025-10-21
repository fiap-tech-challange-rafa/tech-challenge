package br.com.fiap.techchallange.domain.veiculo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlacaTest {

    @Test
    void deveAceitarPlacaAntigaComTracoOuSem() {
        assertDoesNotThrow(() -> new Placa("ABC-1234"));
        assertDoesNotThrow(() -> new Placa("ABC1234"));
    }

    @Test
    void deveAceitarPlacaMercosul() {
        assertDoesNotThrow(() -> new Placa("BRA2E19"));
    }

    @Test
    void deveLancarExcecaoParaPlacaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Placa("A1B2C3"));
        assertThrows(IllegalArgumentException.class, () -> new Placa(""));
        assertThrows(IllegalArgumentException.class, () -> new Placa("1234567"));
    }

    @Test
    void deveRemoverEspacosETraços() {
        Placa placa = new Placa(" a b c - 1 2 3 4 ");
        assertEquals("ABC1234", placa.valor());
    }

    @Test
    void deveImplementarEqualsEHashCodeCorretamente() {
        Placa p1 = new Placa("ABC1234");
        Placa p2 = new Placa("ABC-1234");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void deveGerarToStringComValor() {
        Placa placa = new Placa("ABC1234");
        assertEquals("ABC1234", placa.toString());
    }
}
