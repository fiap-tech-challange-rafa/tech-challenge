package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServicoRepository;
import br.com.fiap.techchallange.domain.ordemservico.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AprovarOrcamentoServiceTest {

    private OrdemServicoRepositoryPort repository;
    private AprovarOrcamentoService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepositoryPort.class);
        service = new AprovarOrcamentoService(repository);
    }

    @Test
    void deveAprovarOrcamento() {
        OrdemServico os = new OrdemServico(1L, 2L);
        os.gerarOrcamento(); // Coloca o status em AGUARDANDO_APROVACAO
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(repository.salvar(any())).thenReturn(os);

        OrdemServico result = service.executar(1L);

        assertEquals(StatusOS.EM_EXECUCAO, result.getStatus());
        verify(repository).salvar(os);
    }

    @Test
    void deveLancarExcecaoSeOSNaoExistir() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.executar(1L));
    }
}
