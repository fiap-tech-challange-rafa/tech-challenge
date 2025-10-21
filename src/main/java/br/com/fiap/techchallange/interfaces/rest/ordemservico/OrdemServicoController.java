package br.com.fiap.techchallange.interfaces.rest.ordemservico;

import br.com.fiap.techchallange.application.cliente.BuscarClienteService;
import br.com.fiap.techchallange.application.email.EnvioOrcamentoEmailService;
import br.com.fiap.techchallange.application.ordemservico.*;
import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
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
@RequestMapping("/api/ordem-servico")
@Tag(name = "Ordens de Serviço", description = "Gerenciamento de ordens de serviço")
public class OrdemServicoController {

    private final CriarOrdemServicoService criar;
    private final IncluirServicoNaOSService incluirServico;
    private final IncluirPecaNaOSService incluirPeca;
    private final GerarOrcamentoService gerarOrcamento;
    private final AprovarOrcamentoService aprovarOrcamento;
    private final FinalizarOrdemServicoService finalizar;
    private final EntregarOrdemServicoService entregar;
    private final BuscarOrdemService buscar;
    private final BuscarClienteService buscarClienteService;
    private final EnvioOrcamentoEmailService envioOrcamentoEmailService;

    public OrdemServicoController(CriarOrdemServicoService criar,
                                  IncluirServicoNaOSService incluirServico,
                                  IncluirPecaNaOSService incluirPeca,
                                  GerarOrcamentoService gerarOrcamento,
                                  AprovarOrcamentoService aprovarOrcamento,
                                  FinalizarOrdemServicoService finalizar,
                                  EntregarOrdemServicoService entregar,
                                  BuscarOrdemService buscar, BuscarClienteService buscarClienteService, EnvioOrcamentoEmailService envioOrcamentoEmailService) {
        this.criar = criar;
        this.incluirServico = incluirServico;
        this.incluirPeca = incluirPeca;
        this.gerarOrcamento = gerarOrcamento;
        this.aprovarOrcamento = aprovarOrcamento;
        this.finalizar = finalizar;
        this.entregar = entregar;
        this.buscar = buscar;
        this.buscarClienteService = buscarClienteService;
        this.envioOrcamentoEmailService = envioOrcamentoEmailService;
    }

    @Operation(summary = "Cria uma nova ordem de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OS criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarOrdemRequest req) {
        OrdemServico os = criar.executar(req.clienteId(), req.veiculoId());
        return ResponseEntity.ok().body(OrdemServicoResponse.fromDomain(os));
    }

    @Operation(summary = "Inclui um serviço na OS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço adicionado com sucesso"),
            @ApiResponse(responseCode = "404", description = "OS ou serviço não encontrado")
    })
    @PostMapping("/{id}/servicos")
    public ResponseEntity<?> incluirServico(@PathVariable Long id, @RequestBody IncluirServicoRequest req) {
        OrdemServico os = incluirServico.executar(id, req.servicoId(), req.descricao());
        return ResponseEntity.ok(OrdemServicoResponse.fromDomain(os));
    }

    @Operation(summary = "Inclui uma peça na OS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Peça adicionada com sucesso"),
            @ApiResponse(responseCode = "404", description = "OS ou peça não encontrada")
    })
    @PostMapping("/{id}/pecas")
    public ResponseEntity<?> incluirPeca(@PathVariable Long id, @RequestBody IncluirPecaRequest req) {
        OrdemServico os = incluirPeca.executar(id, req.pecaId(), req.quantidade());
        return ResponseEntity.ok(OrdemServicoResponse.fromDomain(os));
    }

    @Operation(summary = "Gera orçamento da OS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    @PostMapping("/{id}/orcamento")
    public ResponseEntity<?> gerarOrcamento(@PathVariable Long id) {
        OrdemServico os = gerarOrcamento.executar(id);
        return ResponseEntity.ok(new OrcamentoResponse(os.getTotalOrcamento()));
    }

    @Operation(summary = "Aprova orçamento da OS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento aprovado"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    @GetMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        OrdemServico os = aprovarOrcamento.executar(id);
        return ResponseEntity.ok("Orçamento Aprovado!");
    }

    @Operation(summary = "Finaliza uma OS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS finalizada"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizar(@PathVariable Long id) {
        OrdemServico os = finalizar.executar(id);
        return ResponseEntity.ok(OrdemServicoResponse.fromDomain(os));
    }

    @Operation(summary = "Marca OS como entregue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS entregue"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    @PostMapping("/{id}/entregar")
    public ResponseEntity<?> entregar(@PathVariable Long id) {
        OrdemServico os = entregar.executar(id);
        return ResponseEntity.ok(OrdemServicoResponse.fromDomain(os));
    }

    @Operation(summary = "Busca uma OS pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS encontrada"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponse> porId(@PathVariable Long id) {
        OrdemServico os = buscar.porId(id);
        return ResponseEntity.ok(OrdemServicoResponse.fromDomain(os));
    }

    @Operation(summary = "Lista todas as OS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de OS retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<OrdemServicoResponse>> listar() {
        List<OrdemServico> all = buscar.listarTodos();
        List<OrdemServicoResponse> resp = all.stream().map(OrdemServicoResponse::fromDomain).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Deleta uma OS pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OS deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        buscar.remover(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar Orçamento Cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OS deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    @PostMapping("/{id}/enviar-orcamento")
    public ResponseEntity<Void> enviarOrcamentoEmail(@PathVariable Long id) throws Exception {
        try {
            OrdemServico os = buscar.porId(id);
            Cliente cliente = buscarClienteService.porId(os.getClienteId());

            envioOrcamentoEmailService.enviarOrcamento(cliente, os);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
