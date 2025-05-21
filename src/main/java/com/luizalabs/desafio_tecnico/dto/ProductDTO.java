package com.luizalabs.desafio_tecnico.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private BigDecimal price;
}
