package br.com.fiap.techchallange.interfaces.rest.cliente;

import br.com.fiap.techchallange.application.cliente.*;
import br.com.fiap.techchallange.domain.cliente.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/clientes")
public class ClienteController {

    private final CadastrarClienteService cadastrar;
    private final AtualizarClienteService atualizar;
    private final BuscarClienteService buscar;
    private final RemoverClienteService remover;

    public ClienteController(CadastrarClienteService cadastrar,
                             AtualizarClienteService atualizar,
                             BuscarClienteService buscar,
                             RemoverClienteService remover) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.remover = remover;
    }

    @Operation(summary = "Cria um novo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@RequestBody ClienteRequest req) {
        Cliente c = cadastrar.executar(req.nome(), req.documento(), req.telefone(), req.email());
        ClienteResponse resp = ClienteResponse.fromDomain(c);
        return ResponseEntity.created(URI.create("/api/clientes/" + resp.id())).body(resp);
    }

    @Operation(summary = "Busca um cliente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> porId(@PathVariable Long id) {
        Cliente c = buscar.porId(id);
        return ResponseEntity.ok(ClienteResponse.fromDomain(c));
    }

    @Operation(summary = "Lista todos os clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        List<Cliente> all = buscar.listarTodos();
        List<ClienteResponse> resp = all.stream().map(ClienteResponse::fromDomain).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Atualiza os dados de um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id, @RequestBody ClienteUpdateRequest req) {
        Cliente updated = atualizar.executar(id, req.nome(), req.telefone(), req.email());
        return ResponseEntity.ok(ClienteResponse.fromDomain(updated));
    }

    @Operation(summary = "Deleta um cliente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        remover.executar(id);
        return ResponseEntity.noContent().build();
    }
}
