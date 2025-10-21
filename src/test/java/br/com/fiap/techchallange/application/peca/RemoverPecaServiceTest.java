package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class RemoverPecaServiceTest {
    private PecaRepository repository;
    private RemoverPecaService service;

    @BeforeEach
    void setUp() {
        repository = mock(PecaRepository.class);
        service = new RemoverPecaService(repository);
    }

    @Test
    void deveRemoverPecaPorId() {
        service.executar(1L);
        verify(repository).remover(1L);
    }
}
