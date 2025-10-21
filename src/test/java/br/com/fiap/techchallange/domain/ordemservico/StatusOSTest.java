package br.com.fiap.techchallange.domain.ordemservico;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusOSTest {

    @Test
    void deveConterTodosOsStatusDefinidos() {
        StatusOS[] values = StatusOS.values();

        assertEquals(7, values.length);
        assertNotNull(StatusOS.valueOf("RECEBIDA"));
        assertNotNull(StatusOS.valueOf("EM_DIAGNOSTICO"));
        assertNotNull(StatusOS.valueOf("AGUARDANDO_APROVACAO"));
        assertNotNull(StatusOS.valueOf("EM_EXECUCAO"));
        assertNotNull(StatusOS.valueOf("FINALIZADA"));
        assertNotNull(StatusOS.valueOf("ENTREGUE"));
        assertNotNull(StatusOS.valueOf("CANCELADA"));
    }

    @Test
    void deveRetornarNomeCorretoDoEnum() {
        assertEquals("RECEBIDA", StatusOS.RECEBIDA.name());
        assertEquals("FINALIZADA", StatusOS.FINALIZADA.name());
    }
}
