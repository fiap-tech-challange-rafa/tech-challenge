package br.com.fiap.techchallange.domain.ordemservico;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoTest {

    @Test
    void deveCriarOrdemServicoComStatusInicial() {
        OrdemServico os = new OrdemServico(1L, 2L);

        assertEquals(1L, os.getClienteId());
        assertEquals(2L, os.getVeiculoId());
        assertEquals(StatusOS.RECEBIDA, os.getStatus());
        assertEquals(BigDecimal.ZERO, os.getTotalOrcamento());
        assertNotNull(os.getDataCriacao());
        assertNotNull(os.getDataAtualizacao());
    }

    @Test
    void deveIncluirServicoEAjustarStatusParaDiagnostico() {
        OrdemServico os = new OrdemServico(1L, 1L);
        ItemServico servico = new ItemServico(1L, "Troca de óleo", new BigDecimal("150.00"));

        os.incluirServico(servico);

        assertEquals(1, os.getServicos().size());
        assertEquals(StatusOS.EM_DIAGNOSTICO, os.getStatus());
        assertEquals(new BigDecimal("150.00"), os.getTotalOrcamento());
    }

    @Test
    void deveIncluirPecaEAjustarStatusParaDiagnostico() {
        OrdemServico os = new OrdemServico(1L, 1L);
        ItemPeca peca = new ItemPeca(1L, "Filtro de óleo", new BigDecimal("50.00"), 2);

        os.incluirPeca(peca);

        assertEquals(1, os.getPecas().size());
        assertEquals(StatusOS.EM_DIAGNOSTICO, os.getStatus());
        assertEquals(new BigDecimal("100.00"), os.getTotalOrcamento());
    }

    @Test
    void deveGerarOrcamentoCorretamente() {
        OrdemServico os = new OrdemServico(1L, 1L);
        os.incluirServico(new ItemServico(1L, "Troca de óleo", new BigDecimal("150")));
        os.incluirPeca(new ItemPeca(1L, "Filtro", new BigDecimal("50"), 1));

        os.gerarOrcamento();

        assertEquals(new BigDecimal("200"), os.getTotalOrcamento());
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, os.getStatus());
    }

    @Test
    void deveAprovarOrcamentoEAjustarStatus() {
        OrdemServico os = new OrdemServico(1L, 1L);
        os.gerarOrcamento(); // Colocar em AGUARDANDO_APROVACAO
        os.aprovarOrcamento();

        assertEquals(StatusOS.EM_EXECUCAO, os.getStatus());
    }

    @Test
    void deveRejeitarOrcamentoEAjustarStatus() {
        OrdemServico os = new OrdemServico(1L, 1L);
        os.gerarOrcamento(); // Colocar em AGUARDANDO_APROVACAO
        os.rejeitarOrcamento();

        assertEquals(StatusOS.CANCELADA, os.getStatus());
    }

    @Test
    void deveFinalizarEAjustarStatus() {
        OrdemServico os = new OrdemServico(1L, 1L);
        os.gerarOrcamento(); // Colocar em AGUARDANDO_APROVACAO
        os.aprovarOrcamento(); // Colocar em EM_EXECUCAO
        os.finalizar();

        assertEquals(StatusOS.FINALIZADA, os.getStatus());
    }

    @Test
    void deveEntregarEAjustarStatus() {
        OrdemServico os = new OrdemServico(1L, 1L);
        os.gerarOrcamento(); // Colocar em AGUARDANDO_APROVACAO
        os.aprovarOrcamento(); // Colocar em EM_EXECUCAO
        os.finalizar(); // Colocar em FINALIZADA
        os.entregar();

        assertEquals(StatusOS.ENTREGUE, os.getStatus());
    }

    @Test
    void deveRecalcularOrcamentoComServicosEPecas() {
        OrdemServico os = new OrdemServico(1L, 1L);
        os.incluirServico(new ItemServico(1L, "Troca de pneu", new BigDecimal("200")));
        os.incluirPeca(new ItemPeca(2L, "Pneu", new BigDecimal("300"), 4));

        os.gerarOrcamento();

        assertEquals(new BigDecimal("1400"), os.getTotalOrcamento());
    }

    @Test
    void deveAtualizarDataAoRecalcularOrcamento() throws InterruptedException {
        OrdemServico os = new OrdemServico(1L, 1L);
        LocalDateTime antes = os.getDataAtualizacao();

        Thread.sleep(10);
        os.incluirServico(new ItemServico(1L, "Alinhamento", new BigDecimal("100")));

        assertTrue(os.getDataAtualizacao().isAfter(antes));
    }
}
