package com.luizalabs.desafio_tecnico.exceptions;

import com.luizalabs.desafio_tecnico.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorDTO buildApiError(int status, String message, String path) {
        return ApiErrorDTO.builder()
                .status(status)
                .error(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ApiErrorDTO> handleFileProcessingException(
            FileProcessingException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiErrorDTO apiErrorDTO = buildApiError(
                status.value(),
                "Erro ao processar arquivo: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiErrorDTO);
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidFileFormatException(
            InvalidFileFormatException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorDTO apiErrorDTO = buildApiError(
                status.value(),
                "Formato de arquivo inválido: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiErrorDTO);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorDTO apiErrorDTO = buildApiError(
                status.value(),
                "Parâmetro inválido: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiErrorDTO);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorDTO> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
        ApiErrorDTO apiErrorDTO = buildApiError(
                status.value(),
                "Arquivo muito grande. Tamanho máximo permitido excedido.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiErrorDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorDTO apiErrorDTO = buildApiError(
                status.value(),
                "Erro interno do servidor: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiErrorDTO);
    }
}