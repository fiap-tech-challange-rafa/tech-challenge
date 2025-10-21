package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuscarPecaServiceTest {
    private PecaRepository repository;
    private BuscarPecaService service;

    @BeforeEach
    void setUp() {
        repository = mock(PecaRepository.class);
        service = new BuscarPecaService(repository);
    }

    @Test
    void deveRetornarPecaPorId() {
        Peca p = new Peca("SKU","Filtro",new BigDecimal("50"),1);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(p));
        assertEquals("SKU", service.porId(1L).getSku());
    }

    @Test
    void deveListarTodos() {
        when(repository.listarTodos()).thenReturn(List.of(new Peca("SKU","Filtro",BigDecimal.ONE,1)));
        assertEquals(1, service.listarTodos().size());
    }
}
