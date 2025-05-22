package com.luizalabs.desafio_tecnico.controller;

import com.luizalabs.desafio_tecnico.dto.ApiErrorDTO;
import com.luizalabs.desafio_tecnico.dto.FileProcessingResultDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.exceptions.InvalidFileFormatException;
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

import java.io.IOException;
import java.util.Collections;
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
    @Operation(summary = "Upload de arquivo com dados de pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo processado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido ou parâmetros incorretos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "413", description = "Arquivo muito grande",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "422", description = "Erro ao processar conteúdo do arquivo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<FileProcessingResultDTO> handleUpload(
            @Parameter(description = "Arquivo de texto com dados de pedidos", required = true)
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new InvalidFileFormatException("Arquivo não pode estar vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("text/plain")) {
            throw new InvalidFileFormatException("Tipo de arquivo não suportado. Apenas arquivos de texto são aceitos.");
        }

        FileProcessingResultDTO result = processingService.processFile(file);
        queryService.salvarPedidos(result.getUsers()); // salvar apenas os válidos
        return ResponseEntity.ok(result);
    }
}