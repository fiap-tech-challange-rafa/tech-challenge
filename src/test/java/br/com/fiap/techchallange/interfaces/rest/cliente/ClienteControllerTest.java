package br.com.fiap.techchallange.interfaces.rest.cliente;

import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.cliente.ClienteRepository;
import br.com.fiap.techchallange.domain.cliente.Documento;
import br.com.fiap.techchallange.domain.cliente.Email;
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
class ClienteControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente cliente;

    @BeforeEach
    void setup() {
        repository.removerTodos();
        cliente = repository.salvar(new Cliente("João", new Documento("86579599090"), "11999999999", new Email("joao@email.com")));
    }

    @Test
    void deveCriarCliente() throws Exception {
        ClienteRequest request = new ClienteRequest("Maria", "41693974010", "11988888888", "maria@email.com");

        mockMvc.perform(post("/api/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/admin/clientes/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.documento").value("41693974010"));
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        mockMvc.perform(get("/api/admin/clientes/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()))
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    void deveListarTodosClientes() throws Exception {
        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("João"));
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        ClienteUpdateRequest update = new ClienteUpdateRequest("João da Silva", "11911111111", "joao@novoemail.com");

        mockMvc.perform(put("/api/admin/clientes/{id}", cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.telefone").value("11911111111"))
                .andExpect(jsonPath("$.email").value("joao@novoemail.com"));
    }

}
