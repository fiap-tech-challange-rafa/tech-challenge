package br.com.fiap.techchallange.domain.peca;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PecaTest {

    @Test
    void deveCriarPecaValida() {
        Peca p = new Peca("SKU123", "Filtro de óleo", new BigDecimal("50.00"), 10);

        assertEquals("SKU123", p.getSku());
        assertEquals("Filtro de óleo", p.getNome());
        assertEquals(new BigDecimal("50.00"), p.getPreco());
        assertEquals(10, p.getQuantidadeEstoque());
    }

    @Test
    void deveLancarExcecaoParaCamposInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Peca(null, "Nome", BigDecimal.ONE, 5));
        assertThrows(IllegalArgumentException.class, () -> new Peca("SKU", " ", BigDecimal.ONE, 5));
        assertThrows(IllegalArgumentException.class, () -> new Peca("SKU", "Nome", new BigDecimal("-1"), 5));
        assertThrows(IllegalArgumentException.class, () -> new Peca("SKU", "Nome", BigDecimal.ONE, -5));
    }

    @Test
    void deveAtualizarNomeEPrecoCorretamente() {
        Peca p = new Peca("SKU", "Filtro", new BigDecimal("100"), 5);
        p.atualizarDados("Filtro Novo", new BigDecimal("150"));

        assertEquals("Filtro Novo", p.getNome());
        assertEquals(new BigDecimal("150"), p.getPreco());
    }

    @Test
    void deveIgnorarAtualizacaoComCamposInvalidos() {
        Peca p = new Peca("SKU", "Filtro", new BigDecimal("100"), 5);
        p.atualizarDados(" ", null);
        assertEquals("Filtro", p.getNome());
        assertEquals(new BigDecimal("100"), p.getPreco());
    }

    @Test
    void deveCreditarEdebitarEstoqueCorretamente() {
        Peca p = new Peca("SKU", "Filtro", new BigDecimal("100"), 10);
        p.creditarEstoque(5);
        assertEquals(15, p.getQuantidadeEstoque());

        p.debitarEstoque(5);
        assertEquals(10, p.getQuantidadeEstoque());
    }

    @Test
    void deveLancarExcecaoSeDebitarMaisQueEstoque() {
        Peca p = new Peca("SKU", "Filtro", new BigDecimal("100"), 2);
        assertThrows(IllegalStateException.class, () -> p.debitarEstoque(3));
    }

    @Test
    void deveLancarExcecaoSeCreditarOuDebitarComValorInvalido() {
        Peca p = new Peca("SKU", "Filtro", new BigDecimal("100"), 5);
        assertThrows(IllegalArgumentException.class, () -> p.creditarEstoque(0));
        assertThrows(IllegalArgumentException.class, () -> p.debitarEstoque(0));
    }
}
