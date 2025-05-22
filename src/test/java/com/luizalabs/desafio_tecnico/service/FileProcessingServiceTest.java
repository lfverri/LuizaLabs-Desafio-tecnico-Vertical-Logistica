package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.FileProcessingResultDTO;
import com.luizalabs.desafio_tecnico.dto.OrderDTO;
import com.luizalabs.desafio_tecnico.dto.ProductDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

    private FileProcessingService fileProcessingService;

    @BeforeEach
    void configurar() {
        fileProcessingService = new FileProcessingService();
    }

    @Test
    void testarProcessamentoArquivoComTodasLinhasValidas() throws IOException {
        // Arrange
        String conteudoTeste =
                formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000123.45", "20241215") + "\n" +
                        formatarLinha("0000000002", "Maria Santos", "0000000002", "0000000002", "0000256.789", "20241216");

        MockMultipartFile arquivoMock = new MockMultipartFile(
                "arquivo",
                "teste.txt",
                "text/plain",
                conteudoTeste.getBytes()
        );

        // Act
        FileProcessingResultDTO resultado = fileProcessingService.processFile(arquivoMock);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertEquals(2, resultado.getUsers().size(), "Deve ter 2 usuários válidos");
        assertTrue(resultado.getErrors().isEmpty(), "Não deve ter erros");

        List<Long> idsUsuarios = resultado.getUsers().stream().map(UserDTO::getId).sorted().collect(Collectors.toList());
        assertEquals(Arrays.asList(1L, 2L), idsUsuarios, "Deve ter usuários com IDs 1 e 2");

        UserDTO usuario1 = resultado.getUsers().stream().filter(u -> u.getId().equals(1L)).findFirst().orElse(null);
        assertNotNull(usuario1, "Usuário 1 deve existir");
        assertEquals("João Silva", usuario1.getName(), "Nome do usuário 1 deve ser João Silva");
        assertEquals(1, usuario1.getOrders().size(), "Usuário 1 deve ter 1 pedido");
        OrderDTO pedido1 = usuario1.getOrders().get(0);
        assertEquals(1L, pedido1.getId(), "ID do pedido 1 deve ser 1");
        assertEquals("2024-12-15", pedido1.getDate(), "Data do pedido 1 deve ser 2024-12-15");
        assertEquals(1, pedido1.getProducts().size(), "Pedido 1 deve ter 1 produto");
        ProductDTO produto1 = pedido1.getProducts().get(0);
        assertEquals(1L, produto1.getId(), "ID do produto 1 deve ser 1");
        assertEquals(new BigDecimal("123.45"), produto1.getPrice(), "Preço do produto 1 deve ser 123.45");

        UserDTO usuario2 = resultado.getUsers().stream().filter(u -> u.getId().equals(2L)).findFirst().orElse(null);
        assertNotNull(usuario2, "Usuário 2 deve existir");
        assertEquals("Maria Santos", usuario2.getName(), "Nome do usuário 2 deve ser Maria Santos");
        assertEquals(1, usuario2.getOrders().size(), "Usuário 2 deve ter 1 pedido");
        OrderDTO pedido2 = usuario2.getOrders().get(0);
        assertEquals(2L, pedido2.getId(), "ID do pedido 2 deve ser 2");
        assertEquals("2024-12-16", pedido2.getDate(), "Data do pedido 2 deve ser 2024-12-16");
        assertEquals(1, pedido2.getProducts().size(), "Pedido 2 deve ter 1 produto");
        ProductDTO produto2 = pedido2.getProducts().get(0);
        assertEquals(2L, produto2.getId(), "ID do produto 2 deve ser 2");
        assertEquals(new BigDecimal("256.789"), produto2.getPrice(), "Preço do produto 2 deve ser 256.789");
    }

    @Test
    void testarProcessamentoArquivoComErrosVariados() throws IOException {
        // Arrange
        String conteudoTeste =
                formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000123.45", "20241215") + "\n" +
                        formatarLinha("0000000002", "Maria Santos", "0000000002", "0000000002", "0000256.78", "20241216") + "\n" +
                        formatarLinha("000000ABC3", "Pedro Oliveira", "0000000003", "0000000003", "0000150.00", "20241217") + "\n" +
                        formatarLinha("0000000004", "Ana Costa", "0000000004", "0000000004", "0000100.00", "20241232") + "\n" +
                        formatarLinha("0000000005", "Carlos Pereira", "0000000005", "0000000005", "-0000500.00", "20241218") + "\n" +
                        formatarLinha("0000000006", "", "0000000006", "0000000006", "0000300.00", "20241219") + "\n" +
                        formatarLinha("0000000007", "Fernanda Lima", "0000000007", "0000000007", "0000200.00", "20241220") + "\n" +
                        formatarLinha("0000000008", "Roberto Souza", "0000000008", "0000000008", "0000400.00", "20241221") + "\n" +
                        formatarLinha("0000000009", "Juliana Rodrigues", "0000000009", "0000000009", "0000350.00", "20241222") + "\n" +
                        "linha curta";

        MockMultipartFile arquivoMock = new MockMultipartFile(
                "arquivo",
                "teste.txt",
                "text/plain",
                conteudoTeste.getBytes()
        );

        // Act
        FileProcessingResultDTO resultado = fileProcessingService.processFile(arquivoMock);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertEquals(6, resultado.getUsers().size(), "Deve ter 6 usuários válidos (linhas 1, 2, 5, 7, 8, 9)");
        assertEquals(4, resultado.getErrors().size(), "Deve ter 4 erros");

        List<String> erros = resultado.getErrors();
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 3: Campo 'User ID' inválido: 000000ABC3")),
                "Deve ter erro de User ID inválido na linha 3");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 4: Data inválida: 20241232")),
                "Deve ter erro de data inválida na linha 4");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 6: Campo 'Nome' vazio.")),
                "Deve ter erro de nome vazio na linha 6");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 10: Linha muito curta")),
                "Deve ter erro de linha curta na linha 10");

        List<Long> idsUsuariosValidos = resultado.getUsers().stream()
                .map(UserDTO::getId)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(1L, 2L, 5L, 7L, 8L, 9L), idsUsuariosValidos,
                "Deve ter usuários com IDs 1, 2, 5, 7, 8 e 9");
    }

    @Test
    void testarProcessamentoArquivoVazio() throws IOException {
        // Arrange
        MockMultipartFile arquivoMock = new MockMultipartFile(
                "arquivo",
                "vazio.txt",
                "text/plain",
                "".getBytes()
        );

        // Act
        FileProcessingResultDTO resultado = fileProcessingService.processFile(arquivoMock);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertTrue(resultado.getUsers().isEmpty(), "Deve ter lista de usuários vazia");
        assertTrue(resultado.getErrors().isEmpty(), "Deve ter lista de erros vazia");
    }

    @Test
    void testarProcessamentoArquivoComApenasLinhasVazias() throws IOException {
        // Arrange
        String conteudoTeste = "\n\n   \n\n";

        MockMultipartFile arquivoMock = new MockMultipartFile(
                "arquivo",
                "linhas_vazias.txt",
                "text/plain",
                conteudoTeste.getBytes()
        );

        // Act
        FileProcessingResultDTO resultado = fileProcessingService.processFile(arquivoMock);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertTrue(resultado.getUsers().isEmpty(), "Deve ter lista de usuários vazia");
        assertTrue(resultado.getErrors().isEmpty(), "Não deve ter erros para linhas vazias");
    }

    @Test
    void testarProcessamentoArquivoComErrosEspecificosDeData() throws IOException {
        // Arrange
        String conteudoTeste =
                formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000123.45", "20241315") + "\n" +
                        formatarLinha("0000000002", "Maria Santos", "0000000002", "0000000002", "0000256.789", "18001215") + "\n" +
                        formatarLinha("0000000003", "Pedro Oliveira", "0000000003", "0000000003", "0000150.00", "20240229");

        MockMultipartFile arquivoMock = new MockMultipartFile(
                "arquivo",
                "erros_data.txt",
                "text/plain",
                conteudoTeste.getBytes()
        );

        // Act
        FileProcessingResultDTO resultado = fileProcessingService.processFile(arquivoMock);

        // Assert
        System.out.println("Erros: " + resultado.getErrors()); // Depuração
        assertEquals(1, resultado.getErrors().size(), "Deve ter 1 erro de data");
        assertEquals(2, resultado.getUsers().size(), "Deve ter 2 usuários válidos (linhas 2 e 3)");

        List<String> erros = resultado.getErrors();
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 1: Data inválida") && e.contains("20241315")), "Deve ter erro de mês inválido na linha 1");

        List<Long> idsUsuariosValidos = resultado.getUsers().stream().map(UserDTO::getId).sorted().collect(Collectors.toList());
        assertEquals(Arrays.asList(2L, 3L), idsUsuariosValidos, "Deve ter usuários com IDs 2 e 3");
    }

    @Test
    void testarProcessamentoArquivoComLinhaCurta() throws IOException {
        // Arrange
        String conteudoTeste = "0000000001João Silva";

        MockMultipartFile arquivoMock = new MockMultipartFile(
                "arquivo",
                "curta.txt",
                "text/plain",
                conteudoTeste.getBytes()
        );

        // Act
        FileProcessingResultDTO resultado = fileProcessingService.processFile(arquivoMock);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertTrue(resultado.getUsers().isEmpty(), "Não deve ter usuários válidos");
        assertEquals(1, resultado.getErrors().size(), "Deve ter 1 erro");
        assertTrue(resultado.getErrors().get(0).contains("Linha 1: Linha muito curta (tamanho: 20, esperado: 95)."), "Deve ter erro de linha curta");
    }

    // Método auxiliar para formatar linhas com tamanho exato de 95 caracteres
    private String formatarLinha(String userId, String nome, String orderId, String productId, String valor, String data) {
        String nomePreenchido = String.format("%-45s", nome); // Preenche com espaços até 45 caracteres
        String valorPreenchido = String.format("%12s", valor); // Preenche com espaços à esquerda até 12 caracteres
        return userId + nomePreenchido + orderId + productId + valorPreenchido + data;
    }
}