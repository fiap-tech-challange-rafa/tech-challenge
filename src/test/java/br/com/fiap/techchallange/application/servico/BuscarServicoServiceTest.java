package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuscarServicoServiceTest {
    private ServicoRepository repository;
    private BuscarServicoService service;

    @BeforeEach
    void setUp() {
        repository = mock(ServicoRepository.class);
        service = new BuscarServicoService(repository);
    }

    @Test
    void deveBuscarPorId() {
        Servico s = new Servico("S001","Troca",BigDecimal.ONE);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(s));
        assertEquals("S001", service.porId(1L).getCodigo());
    }

    @Test
    void deveListarTodos() {
        when(repository.listarTodos()).thenReturn(List.of(new Servico("S001","desc",BigDecimal.ONE)));
        assertEquals(1, service.listarTodos().size());
    }
}
