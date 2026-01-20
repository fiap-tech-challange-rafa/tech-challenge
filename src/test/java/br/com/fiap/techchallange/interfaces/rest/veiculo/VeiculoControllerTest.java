package br.com.fiap.techchallange.interfaces.rest.veiculo;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VeiculoControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VeiculoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Veiculo veiculo;

    @BeforeEach
    void setup() {
        repository.removerTodos();
        veiculo = repository.salvar(new Veiculo(1L, new Placa("ABC-1234"), "Fiat", "Uno", 2020));
    }

    @Test
    void deveCriarVeiculo() throws Exception {
        VeiculoRequest request = new VeiculoRequest(1L, "XYZ-9999", "Ford", "Ka", 2022);

        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.placa").value("XYZ9999"))
                .andExpect(jsonPath("$.marca").value("Ford"));
    }

    @Test
    void deveBuscarVeiculoPorId() throws Exception {
        mockMvc.perform(get("/api/admin/veiculos/{id}", veiculo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(veiculo.getId()))
                .andExpect(jsonPath("$.placa").value("ABC1234"));
    }

    @Test
    void deveListarTodosVeiculos() throws Exception {
        mockMvc.perform(get("/api/admin/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].placa").value("ABC1234"));
    }

    @Test
    void deveAtualizarVeiculo() throws Exception {
        VeiculoUpdateRequest update = new VeiculoUpdateRequest("ABC-1234", "Fiat", "Uno Mille", 2021);

        mockMvc.perform(put("/api/admin/veiculos/{id}", veiculo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Uno Mille"))
                .andExpect(jsonPath("$.ano").value(2021));
    }

    @Test
    void deveDeletarVeiculo() throws Exception {
        mockMvc.perform(delete("/api/admin/veiculos/{id}", veiculo.getId()))
                .andExpect(status().isNoContent());


    }
}
