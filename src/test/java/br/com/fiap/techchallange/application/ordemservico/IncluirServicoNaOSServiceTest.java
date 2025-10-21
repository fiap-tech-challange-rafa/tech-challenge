package br.com.fiap.techchallange.application.ordemservico;

import br.com.fiap.techchallange.domain.ordemservico.*;
import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IncluirServicoNaOSServiceTest {

    private OrdemServicoRepository repository;
    private IncluirServicoNaOSService service;
    private ServicoRepository servicoRepository;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepository.class);
        servicoRepository = mock(ServicoRepository.class);
        service = new IncluirServicoNaOSService(repository,servicoRepository);
    }

    @Test
    void deveIncluirServicoNaOS() {
        OrdemServico os = new OrdemServico(1L, 2L);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(repository.salvar(any())).thenReturn(os);
        when(servicoRepository.buscarPorId(99L)).thenReturn(Optional.of(new Servico("23","balanceamento", new BigDecimal(12))));

        OrdemServico result = service.executar(1L, 99L, "Troca de óleo");

        assertEquals(1, result.getServicos().size());
        verify(repository).salvar(os);
    }
}
