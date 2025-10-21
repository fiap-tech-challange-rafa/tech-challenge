package br.com.fiap.techchallange.domain.cliente;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ClienteTest {

    @Test
    void deveCriarClienteComDadosValidos() {
        Documento doc = new Documento("63482672024");
        Email email = new Email("rafaella@example.com");
        Cliente cliente = new Cliente("Rafaella", doc, "99999-9999", email);

        assertNotNull(cliente);
        assertEquals("Rafaella", cliente.getNome());
        assertEquals(doc, cliente.getDocumento());
        assertEquals("99999-9999", cliente.getTelefone());
        assertEquals(email, cliente.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNuloOuVazio() {
        Documento doc = new Documento("63482672024");
        Email email = new Email("teste@example.com");

        assertThrows(IllegalArgumentException.class,
                () -> new Cliente(null, doc, "99999-9999", email));

        assertThrows(IllegalArgumentException.class,
                () -> new Cliente("   ", doc, "99999-9999", email));
    }

    @Test
    void deveLancarExcecaoQuandoDocumentoForNulo() {
        Email email = new Email("teste@example.com");
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente("Rafaella", null, "99999-9999", email));
    }

    @Test
    void devePermitirTelefoneNuloOuEmBranco() {
        Documento doc = new Documento("63482672024");
        Email email = new Email("teste@example.com");

        Cliente c1 = new Cliente("Rafaella", doc, null, email);
        assertNull(c1.getTelefone());

        Cliente c2 = new Cliente("Rafaella", doc, "   ", email);
        assertNull(c2.getTelefone());
    }

    @Test
    void deveAtualizarNomeTelefoneEEmail() {
        Documento doc = new Documento("63482672024");
        Email emailAntigo = new Email("rafaella@old.com");
        Cliente cliente = new Cliente("Rafaella", doc, "99999-9999", emailAntigo);

        Email emailNovo = new Email("rafaella@new.com");
        cliente.atualizarDados("Rafaella Lima", "88888-8888", emailNovo);

        assertEquals("Rafaella Lima", cliente.getNome());
        assertEquals("88888-8888", cliente.getTelefone());
        assertEquals(emailNovo, cliente.getEmail());
    }

    @Test
    void deveIgnorarCamposNulosAoAtualizar() {
        Documento doc = new Documento("63482672024");
        Email email = new Email("rafaella@example.com");
        Cliente cliente = new Cliente("Rafaella", doc, "99999-9999", email);

        cliente.atualizarDados(null, null, null);

        assertEquals("Rafaella", cliente.getNome());
        assertEquals("99999-9999", cliente.getTelefone());
        assertEquals(email, cliente.getEmail());
    }

    @Test
    void deveDefinirTelefoneComoNuloSeAtualizadoComVazio() {
        Documento doc = new Documento("63482672024");
        Email email = new Email("rafaella@example.com");
        Cliente cliente = new Cliente("Rafaella", doc, "99999-9999", email);

        cliente.atualizarDados(null, "   ", null);

        assertNull(cliente.getTelefone());
    }

    @Test
    void deveRemoverEspacosExtrasNoNomeETelefone() {
        Documento doc = new Documento("63482672024");
        Email email = new Email("rafaella@example.com");
        Cliente cliente = new Cliente("   Rafaella   ", doc, "   99999-9999   ", email);

        assertEquals("Rafaella", cliente.getNome());
        assertEquals("99999-9999", cliente.getTelefone());
    }
}
