package com.luizalabs.desafio_tecnico.controller;

import com.luizalabs.desafio_tecnico.dto.ApiErrorDTO;
import com.luizalabs.desafio_tecnico.dto.FileProcessingResultDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.exceptions.InvalidFileFormatException;
import com.luizalabs.desafio_tecnico.service.FileProcessingService;
import com.luizalabs.desafio_tecnico.service.OrderQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileProcessingService processingService;

    @MockitoBean
    private OrderQueryService queryService;

    private MockMultipartFile validFile;
    private MockMultipartFile emptyFile;
    private MockMultipartFile invalidTypeFile;

    @BeforeEach
    void setUp() {
        // Arquivo válido
        validFile = new MockMultipartFile(
                "file",
                "pedidos.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "user1,order1,100\nuser2,order2,200".getBytes()
        );

        // Arquivo vazio
        emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        // Arquivo com tipo inválido
        invalidTypeFile = new MockMultipartFile(
                "file",
                "pedidos.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "fake pdf content".getBytes()
        );
    }

    @Test
    void deveProcessarArquivoComSucesso() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Maria");

        FileProcessingResultDTO resultDTO = new FileProcessingResultDTO();
        resultDTO.setUsers(Collections.singletonList(userDTO));

        when(processingService.processFile(any(MockMultipartFile.class))).thenReturn(resultDTO);
        doNothing().when(queryService).salvarPedidos(anyList());

        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(validFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users[0].name").value("Maria"));

        verify(processingService, times(1)).processFile(any(MockMultipartFile.class));
        verify(queryService, times(1)).salvarPedidos(resultDTO.getUsers());
    }

    @Test
    void deveRetornarBadRequestParaArquivoVazio() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Formato de arquivo inválido: Arquivo não pode estar vazio"));

        verify(processingService, never()).processFile(any());
        verify(queryService, never()).salvarPedidos(any());
    }

    @Test
    void deveRetornarBadRequestParaTipoDeArquivoInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(invalidTypeFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Formato de arquivo inválido: Tipo de arquivo não suportado. Apenas arquivos de texto são aceitos."));

        verify(processingService, never()).processFile(any());
        verify(queryService, never()).salvarPedidos(any());
    }

    @Test
    void deveRetornarBadRequestParaErroDeProcessamento() throws Exception {
        // Arrange
        when(processingService.processFile(any(MockMultipartFile.class)))
                .thenThrow(new InvalidFileFormatException("Erro ao processar conteúdo do arquivo"));

        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(validFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Formato de arquivo inválido: Erro ao processar conteúdo do arquivo"));

        verify(processingService, times(1)).processFile(any(MockMultipartFile.class));
        verify(queryService, never()).salvarPedidos(any());
    }

    @Test
    void deveRetornarInternalServerErrorParaExcecaoGenerica() throws Exception {
        // Arrange
        when(processingService.processFile(any(MockMultipartFile.class)))
                .thenThrow(new RuntimeException("Erro interno"));

        // Act & Assert
        mockMvc.perform(multipart("/api/upload").file(validFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Erro interno do servidor: Erro interno"));

        verify(processingService, times(1)).processFile(any(MockMultipartFile.class));
        verify(queryService, never()).salvarPedidos(any());
    }
}