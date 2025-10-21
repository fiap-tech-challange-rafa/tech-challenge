package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.domain.cliente.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtualizarClienteServiceTest {

    private ClienteRepository repository;
    private AtualizarClienteService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ClienteRepository.class);
        service = new AtualizarClienteService(repository);
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        Cliente existente = new Cliente("Rafa", new Documento("86579599090"), "9999", new Email("r@r.com"));
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.salvar(any())).thenReturn(existente);

        Cliente atualizado = service.executar(1L, "Rafaella Lima", "8888", "rafa@example.com");

        assertEquals("Rafaella Lima", atualizado.getNome());
        assertEquals("8888", atualizado.getTelefone());
        assertEquals("rafa@example.com", atualizado.getEmail().getValor());
    }

    @Test
    void deveLancarExcecaoSeClienteNaoExistir() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.executar(1L, "Novo Nome", "8888", "novo@email.com"));
    }
}
