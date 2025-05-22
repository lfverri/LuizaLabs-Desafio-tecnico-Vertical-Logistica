package com.luizalabs.desafio_tecnico.controller;

import com.luizalabs.desafio_tecnico.dto.FileProcessingResultDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.service.FileProcessingService;
import com.luizalabs.desafio_tecnico.service.OrderQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FileUploadControllerTest {

    private MockMvc mockMvc;
    private FileProcessingService processingService;
    private OrderQueryService queryService;
    private FileUploadController controller;

    @BeforeEach
    void setup() {
        // Inicializa mocks
        processingService = mock(FileProcessingService.class);
        queryService = mock(OrderQueryService.class);
        controller = new FileUploadController(processingService, queryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveProcessarArquivoComSucesso() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "pedidos.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "dados de teste".getBytes()
        );

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Maria");
        List<UserDTO> result = Collections.singletonList(userDTO);

        when(processingService.processFile(any(MockMultipartFile.class))).thenReturn((FileProcessingResultDTO) result);
        doNothing().when(queryService).salvarPedidos(anyList());

        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Maria"));

        verify(processingService, times(1)).processFile(any(MockMultipartFile.class));
        verify(queryService, times(1)).salvarPedidos(result);
    }

    @Test
    void deveRetornarBadRequestParaArquivoInvalido() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "pedidos.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        when(processingService.processFile(any(MockMultipartFile.class)))
                .thenThrow(new IllegalArgumentException("Arquivo inválido"));

        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("[]"));

        verify(processingService, times(1)).processFile(any(MockMultipartFile.class));
        verify(queryService, never()).salvarPedidos(anyList());
    }

    @Test
    void deveRetornarInternalServerErrorParaExcecaoGenerica() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "pedidos.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "dados de teste".getBytes()
        );

        when(processingService.processFile(any(MockMultipartFile.class)))
                .thenThrow(new RuntimeException("Erro interno"));

        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isInternalServerError());

        verify(processingService, times(1)).processFile(any(MockMultipartFile.class));
        verify(queryService, never()).salvarPedidos(anyList());
    }

    @Test
    void deveRetornarBadRequestParaArquivoNulo() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/api/upload"))
                .andExpect(status().isBadRequest());

        verify(processingService, never()).processFile(any(MockMultipartFile.class));
        verify(queryService, never()).salvarPedidos(anyList());
    }
}