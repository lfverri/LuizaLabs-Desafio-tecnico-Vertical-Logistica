package com.luizalabs.desafio_tecnico.controller;

import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.service.OrderQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name = "Order Query", description = "Endpoints para consulta e gerenciamento de pedidos")
public class OrderQueryController {

    private final OrderQueryService queryService;

    public OrderQueryController(OrderQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/orders")
    @Operation(
            summary = "Consultar pedidos processados",
            description = "Consulta pedidos com filtros opcionais por ID do pedido, data de início e data fim"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<List<UserDTO>> consultarPedidos(
            @Parameter(description = "ID do pedido para filtrar")
            @RequestParam(required = false) Long orderId,

            @Parameter(description = "Data de início do período (yyyy-MM-dd)")
            @RequestParam(required = false) LocalDate dataInicio,

            @Parameter(description = "Data fim do período (yyyy-MM-dd)")
            @RequestParam(required = false) LocalDate dataFim) {

        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio)) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        try {
            List<UserDTO> result = queryService.consultarPedidos(
                    Optional.ofNullable(orderId),
                    Optional.ofNullable(dataInicio),
                    Optional.ofNullable(dataFim)
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/users")
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna todos os usuários processados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar usuários")
    })
    public ResponseEntity<List<UserDTO>> listarUsuarios() {
        List<UserDTO> users = queryService.buscarTodosUsuarios();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/clear")
    @Operation(
            summary = "Limpar dados",
            description = "Remove todos os dados processados da memória"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dados removidos com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao limpar dados")
    })
    public ResponseEntity<Void> limparDados() {
        queryService.limparDados();
        return ResponseEntity.noContent().build();
    }

}