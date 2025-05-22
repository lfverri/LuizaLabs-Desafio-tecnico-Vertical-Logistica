package com.luizalabs.desafio_tecnico.controller;

import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.service.OrderQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderQueryController.class)
class OrderQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderQueryService queryService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Maria");
    }

    @Test
    void deveConsultarPedidosComSucesso() throws Exception {
        // Arrange
        List<UserDTO> result = Collections.singletonList(userDTO);
        when(queryService.consultarPedidos(
                eq(Optional.of(1L)),
                eq(Optional.of(LocalDate.of(2025, 1, 1))),
                eq(Optional.of(LocalDate.of(2025, 1, 2)))))
                .thenReturn(result);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                        .param("orderId", "1")
                        .param("dataInicio", "2025-01-01")
                        .param("dataFim", "2025-01-02")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Maria"));

        verify(queryService, times(1)).consultarPedidos(
                eq(Optional.of(1L)),
                eq(Optional.of(LocalDate.of(2025, 1, 1))),
                eq(Optional.of(LocalDate.of(2025, 1, 2))));
    }

    @Test
    void deveRetornarBadRequestParaDatasInvalidas() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                        .param("dataInicio", "2025-01-02")
                        .param("dataFim", "2025-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(queryService, never()).consultarPedidos(any(), any(), any());
    }

    @Test
    void deveRetornarBadRequestParaIllegalArgumentException() throws Exception {
        // Arrange
        when(queryService.consultarPedidos(
                eq(Optional.of(1L)),
                eq(Optional.of(LocalDate.of(2025, 1, 1))),
                eq(Optional.of(LocalDate.of(2025, 1, 2)))))
                .thenThrow(new IllegalArgumentException("Parâmetros inválidos"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                        .param("orderId", "1")
                        .param("dataInicio", "2025-01-01")
                        .param("dataFim", "2025-01-02")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(queryService, times(1)).consultarPedidos(
                eq(Optional.of(1L)),
                eq(Optional.of(LocalDate.of(2025, 1, 1))),
                eq(Optional.of(LocalDate.of(2025, 1, 2))));
    }

    @Test
    void deveListarUsuariosComSucessoOrdenacaoPadrao() throws Exception {
        // Arrange
        List<UserDTO> result = Collections.singletonList(userDTO);
        when(queryService.buscarUsuariosOrdenados(eq("id"), eq("asc"))).thenReturn(result);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Maria"));

        verify(queryService, times(1)).buscarUsuariosOrdenados(eq("id"), eq("asc"));
    }

    @Test
    void deveListarUsuariosComSucessoOrdenacaoCustomizada() throws Exception {
        // Arrange
        List<UserDTO> result = Collections.singletonList(userDTO);
        when(queryService.buscarUsuariosOrdenados(eq("name"), eq("desc"))).thenReturn(result);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .param("order-by", "name")
                        .param("direction", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Maria"));

        verify(queryService, times(1)).buscarUsuariosOrdenados(eq("name"), eq("desc"));
    }

    @Test
    void deveRetornarInternalServerErrorParaListarUsuarios() throws Exception {
        // Arrange
        when(queryService.buscarUsuariosOrdenados(eq("id"), eq("asc")))
                .thenThrow(new RuntimeException("Erro interno"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Erro interno do servidor: Erro interno"));

        verify(queryService, times(1)).buscarUsuariosOrdenados(eq("id"), eq("asc"));
    }

    @Test
    void deveLimparDadosComSucesso() throws Exception {
        // Arrange
        doNothing().when(queryService).limparDados();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(queryService, times(1)).limparDados();
    }

    @Test
    void deveRetornarInternalServerErrorParaLimparDados() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Erro interno")).when(queryService).limparDados();

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Erro interno do servidor: Erro interno"));

        verify(queryService, times(1)).limparDados();
    }
}