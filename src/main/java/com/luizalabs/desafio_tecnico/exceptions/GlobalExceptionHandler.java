package com.luizalabs.desafio_tecnico.exceptions;

import com.luizalabs.desafio_tecnico.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorDTO buildApiError (int status, String message, String path) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setStatus(status);
        apiErrorDTO.setError(message);
        apiErrorDTO.setPath(path);
        apiErrorDTO.setTimestamp(Instant.now());

        return apiErrorDTO;
    }

    @ExceptionHandler (Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorDTO apiErrorDTO = this.buildApiError(status.value(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(apiErrorDTO);
    }
}
