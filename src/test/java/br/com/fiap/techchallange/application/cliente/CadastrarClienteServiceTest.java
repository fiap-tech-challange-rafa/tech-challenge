package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.domain.cliente.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastrarClienteServiceTest {

    private ClienteRepository repository;
    private CadastrarClienteService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ClienteRepository.class);
        service = new CadastrarClienteService(repository);
    }

    @Test
    void deveCadastrarClienteQuandoDocumentoNaoExistir() {
        when(repository.buscarPorDocumento("123")).thenReturn(Optional.empty());

        Cliente salvo = new Cliente("Rafaella", new Documento("86579599090"), "99999-9999", new Email("rafa@example.com"));
        when(repository.salvar(any(Cliente.class))).thenReturn(salvo);

        Cliente result = service.executar("Rafaella", "86579599090", "99999-9999", "rafa@example.com");

        assertEquals("Rafaella", result.getNome());
        verify(repository).salvar(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoDocumentoJaExistir() {
        Cliente existente = new Cliente("Rafa", new Documento("86579599090"), "99999-9999", new Email("r@r.com"));
        when(repository.buscarPorDocumento("86579599090")).thenReturn(Optional.of(existente));

        assertThrows(IllegalStateException.class, () ->
                service.executar("Rafaella", "86579599090", "99999-9999", "rafa@example.com"));
    }
}
