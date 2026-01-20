package br.com.fiap.techchallange.interfaces.rest.ordemservico;

import br.com.fiap.techchallange.application.ordemservico.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.cliente.ClienteRepository;
import br.com.fiap.techchallange.domain.cliente.Documento;
import br.com.fiap.techchallange.domain.cliente.Email;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
import br.com.fiap.techchallange.domain.veiculo.Placa;
import br.com.fiap.techchallange.domain.veiculo.Veiculo;
import br.com.fiap.techchallange.domain.veiculo.VeiculoRepository;
import br.com.fiap.techchallange.interfaces.rest.BaseControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrdemServicoControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdemServicoRepositoryPort repository;

    @Autowired
    private ObjectMapper objectMapper;

    private OrdemServico os;
    private Peca peca;
    private Cliente cliente;
    private Veiculo veiculo;
    private Servico servico;

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PecaRepository pecaRepository;
    @Autowired
    private ServicoRepository servicoRepository;

    @BeforeEach
    void setup() {
        repository.removerTodos();
        pecaRepository.removerTodos();;
        clienteRepository.removerTodos();
        veiculoRepository.removerTodos();;


        cliente = new Cliente("João", new Documento("87198650074"), "11999999999", new Email("joao@email.com"));
        cliente = clienteRepository.salvar(cliente);

        veiculo = new Veiculo(cliente.getId(), new Placa("ABC1238"), "Fiat", "Uno", 2020);
        veiculo = veiculoRepository.salvar(veiculo);

        peca = pecaRepository.salvar(new Peca("SKU123", "Filtro de óleo", new BigDecimal("50"), 10));
        servico = servicoRepository.salvar(new Servico("123", "as", new BigDecimal("10")));

        os = new OrdemServico(cliente.getId(), veiculo.getId());
        os = repository.salvar(os);
    }

    @Test
    void deveCriarOrdemServico() throws Exception {
        CriarOrdemRequest request = new CriarOrdemRequest(cliente.getId(), veiculo.getId());

        mockMvc.perform(post("/api/ordem-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clienteId").value(cliente.getId().intValue()))
                .andExpect(jsonPath("$.veiculoId").value(veiculo.getId().intValue()));
    }

    @Test
    void deveIncluirServicoNaOS() throws Exception {
        IncluirServicoRequest request = new IncluirServicoRequest(servico.getId(), "Troca de óleo");

        mockMvc.perform(post("/api/ordem-servico/{id}/servicos", os.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicos", hasSize(1)))
                .andExpect(jsonPath("$.servicos[0].descricao").value("Troca de óleo"));
    }

    @Test
    void deveIncluirPecaNaOS() throws Exception {
        IncluirPecaRequest request = new IncluirPecaRequest(peca.getId(), 10);

        mockMvc.perform(post("/api/ordem-servico/{id}/pecas", os.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pecas", hasSize(1)))
                .andExpect(jsonPath("$.pecas[0].nome").value("Filtro de óleo"));
    }

    @Test
    void deveGerarOrcamento() throws Exception {
        mockMvc.perform(post("/api/ordem-servico/{id}/orcamento", os.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrcamento").exists());
    }


    @Test
    void deveFinalizarOrdemServico() throws Exception {
        mockMvc.perform(post("/api/ordem-servico/{id}/finalizar", os.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADA"));
    }

    @Test
    void deveEntregarOrdemServico() throws Exception {
        mockMvc.perform(post("/api/ordem-servico/{id}/entregar", os.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    @Test
    void deveBuscarOrdemPorId() throws Exception {
        mockMvc.perform(get("/api/ordem-servico/{id}", os.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(os.getId().intValue()))
                .andExpect(jsonPath("$.clienteId").value(cliente.getId().intValue()));
    }

    @Test
    void deveListarTodasOrdens() throws Exception {
        mockMvc.perform(get("/api/ordem-servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveDeletarOrdemServico() throws Exception {
        mockMvc.perform(delete("/api/ordem-servico/{id}", os.getId()))
                .andExpect(status().isNoContent());

    }
    @Test
    void deveEnviarOrcamentoPorEmail() throws Exception {
        mockMvc.perform(post("/api/ordem-servico/{id}/enviar-orcamento", os.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


}
