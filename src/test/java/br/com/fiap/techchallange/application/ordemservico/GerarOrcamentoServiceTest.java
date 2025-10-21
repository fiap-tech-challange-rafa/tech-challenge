package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GerarOrcamentoServiceTest {

    private OrdemServicoRepository repository;
    private GerarOrcamentoService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepository.class);
        service = new GerarOrcamentoService(repository);
    }

    @Test
    void deveGerarOrcamentoComSucesso() {
        OrdemServico os = new OrdemServico(1L, 2L);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(repository.salvar(any())).thenReturn(os);

        OrdemServico result = service.executar(1L);

        assertNotNull(result);
        verify(repository).salvar(os);
    }

    @Test
    void deveLancarExcecaoSeOSNaoExistir() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.executar(1L));
    }
}
