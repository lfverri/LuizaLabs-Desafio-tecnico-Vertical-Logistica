package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.OrderDTO;
import com.luizalabs.desafio_tecnico.dto.ProductDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderQueryServiceTest {

    private OrderQueryService service;

    private Long userId;
    private Long orderId;

    @BeforeEach
    void setup() {
        service = new OrderQueryService();

        // Mock de dados
        userId = 1L;
        orderId = 101L;

        ProductDTO product = new ProductDTO();
        product.setId(100L);
        product.setPrice(new BigDecimal("99.99"));

        OrderDTO order = new OrderDTO();
        order.setId(orderId);
        order.setDate("2024-05-20");
        order.setProducts(List.of(product));

        UserDTO user = new UserDTO();
        user.setId(userId);
        user.setName("Maria");
        user.setOrders(List.of(order));

        service.salvarPedidos(List.of(user));
    }

    @Test
    void deveBuscarTodosUsuarios() {
        List<UserDTO> users = service.buscarTodosUsuarios();
        assertEquals(1, users.size());
        assertEquals("Maria", users.get(0).getName());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        Optional<UserDTO> user = service.buscarUsuarioPorId(userId);
        assertTrue(user.isPresent());
        assertEquals("Maria", user.get().getName());
    }

    @Test
    void deveConsultarPedidosPorId() {
        List<UserDTO> result = service.consultarPedidos(Optional.of(orderId), Optional.empty(), Optional.empty());
        assertEquals(1, result.size());
        assertEquals(orderId, result.get(0).getOrders().get(0).getId());
    }

    @Test
    void deveConsultarPedidosPorData() {
        LocalDate inicio = LocalDate.of(2024, 5, 19);
        LocalDate fim = LocalDate.of(2024, 5, 21);

        List<UserDTO> result = service.consultarPedidos(Optional.empty(), Optional.of(inicio), Optional.of(fim));
        assertEquals(1, result.size());
    }

    @Test
    void deveRetornarListaVaziaSeForaDoIntervalo() {
        LocalDate inicio = LocalDate.of(2024, 6, 1);
        LocalDate fim = LocalDate.of(2024, 6, 30);

        List<UserDTO> result = service.consultarPedidos(Optional.empty(), Optional.of(inicio), Optional.of(fim));
        assertTrue(result.isEmpty());
    }

    @Test
    void deveLimparTodosOsPedidos() {
        service.limparDados();
        assertTrue(service.buscarTodosUsuarios().isEmpty());
    }


}
