package com.luizalabs.desafio_tecnico.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDTO {
    private Integer status;
    private String error;
    private String path;
    private Instant timestamp;
}