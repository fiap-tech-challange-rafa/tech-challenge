package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RemoverVeiculoServiceTest {

    private VeiculoRepository repository;
    private RemoverVeiculoService service;

    @BeforeEach
    void setUp() {
        repository = mock(VeiculoRepository.class);
        service = new RemoverVeiculoService(repository);
    }

    @Test
    void deveRemoverVeiculoPorId() {
        service.executar(1L);
        verify(repository).remover(1L);
    }
}
