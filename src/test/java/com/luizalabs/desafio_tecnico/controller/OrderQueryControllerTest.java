package com.luizalabs.desafio_tecnico.controller;

import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.service.OrderQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderQueryControllerTest {

    private MockMvc mockMvc;
    private OrderQueryService queryService;
    private OrderQueryController controller;

    @BeforeEach
    void setup() {
        queryService = mock(OrderQueryService.class);
        controller = new OrderQueryController(queryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveConsultarPedidosComSucessoComTodosParametros() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Maria");
        List<UserDTO> result = Collections.singletonList(userDTO);

        when(queryService.consultarPedidos(
                Optional.of(101L),
                Optional.of(LocalDate.of(2024, 5, 19)),
                Optional.of(LocalDate.of(2024, 5, 21))
        )).thenReturn(result);

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                        .param("orderId", "101")
                        .param("dataInicio", "2024-05-19")
                        .param("dataFim", "2024-05-21")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Maria"));

        verify(queryService, times(1)).consultarPedidos(
                Optional.of(101L),
                Optional.of(LocalDate.of(2024, 5, 19)),
                Optional.of(LocalDate.of(2024, 5, 21))
        );
    }

    @Test
    void deveConsultarPedidosComSucessoSemParametros() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("João");
        List<UserDTO> result = Collections.singletonList(userDTO);

        when(queryService.consultarPedidos(Optional.empty(), Optional.empty(), Optional.empty()))
                .thenReturn(result);

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("João"));

        verify(queryService, times(1)).consultarPedidos(
                Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    @Test
    void deveRetornarBadRequestParaIntervaloDeDatasInvalido() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                        .param("dataInicio", "2024-05-21")
                        .param("dataFim", "2024-05-19")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("[]"));
    }

    @Test
    void deveRetornarBadRequestParaIntervaloDeDatasInvalidoSemChamarServico() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders")
                        .param("dataInicio", "2024-05-21")
                        .param("dataFim", "2024-05-19")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("[]"));

        verify(queryService, never()).consultarPedidos(any(), any(), any());
    }

    @Test
    void deveConsultarPedidosComSucessoComIntervaloValido() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Maria");
        List<UserDTO> result = Collections.singletonList(userDTO);

        when(queryService.consultarPedidos(
                Optional.empty(),
                Optional.of(LocalDate.of(2024, 5, 19)),
                Optional.of(LocalDate.of(2024, 5, 21))
        )).thenReturn(result);

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                        .param("dataInicio", "2024-05-19")
                        .param("dataFim", "2024-05-21")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Maria"));

        verify(queryService, times(1)).consultarPedidos(
                Optional.ofNullable(null),
                Optional.of(LocalDate.of(2024, 5, 19)),
                Optional.of(LocalDate.of(2024, 5, 21))
        );
    }
    @Test
    void deveListarUsuariosComSucesso() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Maria");
        List<UserDTO> result = Collections.singletonList(userDTO);

        when(queryService.buscarTodosUsuarios()).thenReturn(result);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Maria"));

        verify(queryService, times(1)).buscarTodosUsuarios();
    }

    @Test
    void deveListarUsuariosVazioComSucesso() throws Exception {
        // Arrange
        when(queryService.buscarTodosUsuarios()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(queryService, times(1)).buscarTodosUsuarios();
    }

    @Test
    void deveLimparDadosComSucesso() throws Exception {
        // Arrange
        doNothing().when(queryService).limparDados();

        // Act & Assert
        mockMvc.perform(delete("/api/clear"))
                .andExpect(status().isNoContent());

        verify(queryService, times(1)).limparDados();
    }
}