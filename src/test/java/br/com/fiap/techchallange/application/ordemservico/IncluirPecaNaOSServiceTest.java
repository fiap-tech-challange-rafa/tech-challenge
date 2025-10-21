package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.*;
import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IncluirPecaNaOSServiceTest {

    private OrdemServicoRepository repository;
    private PecaRepository pecaRepository;

    private IncluirPecaNaOSService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepository.class);
        pecaRepository = mock(PecaRepository.class);
        service = new IncluirPecaNaOSService(repository,pecaRepository);
    }

    @Test
    void deveIncluirPecaNaOS() {
        OrdemServico os = new OrdemServico(1L, 2L);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(repository.salvar(any())).thenReturn(os);
        when(pecaRepository.buscarPorId(99L)).thenReturn(Optional.of(new Peca("123", "pneu", new BigDecimal(130.0), 3)));

        OrdemServico result = service.executar(1L, 99L,  2);

        assertEquals(1, result.getPecas().size());
        verify(repository).salvar(os);
    }
}
