package br.com.fiap.techchallange.interfaces.rest.veiculo;

import br.com.fiap.techchallange.application.veiculo.AtualizarVeiculoService;
import br.com.fiap.techchallange.application.veiculo.BuscarVeiculoService;
import br.com.fiap.techchallange.application.veiculo.CadastrarVeiculoService;
import br.com.fiap.techchallange.application.veiculo.RemoverVeiculoService;
import br.com.fiap.techchallange.domain.veiculo.Veiculo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/veiculos")
@Tag(name = "Veículos", description = "Operações relacionadas a veículos")
public class VeiculoController {

    private final CadastrarVeiculoService cadastrar;
    private final AtualizarVeiculoService atualizar;
    private final BuscarVeiculoService buscar;
    private final RemoverVeiculoService remover;

    public VeiculoController(CadastrarVeiculoService cadastrar,
                             AtualizarVeiculoService atualizar,
                             BuscarVeiculoService buscar,
                             RemoverVeiculoService remover) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.remover = remover;
    }

    @Operation(summary = "Criar veículo", description = "Cria um novo veículo associado a um cliente")
    @PostMapping
    public ResponseEntity<VeiculoResponse> criar(@RequestBody VeiculoRequest req) {
        Veiculo created = cadastrar.executar(req.clienteId(), req.placa(), req.marca(), req.modelo(), req.ano());
        VeiculoResponse resp = VeiculoResponse.fromDomain(created);
        return ResponseEntity.created(URI.create("/api/veiculos/" + resp.id())).body(resp);
    }

    @Operation(summary = "Buscar veículo por ID", description = "Retorna um veículo pelo seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponse> porId(
            @Parameter(description = "ID do veículo", required = true) @PathVariable Long id) {
        Veiculo v = buscar.porId(id);
        return ResponseEntity.ok(VeiculoResponse.fromDomain(v));
    }

    @Operation(summary = "Listar veículos", description = "Lista todos os veículos ou filtra por cliente")
    @GetMapping
    public ResponseEntity<List<VeiculoResponse>> listar(
            @Parameter(description = "ID do cliente para filtragem") @RequestParam(required = false) Long clienteId) {
        List<Veiculo> list = (clienteId == null) ? buscar.listarTodos() : buscar.porCliente(clienteId);
        List<VeiculoResponse> resp = list.stream().map(VeiculoResponse::fromDomain).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Atualizar veículo", description = "Atualiza os dados de um veículo existente")
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> atualizar(
            @Parameter(description = "ID do veículo", required = true) @PathVariable Long id,
            @RequestBody VeiculoUpdateRequest req) {
        Veiculo updated = atualizar.executar(id, req.placa(), req.marca(), req.modelo(), req.ano());
        return ResponseEntity.ok(VeiculoResponse.fromDomain(updated));
    }

    @Operation(summary = "Deletar veículo", description = "Remove um veículo pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do veículo", required = true) @PathVariable Long id) {
        remover.executar(id);
        return ResponseEntity.noContent().build();
    }
}
