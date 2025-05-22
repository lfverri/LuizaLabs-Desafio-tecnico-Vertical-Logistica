package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.ParsedLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParsedLineServiceTest {

    private ParsedLineService parsedLineService;

    @BeforeEach
    void configurar() {
        parsedLineService = new ParsedLineService();
    }

    @Test
    void testarParseamentoLinhaValida() {
        // Arrange
        String linha = formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000123.45", "20231215");
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertTrue(erros.isEmpty(), "Não deve ter erros");
        assertEquals(1L, resultado.userId(), "User ID deve ser 1");
        assertEquals("João Silva", resultado.name(), "Nome deve ser João Silva");
        assertEquals(1L, resultado.orderId(), "Order ID deve ser 1");
        assertEquals(1L, resultado.productId(), "Product ID deve ser 1");
        assertEquals(new BigDecimal("123.45"), resultado.value(), "Valor deve ser 123.45");
        assertEquals(LocalDate.of(2023, 12, 15), resultado.date(), "Data deve ser 2023-12-15");
    }

    @Test
    void testarParseamentoLinhaComErrosVariados() {
        // Arrange
        String linha = formatarLinha("000000ABC1", "", "0000000002", "-000000003", "-0000500.00", "20241315");
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        System.out.println("Erros: " + erros); // Depuração
        assertNull(resultado, "Resultado deve ser nulo devido a erros");
        assertEquals(5, erros.size(), "Deve ter 5 erros");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 1: User ID inválido: '000000ABC1'")), "Deve ter erro de User ID inválido");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 1: Nome não pode estar vazio")), "Deve ter erro de nome vazio");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 1: Product ID deve ser positivo. Valor: -3")), "Deve ter erro de Product ID negativo");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 1: Valor não pode ser negativo. Valor: -500.00")), "Deve ter erro de valor negativo");
        assertTrue(erros.stream().anyMatch(e -> e.contains("Linha 1: Data inválida: '20241315'") && e.contains("Invalid value for MonthOfYear")), "Deve ter erro de data inválida");
    }

    @Test
    void testarParseamentoLinhaCurta() {
        // Arrange
        String linha = "0000000001João Silva";
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        assertNull(resultado, "Resultado deve ser nulo devido a linha curta");
        assertEquals(1, erros.size(), "Deve ter 1 erro");
        assertTrue(erros.get(0).contains("Linha 1: Campos fora do intervalo esperado"), "Deve ter erro de linha curta");
    }

    @Test
    void testarParseamentoLinhaComValorZero() {
        // Arrange
        String linha = formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000000.00", "20231215");
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertTrue(erros.isEmpty(), "Não deve ter erros");
        assertEquals(new BigDecimal("0.00"), resultado.value(), "Valor deve ser 0.00");
    }

    @Test
    void testarParseamentoLinhaComUserIdZero() {
        // Arrange
        String linha = formatarLinha("0000000000", "João Silva", "0000000001", "0000000001", "0000123.45", "20231215");
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        assertNull(resultado, "Resultado deve ser nulo devido a User ID inválido");
        assertEquals(1, erros.size(), "Deve ter 1 erro");
        assertEquals("Linha 1: User ID deve ser positivo. Valor: 0", erros.get(0), "Deve ter erro de User ID zero");
    }

    @Test
    void testarParseamentoLinhaComDataAnoBissextoValida() {
        // Arrange
        String linha = formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000123.45", "20200229");
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertTrue(erros.isEmpty(), "Não deve ter erros");
        assertEquals(LocalDate.of(2020, 2, 29), resultado.date(), "Data deve ser 2020-02-29");
    }

    @Test
    void testarParseamentoLinhaComDataAnoNaoBissextoInvalida() {
        // Arrange
        String linha = formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000123.45", "20210229");
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        assertNull(resultado, "Resultado deve ser nulo devido a data inválida");
        assertEquals(1, erros.size(), "Deve ter 1 erro");
        assertTrue(erros.get(0).contains("Linha 1: Data inválida: '20210229'"), "Deve ter erro de data inválida");
    }

    @Test
    void testarParseamentoLinhaComDataAnoAntigo() {
        // Arrange
        String linha = formatarLinha("0000000001", "João Silva", "0000000001", "0000000001", "0000123.45", "18001215");
        List<String> erros = new ArrayList<>();
        int numeroLinha = 1;

        // Act
        ParsedLine resultado = parsedLineService.parse(linha, numeroLinha, erros);

        // Assert
        assertNotNull(resultado, "Resultado não deve ser nulo");
        assertTrue(erros.isEmpty(), "Não deve ter erros");
        assertEquals(LocalDate.of(1800, 12, 15), resultado.date(), "Data deve ser 1800-12-15");
    }

    private String formatarLinha(String userId, String nome, String orderId, String productId, String valor, String data) {
        String nomePreenchido = String.format("%-45s", nome); // Preenche com espaços até 45 caracteres
        String valorPreenchido = String.format("%12s", valor); // Preenche com espaços à esquerda até 12 caracteres
        return userId + nomePreenchido + orderId + productId + valorPreenchido + data;
    }
}