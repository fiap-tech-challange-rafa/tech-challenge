package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastrarServicoServiceTest {
    private ServicoRepository repository;
    private CadastrarServicoService service;

    @BeforeEach
    void setUp() {
        repository = mock(ServicoRepository.class);
        service = new CadastrarServicoService(repository);
    }

    @Test
    void deveCadastrarServicoNovo() {
        when(repository.buscarPorCodigo("S001")).thenReturn(Optional.empty());
        Servico salvo = new Servico("S001","Troca de óleo",new BigDecimal("100"));
        when(repository.salvar(any())).thenReturn(salvo);

        Servico result = service.executar("S001","Troca de óleo",new BigDecimal("100"));
        assertEquals("S001", result.getCodigo());
        verify(repository).salvar(any());
    }

    @Test
    void deveLancarExcecaoSeCodigoDuplicado() {
        when(repository.buscarPorCodigo("S001"))
                .thenReturn(Optional.of(new Servico("S001","desc",BigDecimal.ONE)));

        assertThrows(IllegalStateException.class, () ->
                service.executar("S001","desc",BigDecimal.TEN));
    }
}
