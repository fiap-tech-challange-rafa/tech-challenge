package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EntregarOrdemServicoServiceTest {

    private OrdemServicoRepositoryPort repository;
    private EntregarOrdemServicoService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepositoryPort.class);
        service = new EntregarOrdemServicoService(repository);
    }

    @Test
    void deveEntregarOSComSucesso() {
        OrdemServico os = new OrdemServico(1L, 2L);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(repository.salvar(any())).thenReturn(os);

        OrdemServico result = service.executar(1L);

        assertEquals(StatusOS.ENTREGUE, result.getStatus());
        verify(repository).salvar(os);
    }

    @Test
    void deveLancarExcecaoSeOSNaoExistir() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.executar(1L));
    }
}
