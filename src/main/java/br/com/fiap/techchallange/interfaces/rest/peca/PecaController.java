package br.com.fiap.techchallange.interfaces.rest.peca;


import br.com.fiap.techchallange.application.peca.*;
import br.com.fiap.techchallange.domain.peca.Peca;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/pecas")
@Tag(name = "Peças", description = "Operações relacionadas a peças e estoque")
public class PecaController {

    private final CadastrarPecaService cadastrar;
    private final AtualizarPecaService atualizar;
    private final BuscarPecaService buscar;
    private final RemoverPecaService remover;
    private final AjustarEstoqueService ajustar;

    public PecaController(CadastrarPecaService cadastrar,
                          AtualizarPecaService atualizar,
                          BuscarPecaService buscar,
                          RemoverPecaService remover,
                          AjustarEstoqueService ajustar) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.remover = remover;
        this.ajustar = ajustar;
    }

    @Operation(summary = "Criar peça")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Peça criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<PecaResponse> criar(@RequestBody PecaRequest req) {
        Peca created = cadastrar.executar(req.sku(), req.nome(), req.preco(), req.quantidadeEstoque());
        PecaResponse resp = PecaResponse.fromDomain(created);
        return ResponseEntity.created(URI.create("/api/pecas/" + resp.id())).body(resp);
    }

    @Operation(summary = "Busca uma peça pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Peça encontrada"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PecaResponse> porId(@PathVariable Long id) {
        Peca p = buscar.porId(id);
        return ResponseEntity.ok(PecaResponse.fromDomain(p));
    }

    @Operation(summary = "Lista todas as peças")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<PecaResponse>> listar() {
        List<Peca> all = buscar.listarTodos();
        List<PecaResponse> resp = all.stream().map(PecaResponse::fromDomain).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Atualiza uma peça pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Peça atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PecaResponse> atualizar(@PathVariable Long id, @RequestBody PecaUpdateRequest req) {
        Peca updated = atualizar.executar(id, req.nome(), req.preco());
        return ResponseEntity.ok(PecaResponse.fromDomain(updated));
    }

    @Operation(summary = "Deleta uma peça pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Peça deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        remover.executar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ajusta estoque de uma peça")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque ajustado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    })
    @PostMapping("/{id}/ajustar-estoque")
    public ResponseEntity<PecaResponse> ajustarEstoque(@PathVariable Long id, @RequestBody AjusteEstoqueRequest req) {
        Peca p = ajustar.ajustar(id, req.quantidade());
        return ResponseEntity.ok(PecaResponse.fromDomain(p));
    }
}
