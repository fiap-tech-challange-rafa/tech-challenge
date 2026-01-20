package br.com.fiap.techchallange.interfaces.rest.servico;

import br.com.fiap.techchallange.domain.servico.Servico;
import br.com.fiap.techchallange.domain.servico.ServicoRepository;
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
class ServicoControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServicoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Servico servico;

    @BeforeEach
    void setup() {
        repository.removerTodos();
        servico = repository.salvar(new Servico("S001", "Troca de óleo", new BigDecimal(150.0)));
    }

    @Test
    void deveCriarServico() throws Exception {
        ServicoRequest request = new ServicoRequest("S002", "Alinhamento", new BigDecimal(120.0));

        mockMvc.perform(post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.codigo").value("S002"))
                .andExpect(jsonPath("$.descricao").value("Alinhamento"));
    }

    @Test
    void deveBuscarServicoPorId() throws Exception {
        mockMvc.perform(get("/api/admin/servicos/{id}", servico.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(servico.getId()))
                .andExpect(jsonPath("$.descricao").value("Troca de óleo"));
    }

    @Test
    void deveListarTodosServicos() throws Exception {
        mockMvc.perform(get("/api/admin/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descricao").value("Troca de óleo"));
    }

    @Test
    void deveAtualizarServico() throws Exception {
        ServicoUpdateRequest update = new ServicoUpdateRequest("Troca de óleo premium", new BigDecimal(180.0));

        mockMvc.perform(put("/api/admin/servicos/{id}", servico.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Troca de óleo premium"))
                .andExpect(jsonPath("$.preco").value(180.0));
    }

    @Test
    void deveDeletarServico() throws Exception {
        mockMvc.perform(delete("/api/admin/servicos/{id}", servico.getId()))
                .andExpect(status().isNoContent());


    }
}
