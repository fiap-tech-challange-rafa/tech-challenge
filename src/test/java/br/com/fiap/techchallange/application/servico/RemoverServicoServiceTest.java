package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class RemoverServicoServiceTest {
    private ServicoRepository repository;
    private RemoverServicoService service;

    @BeforeEach
    void setUp() {
        repository = mock(ServicoRepository.class);
        service = new RemoverServicoService(repository);
    }

    @Test
    void deveRemoverServicoPorId() {
        service.executar(1L);
        verify(repository).remover(1L);
    }
}
