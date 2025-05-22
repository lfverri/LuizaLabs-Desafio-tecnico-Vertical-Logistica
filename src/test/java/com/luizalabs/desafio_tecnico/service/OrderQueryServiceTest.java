package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.OrderDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OrderQueryServiceTest {

    private OrderQueryService orderQueryService;
    private List<UserDTO> testUsers;

    @BeforeEach
    void setUp() {
        orderQueryService = new OrderQueryService();
        testUsers = createTestUsers();
        orderQueryService.salvarPedidos(testUsers);
    }

    private List<UserDTO> createTestUsers() {
        List<UserDTO> users = new ArrayList<>();

        // Usuário 1
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setName("João Silva");

        List<OrderDTO> orders1 = new ArrayList<>();
        OrderDTO order1 = new OrderDTO();
        order1.setId(101L);
        order1.setDate("2024-01-15");
        orders1.add(order1);

        OrderDTO order2 = new OrderDTO();
        order2.setId(102L);
        order2.setDate("2024-02-20");
        orders1.add(order2);

        user1.setOrders(orders1);
        users.add(user1);

        // Usuário 2
        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setName("Maria Santos");

        List<OrderDTO> orders2 = new ArrayList<>();
        OrderDTO order3 = new OrderDTO();
        order3.setId(103L);
        order3.setDate("2024-01-10");
        orders2.add(order3);

        user2.setOrders(orders2);
        users.add(user2);

        // Usuário 3
        UserDTO user3 = new UserDTO();
        user3.setId(3L);
        user3.setName("Ana Costa");

        List<OrderDTO> orders3 = new ArrayList<>();
        OrderDTO order4 = new OrderDTO();
        order4.setId(104L);
        order4.setDate("2024-03-01");
        orders3.add(order4);

        user3.setOrders(orders3);
        users.add(user3);

        return users;
    }

    @Nested
    @DisplayName("Testes para consultarPedidos")
    class ConsultarPedidosTests {

        @Test
        @DisplayName("Deve retornar todos os pedidos quando nenhum filtro é aplicado")
        void deveRetornarTodosPedidosQuandoNenhumFiltroAplicado() {
            List<UserDTO> resultado = orderQueryService.consultarPedidos(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );

            assertEquals(3, resultado.size());
            assertEquals(2, resultado.get(0).getOrders().size()); // João tem 2 pedidos
            assertEquals(1, resultado.get(1).getOrders().size()); // Maria tem 1 pedido
            assertEquals(1, resultado.get(2).getOrders().size()); // Ana tem 1 pedido
        }

        @Test
        @DisplayName("Deve filtrar por ID do pedido específico")
        void deveFiltrarPorIdPedidoEspecifico() {
            List<UserDTO> resultado = orderQueryService.consultarPedidos(
                    Optional.of(101L),
                    Optional.empty(),
                    Optional.empty()
            );

            assertEquals(1, resultado.size());
            assertEquals("João Silva", resultado.get(0).getName());
            assertEquals(1, resultado.get(0).getOrders().size());
            assertEquals(101L, resultado.get(0).getOrders().get(0).getId());
        }

        @Test
        @DisplayName("Deve filtrar por data de início")
        void deveFiltrarPorDataInicio() {
            LocalDate dataInicio = LocalDate.of(2024, 2, 1);

            List<UserDTO> resultado = orderQueryService.consultarPedidos(
                    Optional.empty(),
                    Optional.of(dataInicio),
                    Optional.empty()
            );

            assertEquals(2, resultado.size());

            // Verifica João - deve ter apenas 1 pedido (o de fevereiro)
            UserDTO joao = resultado.stream()
                    .filter(u -> u.getName().equals("João Silva"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(joao);
            assertEquals(1, joao.getOrders().size());
            assertEquals("2024-02-20", joao.getOrders().get(0).getDate());

            // Verifica Ana - deve ter 1 pedido (o de março)
            UserDTO ana = resultado.stream()
                    .filter(u -> u.getName().equals("Ana Costa"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(ana);
            assertEquals(1, ana.getOrders().size());
        }

        @Test
        @DisplayName("Deve filtrar por data de fim")
        void deveFiltrarPorDataFim() {
            LocalDate dataFim = LocalDate.of(2024, 1, 31);

            List<UserDTO> resultado = orderQueryService.consultarPedidos(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(dataFim)
            );

            assertEquals(2, resultado.size());

            // Verifica se contém apenas pedidos até janeiro
            resultado.forEach(user -> {
                user.getOrders().forEach(order -> {
                    LocalDate dataPedido = LocalDate.parse(order.getDate());
                    assertTrue(dataPedido.isBefore(dataFim) || dataPedido.isEqual(dataFim));
                });
            });
        }

        @Test
        @DisplayName("Deve filtrar por intervalo de datas")
        void deveFiltrarPorIntervaloDatas() {
            LocalDate dataInicio = LocalDate.of(2024, 1, 12);
            LocalDate dataFim = LocalDate.of(2024, 2, 25);

            List<UserDTO> resultado = orderQueryService.consultarPedidos(
                    Optional.empty(),
                    Optional.of(dataInicio),
                    Optional.of(dataFim)
            );

            assertEquals(1, resultado.size());
            assertEquals("João Silva", resultado.get(0).getName());
            assertEquals(2, resultado.get(0).getOrders().size());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nenhum pedido atende aos critérios")
        void deveRetornarListaVaziaQuandoNenhumPedidoAtendeCriterios() {
            List<UserDTO> resultado = orderQueryService.consultarPedidos(
                    Optional.of(999L),
                    Optional.empty(),
                    Optional.empty()
            );

            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve combinar filtros de ID e data")
        void deveCombinarFiltrosIdEData() {
            List<UserDTO> resultado = orderQueryService.consultarPedidos(
                    Optional.of(102L),
                    Optional.of(LocalDate.of(2024, 2, 1)),
                    Optional.of(LocalDate.of(2024, 2, 28))
            );

            assertEquals(1, resultado.size());
            assertEquals("João Silva", resultado.get(0).getName());
            assertEquals(1, resultado.get(0).getOrders().size());
            assertEquals(102L, resultado.get(0).getOrders().get(0).getId());
        }
    }

    @Nested
    @DisplayName("Testes para salvarPedidos")
    class SalvarPedidosTests {

        @Test
        @DisplayName("Deve salvar novos pedidos substituindo os existentes")
        void deveSalvarNovosPedidosSubstituindoExistentes() {
            List<UserDTO> novosPedidos = new ArrayList<>();
            UserDTO novoUser = new UserDTO();
            novoUser.setId(10L);
            novoUser.setName("Novo Usuário");
            novoUser.setOrders(new ArrayList<>());
            novosPedidos.add(novoUser);

            orderQueryService.salvarPedidos(novosPedidos);

            List<UserDTO> resultado = orderQueryService.buscarTodosUsuarios();
            assertEquals(1, resultado.size());
            assertEquals("Novo Usuário", resultado.get(0).getName());
        }

        @Test
        @DisplayName("Deve permitir salvar lista vazia")
        void devePermitirSalvarListaVazia() {
            orderQueryService.salvarPedidos(new ArrayList<>());

            List<UserDTO> resultado = orderQueryService.buscarTodosUsuarios();
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("Testes para buscarTodosUsuarios")
    class BuscarTodosUsuariosTests {

        @Test
        @DisplayName("Deve retornar todos os usuários salvos")
        void deveRetornarTodosUsuariosSalvos() {
            List<UserDTO> resultado = orderQueryService.buscarTodosUsuarios();

            assertEquals(3, resultado.size());
            assertEquals("João Silva", resultado.get(0).getName());
            assertEquals("Maria Santos", resultado.get(1).getName());
            assertEquals("Ana Costa", resultado.get(2).getName());
        }

        @Test
        @DisplayName("Deve retornar nova instância da lista")
        void deveRetornarNovaInstanciaDaLista() {
            List<UserDTO> resultado1 = orderQueryService.buscarTodosUsuarios();
            List<UserDTO> resultado2 = orderQueryService.buscarTodosUsuarios();

            assertNotSame(resultado1, resultado2);
            assertEquals(resultado1.size(), resultado2.size());
        }
    }

    @Nested
    @DisplayName("Testes para buscarUsuarioPorId")
    class BuscarUsuarioPorIdTests {

        @Test
        @DisplayName("Deve encontrar usuário por ID existente")
        void deveEncontrarUsuarioPorIdExistente() {
            Optional<UserDTO> resultado = orderQueryService.buscarUsuarioPorId(2L);

            assertTrue(resultado.isPresent());
            assertEquals("Maria Santos", resultado.get().getName());
            assertEquals(2L, resultado.get().getId());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio para ID inexistente")
        void deveRetornarOptionalVazioParaIdInexistente() {
            Optional<UserDTO> resultado = orderQueryService.buscarUsuarioPorId(999L);

            assertFalse(resultado.isPresent());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio para ID null")
        void deveRetornarOptionalVazioParaIdNull() {
            Optional<UserDTO> resultado = orderQueryService.buscarUsuarioPorId(null);

            assertFalse(resultado.isPresent());
        }
    }

    @Nested
    @DisplayName("Testes para limparDados")
    class LimparDadosTests {

        @Test
        @DisplayName("Deve limpar todos os dados")
        void deveLimparTodosDados() {
            orderQueryService.limparDados();

            List<UserDTO> resultado = orderQueryService.buscarTodosUsuarios();
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve permitir adicionar dados após limpeza")
        void devePermitirAdicionarDadosAposLimpeza() {
            orderQueryService.limparDados();

            List<UserDTO> novosDados = createTestUsers();
            orderQueryService.salvarPedidos(novosDados);

            List<UserDTO> resultado = orderQueryService.buscarTodosUsuarios();
            assertEquals(3, resultado.size());
        }
    }

    @Nested
    @DisplayName("Testes para buscarUsuariosOrdenados")
    class BuscarUsuariosOrdenadosTests {

        @ParameterizedTest
        @MethodSource("parametrosOrdenacao")
        @DisplayName("Deve ordenar usuários corretamente")
        void deveOrdenarUsuariosCorretamente(String orderBy, String direction, String[] expectedOrder) {
            List<UserDTO> resultado = orderQueryService.buscarUsuariosOrdenados(orderBy, direction);

            assertEquals(3, resultado.size());
            for (int i = 0; i < expectedOrder.length; i++) {
                assertEquals(expectedOrder[i], resultado.get(i).getName());
            }
        }

        private static Stream<Arguments> parametrosOrdenacao() {
            return Stream.of(
                    Arguments.of("name", "asc", new String[]{"Ana Costa", "João Silva", "Maria Santos"}),
                    Arguments.of("name", "desc", new String[]{"Maria Santos", "João Silva", "Ana Costa"}),
                    Arguments.of("id", "asc", new String[]{"João Silva", "Maria Santos", "Ana Costa"}),
                    Arguments.of("id", "desc", new String[]{"Ana Costa", "Maria Santos", "João Silva"}),
                    Arguments.of("invalid", "asc", new String[]{"João Silva", "Maria Santos", "Ana Costa"}), // default para ID
                    Arguments.of("name", "invalid", new String[]{"Ana Costa", "João Silva", "Maria Santos"}) // default para ASC
            );
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há usuários")
        void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
            orderQueryService.limparDados();

            List<UserDTO> resultado = orderQueryService.buscarUsuariosOrdenados("name", "asc");

            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve tratar orderBy case insensitive")
        void deveTratarOrderByCaseInsensitive() {
            List<UserDTO> resultado1 = orderQueryService.buscarUsuariosOrdenados("NAME", "asc");
            List<UserDTO> resultado2 = orderQueryService.buscarUsuariosOrdenados("name", "asc");

            assertEquals(resultado1.size(), resultado2.size());
            for (int i = 0; i < resultado1.size(); i++) {
                assertEquals(resultado1.get(i).getName(), resultado2.get(i).getName());
            }
        }

        @Test
        @DisplayName("Deve tratar direction case insensitive")
        void deveTratarDirectionCaseInsensitive() {
            List<UserDTO> resultado1 = orderQueryService.buscarUsuariosOrdenados("name", "DESC");
            List<UserDTO> resultado2 = orderQueryService.buscarUsuariosOrdenados("name", "desc");

            assertEquals(resultado1.size(), resultado2.size());
            for (int i = 0; i < resultado1.size(); i++) {
                assertEquals(resultado1.get(i).getName(), resultado2.get(i).getName());
            }
        }
    }

    @Nested
    @DisplayName("Testes de Integração")
    class TestesIntegracao {

        @Test
        @DisplayName("Deve manter integridade dos dados em operações sequenciais")
        void deveManterIntegridadeDadosOperacoesSequenciais() {

            List<UserDTO> novosDados = createTestUsers();
            orderQueryService.salvarPedidos(novosDados);

            List<UserDTO> todos = orderQueryService.buscarTodosUsuarios();
            assertEquals(3, todos.size());

            Optional<UserDTO> usuario = orderQueryService.buscarUsuarioPorId(1L);
            assertTrue(usuario.isPresent());

            List<UserDTO> pedidos = orderQueryService.consultarPedidos(
                    Optional.empty(), Optional.empty(), Optional.empty()
            );
            assertEquals(3, pedidos.size());

            List<UserDTO> ordenados = orderQueryService.buscarUsuariosOrdenados("name", "asc");
            assertEquals(3, ordenados.size());

            orderQueryService.limparDados();
            assertTrue(orderQueryService.buscarTodosUsuarios().isEmpty());
        }
    }
}