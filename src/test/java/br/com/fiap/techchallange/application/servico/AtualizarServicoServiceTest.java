package br.com.fiap.techchallange.application.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtualizarServicoServiceTest {
    private ServicoRepository repository;
    private AtualizarServicoService service;

    @BeforeEach
    void setUp() {
        repository = mock(ServicoRepository.class);
        service = new AtualizarServicoService(repository);
    }

    @Test
    void deveAtualizarServicoExistente() {
        Servico existente = new Servico("S001","Troca de óleo",new BigDecimal("100"));
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.salvar(any())).thenReturn(existente);

        Servico atualizado = service.executar(1L,"Revisão completa",new BigDecimal("250"));
        assertEquals("Revisão completa", atualizado.getDescricao());
        assertEquals(new BigDecimal("250"), atualizado.getPreco());
    }

    @Test
    void deveLancarExcecaoSeNaoEncontrarServico() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                service.executar(1L,"nova",BigDecimal.ONE));
    }
}
