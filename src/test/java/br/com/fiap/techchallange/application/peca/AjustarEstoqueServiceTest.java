package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AjustarEstoqueServiceTest {
    private PecaRepository repository;
    private AjustarEstoqueService service;

    @BeforeEach
    void setUp() {
        repository = mock(PecaRepository.class);
        service = new AjustarEstoqueService(repository);
    }

    @Test
    void deveCreditarEstoque() {
        Peca p = new Peca("SKU","Filtro",new BigDecimal("50"),5);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(p));
        when(repository.salvar(any())).thenReturn(p);

        Peca result = service.ajustar(1L,5);
        assertEquals(10, result.getQuantidadeEstoque());
    }

    @Test
    void deveDebitarEstoque() {
        Peca p = new Peca("SKU","Filtro",new BigDecimal("50"),5);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(p));
        when(repository.salvar(any())).thenReturn(p);

        Peca result = service.ajustar(1L,-3);
        assertEquals(2, result.getQuantidadeEstoque());
    }

    @Test
    void deveLancarExcecaoParaQuantidadeZero() {
        assertThrows(IllegalArgumentException.class, () -> service.ajustar(1L,0));
    }

    @Test
    void deveLancarExcecaoSePecaNaoExistir() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.ajustar(1L,5));
    }
}
