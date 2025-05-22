package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessingServiceTest {

    private final FileProcessingService service = new FileProcessingService();

    @Test
    void processFile_deveProcessarCorretamenteUmArquivoValido() throws IOException {
        // Arrange: linha de 95 caracteres válidos
        String linhaValida = String.format("%-10s%-45s%-10s%-10s%-12s%-8s",
                "1", "João da Silva", "100", "200", "00001234.56", "20240520");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "dados.txt",
                "text/plain",
                linhaValida.getBytes()
        );

        // Act
        List<UserDTO> resultado = service.processFile(file);

        // Assert
        assertEquals(1, resultado.size());
        UserDTO usuario = resultado.get(0);
        assertEquals(1L, usuario.getId());
        assertEquals("João da Silva", usuario.getName());
        assertEquals(1, usuario.getOrders().size());
        assertEquals(1, usuario.getOrders().get(0).getProducts().size());
        assertEquals(new BigDecimal("1234.56"), usuario.getOrders().get(0).getProducts().get(0).getPrice());
    }

    @Test
    void processFile_deveIgnorarLinhasInvalidas() throws IOException {
        String linhaInvalida = "linha inválida curta";
        String linhaValida = String.format("%-10s%-45s%-10s%-10s%-12s%-8s",
                "2", "Maria", "101", "201", "00005678.90", "20240521");

        String conteudo = linhaInvalida + "\n" + linhaValida;

        MockMultipartFile file = new MockMultipartFile(
                "file", "dados.txt", "text/plain", conteudo.getBytes()
        );

        List<UserDTO> resultado = service.processFile(file);

        assertEquals(1, resultado.size());
        assertEquals("Maria", resultado.get(0).getName());
    }
}
