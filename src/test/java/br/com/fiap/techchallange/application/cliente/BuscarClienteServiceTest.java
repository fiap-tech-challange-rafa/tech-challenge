package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.domain.cliente.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuscarClienteServiceTest {

    private ClienteRepository repository;
    private BuscarClienteService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ClienteRepository.class);
        service = new BuscarClienteService(repository);
    }

    @Test
    void deveRetornarClientePorId() {
        Cliente c = new Cliente("Rafa", new Documento("86579599090"), "9999", new Email("r@r.com"));
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(c));

        Cliente result = service.porId(1L);

        assertEquals("Rafa", result.getNome());
    }

    @Test
    void deveLancarExcecaoSeClienteNaoEncontradoPorId() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.porId(1L));
    }

    @Test
    void deveBuscarPorDocumento() {
        Cliente c = new Cliente("Rafa", new Documento("86579599090"), "9999", new Email("r@r.com"));
        when(repository.buscarPorDocumento("123")).thenReturn(Optional.of(c));

        Cliente result = service.porDocumento("123");
        assertEquals("Rafa", result.getNome());
    }

    @Test
    void deveListarTodos() {
        when(repository.listarTodos()).thenReturn(List.of(
                new Cliente("Rafa", new Documento("86579599090"), "9999", null),
                new Cliente("Ana", new Documento("86579599090"), "8888", null)
        ));

        List<Cliente> lista = service.listarTodos();

        assertEquals(2, lista.size());
    }
}
