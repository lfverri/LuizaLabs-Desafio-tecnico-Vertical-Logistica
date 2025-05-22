package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class FileProcessingService {

    public FileProcessingResultDTO processFile(MultipartFile file) throws IOException {
        Map<Long, UserDTO> userMap = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    lineNumber++;
                    continue;
                }

                if (line.length() < 95) {
                    errors.add(error(lineNumber, "Padrão inválido (tamanho: " + line.length() + ", esperado: 95)."));
                    lineNumber++;
                    continue;
                }

                try {
                    ParsedLine parsed = parseLine(line, lineNumber, errors);
                    if (parsed != null) {
                        addToMap(userMap, parsed);
                    }
                } catch (Exception e) {
                    errors.add(error(lineNumber, "Erro inesperado: " + e.getMessage()));
                }

                lineNumber++;
            }
        }

        FileProcessingResultDTO result = new FileProcessingResultDTO();
        result.setUsers(new ArrayList<>(userMap.values()));
        result.setErrors(errors);
        return result;
    }

    private ParsedLine parseLine(String line, int lineNumber, List<String> errors) {
        try {
            Long userId = parseLong(line.substring(0, 10), "User ID", lineNumber, errors);
            String name = parseText(line.substring(10, 55), "Nome", lineNumber, errors);
            Long orderId = parseLong(line.substring(55, 65), "Order ID", lineNumber, errors);
            Long productId = parseLong(line.substring(65, 75), "Product ID", lineNumber, errors);
            BigDecimal value = parseDecimal(line.substring(75, 87), "Valor", lineNumber, errors);
            LocalDate date = parseDate(line.substring(87, 95), lineNumber, errors);

            if (Stream.of(userId, name, orderId, productId, value, date).anyMatch(Objects::isNull)) {
                return null;
            }

            return new ParsedLine(userId, name, orderId, productId, value, date);
        } catch (StringIndexOutOfBoundsException e) {
            errors.add(error(lineNumber, "Campos fora do intervalo esperado: " + e.getMessage()));
            return null;
        }
    }

    private void addToMap(Map<Long, UserDTO> map, ParsedLine parsed) {
        UserDTO user = map.computeIfAbsent(parsed.userId(), id -> {
            UserDTO u = new UserDTO();
            u.setId(id);
            u.setName(parsed.name());
            u.setOrders(new ArrayList<>());
            return u;
        });

        OrderDTO order = user.getOrders().stream()
                .filter(o -> o.getId().equals(parsed.orderId()))
                .findFirst()
                .orElseGet(() -> {
                    OrderDTO o = new OrderDTO();
                    o.setId(parsed.orderId());
                    o.setDate(parsed.date().toString());
                    o.setUser(user);
                    o.setProducts(new ArrayList<>());
                    user.getOrders().add(o);
                    return o;
                });

        ProductDTO product = new ProductDTO();
        product.setId(parsed.productId());
        product.setPrice(parsed.value());
        order.getProducts().add(product);
    }


    private Long parseLong(String str, String fieldName, int lineNumber, List<String> errors) {
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            errors.add(error(lineNumber, "Campo '" + fieldName + "' inválido: " + str));
            return null;
        }
    }

    private String parseText(String str, String fieldName, int lineNumber, List<String> errors) {
        String text = str.trim();
        if (text.isEmpty()) {
            errors.add(error(lineNumber, "Campo '" + fieldName + "' vazio."));
            return null;
        }
        return text;
    }

    private BigDecimal parseDecimal(String str, String fieldName, int lineNumber, List<String> errors) {
        try {
            String cleanStr = str.trim().replace(",", ".");
            return new BigDecimal(cleanStr);
        } catch (NumberFormatException e) {
            errors.add(error(lineNumber, "Campo '" + fieldName + "' inválido: " + str));
            return null;
        }
    }

    private LocalDate parseDate(String str, int lineNumber, List<String> errors) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(str.trim(), formatter);
        } catch (DateTimeParseException e) {
            errors.add(error(lineNumber, "Data inválida: " + str));
            return null;
        }
    }

    private String error(int lineNumber, String msg) {
        return "Linha " + lineNumber + ": " + msg;
    }

}