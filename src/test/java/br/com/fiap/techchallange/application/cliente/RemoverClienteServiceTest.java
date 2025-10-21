package br.com.fiap.techchallange.application.cliente;

import br.com.fiap.techchallange.infrastructure.cliente.SpringDataClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class RemoverClienteServiceTest {

    private SpringDataClienteRepository repository;
    private RemoverClienteService service;

    @BeforeEach
    void setUp() {
        repository = mock(SpringDataClienteRepository.class);
        service = new RemoverClienteService(repository);
    }

    @Test
    void deveRemoverClientePorId() {
        service.executar(1L);
        verify(repository).deleteById(1L);
    }
}
