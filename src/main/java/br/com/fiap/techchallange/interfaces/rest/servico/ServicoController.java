package br.com.fiap.techchallange.interfaces.rest.servico;

import br.com.fiap.techchallange.application.servico.AtualizarServicoService;
import br.com.fiap.techchallange.application.servico.BuscarServicoService;
import br.com.fiap.techchallange.application.servico.CadastrarServicoService;
import br.com.fiap.techchallange.application.servico.RemoverServicoService;
import br.com.fiap.techchallange.domain.servico.Servico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Operações relacionadas a serviços")
public class ServicoController {

    private final CadastrarServicoService cadastrar;
    private final AtualizarServicoService atualizar;
    private final BuscarServicoService buscar;
    private final RemoverServicoService remover;

    public ServicoController(CadastrarServicoService cadastrar,
                             AtualizarServicoService atualizar,
                             BuscarServicoService buscar,
                             RemoverServicoService remover) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.remover = remover;
    }

    @Operation(summary = "Criar serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ServicoResponse> criar(@RequestBody ServicoRequest req) {
        Servico created = cadastrar.executar(req.codigo(), req.descricao(), req.preco());
        ServicoResponse resp = ServicoResponse.fromDomain(created);
        return ResponseEntity.created(URI.create("/api/servicos/" + resp.id())).body(resp);
    }

    @Operation(summary = "Busca um serviço pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> porId(@PathVariable Long id) {
        Servico s = buscar.porId(id);
        return ResponseEntity.ok(ServicoResponse.fromDomain(s));
    }

    @Operation(summary = "Lista todos os serviços")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listar() {
        List<Servico> all = buscar.listarTodos();
        List<ServicoResponse> resp = all.stream().map(ServicoResponse::fromDomain).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Atualiza um serviço pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> atualizar(@PathVariable Long id, @RequestBody ServicoUpdateRequest req) {
        Servico updated = atualizar.executar(id, req.descricao(), req.preco());
        return ResponseEntity.ok(ServicoResponse.fromDomain(updated));
    }

    @Operation(summary = "Deleta um serviço pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        remover.executar(id);
        return ResponseEntity.noContent().build();
    }
}
