package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.FileProcessingResultDTO;
import com.luizalabs.desafio_tecnico.dto.OrderDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import com.luizalabs.desafio_tecnico.dto.ProductDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileProcessingService {

    public FileProcessingResultDTO processFile(MultipartFile file) throws IOException {
        Map<Long, UserDTO> userMap = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.length() < 95) {
                    errors.add("Linha " + lineNumber + ": Linha muito curta para ser processada.");
                    lineNumber++;
                  continue;
                }

                try {
                    Long userId = Long.parseLong(line.substring(0, 10).trim());
                    String name = line.substring(10, 55).trim();
                    Long orderId = Long.parseLong(line.substring(55, 65).trim());
                    Long productId = Long.parseLong(line.substring(65, 75).trim());
                    String valueStr = line.substring(75, 87).trim();
                    BigDecimal value = new BigDecimal(valueStr);
                    String dateRaw = line.substring(87, 95).trim();
                    String formattedDate = dateRaw.substring(0, 4) + "-" +
                            dateRaw.substring(4, 6) + "-" +
                            dateRaw.substring(6);

                    UserDTO user = userMap.computeIfAbsent(userId, id -> {
                        UserDTO u = new UserDTO();
                        u.setId(id);
                        u.setName(name);
                        u.setOrders(new ArrayList<>());
                        return u;
                    });

                    OrderDTO order = user.getOrders().stream()
                            .filter(o -> o.getId().equals(orderId))
                            .findFirst()
                            .orElseGet(() -> {
                                OrderDTO o = new OrderDTO();
                                o.setId(orderId);
                                o.setDate(formattedDate);
                                o.setUser(user);
                                o.setProducts(new ArrayList<>());
                                user.getOrders().add(o);
                                return o;
                            });

                    ProductDTO product = new ProductDTO();
                    product.setId(productId);
                    product.setPrice(value);
                    order.getProducts().add(product);

                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    errors.add("Linha " + lineNumber + ": Erro ao processar. " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }

                lineNumber++;
            }
        }

        FileProcessingResultDTO result = new FileProcessingResultDTO();
        result.setUsers(new ArrayList<>(userMap.values()));
        result.setErrors(errors);

        return result;
    }
}