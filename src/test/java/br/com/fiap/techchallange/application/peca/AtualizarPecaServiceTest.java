package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtualizarPecaServiceTest {
    private PecaRepository repository;
    private AtualizarPecaService service;

    @BeforeEach
    void setUp() {
        repository = mock(PecaRepository.class);
        service = new AtualizarPecaService(repository);
    }

    @Test
    void deveAtualizarPecaExistente() {
        Peca p = new Peca("SKU","Filtro",new BigDecimal("50"),10);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(p));
        when(repository.salvar(any())).thenReturn(p);

        Peca result = service.executar(1L,"Filtro Novo",new BigDecimal("75"));
        assertEquals("Filtro Novo", result.getNome());
        assertEquals(new BigDecimal("75"), result.getPreco());
    }

    @Test
    void deveLancarExcecaoSePecaNaoEncontrada() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                service.executar(1L,"x",BigDecimal.ONE));
    }
}
