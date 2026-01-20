package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BuscarOrdemServiceTest {

    private OrdemServicoRepositoryPort repository;
    private BuscarOrdemService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepositoryPort.class);
        service = new BuscarOrdemService(repository);
    }

    @Test
    void deveBuscarPorId() {
        OrdemServico os = new OrdemServico(1L, 2L);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));

        OrdemServico result = service.porId(1L);
        assertEquals(1L, result.getClienteId());
    }

    @Test
    void deveListarTodasOS() {
        when(repository.listarTodos()).thenReturn(List.of(new OrdemServico(1L, 2L)));
        assertEquals(1, service.listarTodos().size());
    }


}
