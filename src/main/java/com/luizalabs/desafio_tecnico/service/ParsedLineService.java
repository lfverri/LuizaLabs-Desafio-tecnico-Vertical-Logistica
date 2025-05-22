package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.ParsedLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service

public class ParsedLineService {
    public ParsedLine parse(String line, int lineNumber, List<String> errors) {
        try {
            Long userId = parseLong(line.substring(0, 10), "User ID", lineNumber, errors);
            String name = parseText(line.substring(10, 55), "Nome", lineNumber, errors);
            Long orderId = parseLong(line.substring(55, 65), "Order ID", lineNumber, errors);
            Long productId = parseLong(line.substring(65, 75), "Product ID", lineNumber, errors);
            BigDecimal value = parseDecimal(line.substring(75, 87), "Valor", lineNumber, errors);
            LocalDate date = parseDate(line.substring(87, 95), lineNumber, errors);

            if (Stream.of(userId, name, orderId, productId, value, date).anyMatch(Objects::isNull)) {
                return null; // já registrou erro
            }

            return new ParsedLine(userId, name, orderId, productId, value, date);

        } catch (StringIndexOutOfBoundsException e) {
            errors.add(error(lineNumber, "Campos fora do intervalo esperado: " + e.getMessage()));
            return null;
        }
    }

    private Long parseLong(String str, String fieldName, int line, List<String> errors) {
        try {
            Long val = Long.parseLong(str.trim());
            if (val <= 0) {
                errors.add(error(line, fieldName + " deve ser positivo. Valor: " + val));
                return null;
            }
            return val;
        } catch (NumberFormatException e) {
            errors.add(error(line, fieldName + " inválido: '" + str.trim() + "'"));
            return null;
        }
    }

    private String parseText(String str, String fieldName, int line, List<String> errors) {
        String value = str.trim();
        if (value.isEmpty()) {
            errors.add(error(line, fieldName + " não pode estar vazio"));
            return null;
        }
        return value;
    }

    private BigDecimal parseDecimal(String str, String fieldName, int line, List<String> errors) {
        try {
            BigDecimal val = new BigDecimal(str.trim());
            if (val.compareTo(BigDecimal.ZERO) < 0) {
                errors.add(error(line, fieldName + " não pode ser negativo. Valor: " + val));
                return null;
            }
            return val;
        } catch (NumberFormatException e) {
            errors.add(error(line, fieldName + " inválido: '" + str.trim() + "'"));
            return null;
        }
    }

    private LocalDate parseDate(String str, int line, List<String> errors) {
        if (str.trim().length() != 8) {
            errors.add(error(line, "Data deve ter 8 dígitos (yyyymmdd). Valor: '" + str.trim() + "'"));
            return null;
        }

        try {
            int year = Integer.parseInt(str.substring(0, 4));
            int month = Integer.parseInt(str.substring(4, 6));
            int day = Integer.parseInt(str.substring(6, 8));
            return LocalDate.of(year, month, day);
        } catch (DateTimeException | NumberFormatException e) {
            errors.add(error(line, "Data inválida: '" + str.trim() + "' - " + e.getMessage()));
            return null;
        }
    }

    private String error(int line, String message) {
        return "Linha " + line + ": " + message;
    }

}