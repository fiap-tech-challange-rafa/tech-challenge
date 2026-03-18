package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RejeitarOrcamentoServiceTest {

    private OrdemServicoRepositoryPort repository;
    private RejeitarOrcamentoService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepositoryPort.class);
        service = new RejeitarOrcamentoService(repository);
    }

    @Test
    void deveRejeitarOrcamento() {
        OrdemServico os = new OrdemServico(1L, 2L);
        os.gerarOrcamento(); // Coloca o status em AGUARDANDO_APROVACAO
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(repository.salvar(any())).thenReturn(os);

        OrdemServico result = service.executar(1L);

        assertEquals(StatusOS.CANCELADA, result.getStatus());
        verify(repository).salvar(os);
    }

    @Test
    void deveLancarExcecaoSeOSNaoExistir() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.executar(1L));
    }

    @Test
    void deveLancarExcecaoSeOSNaoEstaAguardandoAprovacao() {
        OrdemServico os = new OrdemServico(1L, 2L);
        // Deixar em status RECEBIDA (não modificar)
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));

        assertThrows(IllegalStateException.class, () -> service.executar(1L));
    }
}
