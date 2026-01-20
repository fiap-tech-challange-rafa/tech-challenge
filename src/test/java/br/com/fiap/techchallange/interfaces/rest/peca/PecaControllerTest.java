package br.com.fiap.techchallange.interfaces.rest.peca;

import br.com.fiap.techchallange.domain.peca.Peca;
import br.com.fiap.techchallange.domain.peca.PecaRepository;
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
class PecaControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PecaRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Peca peca;

    @BeforeEach
    void setup() {
        repository.removerTodos();
        peca = repository.salvar(new Peca("SKU123", "Filtro de óleo", new BigDecimal(50.0), 10));
    }

    @Test
    void deveCriarPeca() throws Exception {
        PecaRequest request = new PecaRequest("SKU456", "Pastilha de freio", new BigDecimal(80.0), 20);

        mockMvc.perform(post("/api/admin/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/admin/pecas/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sku").value("SKU456"))
                .andExpect(jsonPath("$.nome").value("Pastilha de freio"));
    }

    @Test
    void deveBuscarPecaPorId() throws Exception {
        mockMvc.perform(get("/api/admin/pecas/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(peca.getId()))
                .andExpect(jsonPath("$.nome").value("Filtro de óleo"));
    }

    @Test
    void deveListarTodasPecas() throws Exception {
        mockMvc.perform(get("/api/admin/pecas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Filtro de óleo"));
    }

    @Test
    void deveAtualizarPeca() throws Exception {
        PecaUpdateRequest update = new PecaUpdateRequest("Filtro de óleo premium", new BigDecimal(750.0));

        mockMvc.perform(put("/api/admin/pecas/{id}", peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Filtro de óleo premium"))
                .andExpect(jsonPath("$.preco").value(750.0));
    }

    @Test
    void deveDeletarPeca() throws Exception {
        mockMvc.perform(delete("/api/admin/pecas/{id}", peca.getId()))
                .andExpect(status().isNoContent());

    }

    @Test
    void deveAjustarEstoque() throws Exception {
        AjusteEstoqueRequest request = new AjusteEstoqueRequest(5);

        mockMvc.perform(post("/api/admin/pecas/{id}/ajustar-estoque", peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque").value(15));
    }
}
