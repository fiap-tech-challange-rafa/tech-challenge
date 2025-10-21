package br.com.fiap.techchallange.application.peca;

import br.com.fiap.techchallange.domain.peca.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastrarPecaServiceTest {
    private PecaRepository repository;
    private CadastrarPecaService service;

    @BeforeEach
    void setUp() {
        repository = mock(PecaRepository.class);
        service = new CadastrarPecaService(repository);
    }

    @Test
    void deveCadastrarPecaNova() {
        when(repository.buscarPorSku("SKU123")).thenReturn(Optional.empty());
        Peca salva = new Peca("SKU123","Filtro",new BigDecimal("50"),5);
        when(repository.salvar(any())).thenReturn(salva);

        Peca result = service.executar("SKU123","Filtro",new BigDecimal("50"),5);
        assertEquals("SKU123", result.getSku());
        verify(repository).salvar(any());
    }

    @Test
    void deveLancarExcecaoParaSkuDuplicado() {
        when(repository.buscarPorSku("SKU123"))
                .thenReturn(Optional.of(new Peca("SKU123","Filtro",BigDecimal.ONE,1)));
        assertThrows(IllegalStateException.class, () ->
                service.executar("SKU123","Filtro",BigDecimal.ONE,1));
    }
}
