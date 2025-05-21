package com.luizalabs.desafio_tecnico.controller;

import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.service.FileProcessingService;
import com.luizalabs.desafio_tecnico.service.OrderQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "File Processing", description = "Endpoints para processamento de arquivos de pedidos")
public class FileUploadController {

    private final FileProcessingService processingService;
    private final OrderQueryService queryService;

    public FileUploadController(FileProcessingService processingService,
                                OrderQueryService queryService) {
        this.processingService = processingService;
        this.queryService = queryService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload e processamento de arquivo de pedidos",
            description = "Faz upload de um arquivo de texto com dados de pedidos e retorna os dados processados agrupados por usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Arquivo processado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<UserDTO>> handleUpload(
            @Parameter(description = "Arquivo de texto com dados de pedidos", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            List<UserDTO> result = processingService.processFile(file);
            queryService.salvarPedidos(result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}