package br.com.fiap.techchallange.domain.servico;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ServicoTest {

    @Test
    void deveCriarServicoValido() {
        Servico s = new Servico("COD001", "Troca de óleo", new BigDecimal("120.00"));

        assertEquals("COD001", s.getCodigo());
        assertEquals("Troca de óleo", s.getDescricao());
        assertEquals(new BigDecimal("120.00"), s.getPreco());
    }

    @Test
    void deveLancarExcecaoParaCamposInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Servico(null, "desc", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Servico("  ", "desc", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Servico("COD", "  ", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Servico("COD", "desc", new BigDecimal("-1")));
    }

    @Test
    void deveAtualizarDescricaoEPrecoCorretamente() {
        Servico s = new Servico("COD001", "Troca de óleo", new BigDecimal("120.00"));
        s.atualizar("Revisão completa", new BigDecimal("200.00"));

        assertEquals("Revisão completa", s.getDescricao());
        assertEquals(new BigDecimal("200.00"), s.getPreco());
    }

    @Test
    void deveIgnorarAtualizacaoComCamposInvalidos() {
        Servico s = new Servico("COD001", "Troca de óleo", new BigDecimal("120.00"));
        s.atualizar(" ", null);
        assertEquals("Troca de óleo", s.getDescricao());
        assertEquals(new BigDecimal("120.00"), s.getPreco());
    }

    @Test
    void deveLancarExcecaoSePrecoNegativoNaAtualizacao() {
        Servico s = new Servico("COD001", "Troca de óleo", new BigDecimal("120.00"));
        assertThrows(IllegalArgumentException.class, () -> s.atualizar("Troca de filtro", new BigDecimal("-10.00")));
    }
}
