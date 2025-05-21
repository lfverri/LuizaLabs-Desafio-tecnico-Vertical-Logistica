package com.luizalabs.desafio_tecnico.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String date;

    @JsonBackReference
    private UserDTO user;

    private List<ProductDTO> products;

    public BigDecimal getTotal() {
        return products.stream()
                .map(ProductDTO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
