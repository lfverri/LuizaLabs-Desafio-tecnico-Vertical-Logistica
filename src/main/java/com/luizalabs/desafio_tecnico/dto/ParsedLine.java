package com.luizalabs.desafio_tecnico.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParsedLine(
        Long userId,
        String name,
        Long orderId,
        Long productId,
        BigDecimal value,
        LocalDate date
) {}