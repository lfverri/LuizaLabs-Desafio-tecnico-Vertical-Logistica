package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.OrderDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileProcessingService {
    public List<UserDTO> processFile(MultipartFile file)  {
        Map<Long, UserDTO> userMap = new LinkedHashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            long userId = Long.parseLong(line.substring(0, 10).trim());
            String name = line.substring(10, 55).trim();
            long orderId = Long.parseLong(line.substring(55, 65).trim());
            long productId = Long.parseLong(line.substring(65, 75).trim());
            String value = line.substring(75, 87).trim();
            String dateRaw = line.substring(87, 95).trim();
            String formattedDate = dateRaw.substring(0, 4) + "-" + dateRaw.substring(4, 6) + "-" + dateRaw.substring(6);

            // Agrupamento de produtos e pedidos
            UserDTO user = userMap.computeIfAbsent(userId, id -> {
                UserDTO u = new UserDTO();
                u.setUserId(id);
                u.setName(name);
                u.setOrders(new ArrayList<>());
                return u;
            });

            OrderDTO order = user.getOrders().stream()
                    .filter(o -> o.getOrderId() == orderId)
                    .findFirst()
                    .orElseGet(() -> {
                        OrderDTO o = new OrderDTO();
                        o.setOrderId(orderId);
                        o.setDate(formattedDate);
                        o.setProducts(new ArrayList<>());
                        user.getOrders().add(o);
                        return o;
                    });

            // Adiciona produto
            OrderProduct product = new OrderProduct();
            product.setProductId(productId);
            product.setValue(value);
            order.getProducts().add(product);
        }

        // Calcular total
        userMap.values().forEach(user -> user.getOrders().forEach(order -> {
            double total = order.getProducts().stream()
                    .mapToDouble(p -> Double.parseDouble(p.getValue()))
                    .sum();
            order.setTotal(String.format("%.2f", total));
        }));

        return new ArrayList<>(userMap.values());
    }
}

