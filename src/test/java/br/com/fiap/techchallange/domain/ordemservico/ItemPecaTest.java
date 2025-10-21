package br.com.fiap.techchallange.domain.ordemservico;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemPecaTest {

    @Test
    void deveCriarItemPecaCorretamente() {
        ItemPeca item = new ItemPeca(1L, "Filtro de óleo", new BigDecimal("50.00"), 2);

        assertEquals(1L, item.getId());
        assertEquals("Filtro de óleo", item.getNome());
        assertEquals(new BigDecimal("50.00"), item.getPrecoUnitario());
        assertEquals(2, item.getQuantidade());
    }

    @Test
    void deveCalcularSubtotalCorretamente() {
        ItemPeca item = new ItemPeca(1L, "Pneu", new BigDecimal("300.00"), 4);
        BigDecimal subtotal = item.getSubtotal();

        assertEquals(new BigDecimal("1200.00"), subtotal);
    }

    @Test
    void deveRetornarZeroSePrecoOuQuantidadeForNula() {
        ItemPeca item1 = new ItemPeca(1L, "Peça", null, 3);
        assertThrows(NullPointerException.class, item1::getSubtotal);

        ItemPeca item2 = new ItemPeca(1L, "Peça", new BigDecimal("10.00"), null);
        assertThrows(NullPointerException.class, item2::getSubtotal);
    }
}
