package br.com.fiap.techchallange.domain.cliente;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class DocumentoTest {

    @Test
    void deveCriarDocumentoValido() {
        Documento doc = new Documento("63482672024");
        assertEquals("63482672024", doc.valor());
    }

    @Test
    void deveRemoverEspacosExtras() {
        Documento doc = new Documento("   98765432100   ");
        assertEquals("98765432100", doc.valor());
    }

    @Test
    void deveLancarExcecaoParaDocumentoNuloOuVazio() {
        assertThrows(IllegalArgumentException.class, () -> new Documento(null));
        assertThrows(IllegalArgumentException.class, () -> new Documento("   "));
    }
}
