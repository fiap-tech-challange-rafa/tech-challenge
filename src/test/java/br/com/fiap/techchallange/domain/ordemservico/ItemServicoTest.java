package br.com.fiap.techchallange.domain.ordemservico;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemServicoTest {

    @Test
    void deveCriarItemServicoCorretamente() {
        ItemServico item = new ItemServico(1L, "Troca de óleo", new BigDecimal("150.00"));

        assertEquals(1L, item.getServicoId());
        assertEquals("Troca de óleo", item.getDescricao());
        assertEquals(new BigDecimal("150.00"), item.getPreco());
    }

    @Test
    void devePermitirValoresNulosSemExplodir() {
        ItemServico item = new ItemServico(null, null, null);

        assertNull(item.getServicoId());
        assertNull(item.getDescricao());
        assertNull(item.getPreco());
    }
}
