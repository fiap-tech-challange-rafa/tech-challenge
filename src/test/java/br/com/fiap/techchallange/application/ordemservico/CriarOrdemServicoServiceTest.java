package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CriarOrdemServicoServiceTest {

    private OrdemServicoRepository repository;
    private CriarOrdemServicoService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepository.class);
        service = new CriarOrdemServicoService(repository);
    }

    @Test
    void deveCriarNovaOrdemDeServico() {
        OrdemServico os = new OrdemServico(1L, 2L);
        when(repository.salvar(any())).thenReturn(os);

        OrdemServico result = service.executar(1L, 2L);

        assertEquals(1L, result.getClienteId());
        assertEquals(2L, result.getVeiculoId());
        verify(repository).salvar(any(OrdemServico.class));
    }
}
