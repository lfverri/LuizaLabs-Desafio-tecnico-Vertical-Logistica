package com.luizalabs.desafio_tecnico.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;

    @JsonManagedReference
    private List<OrderDTO> orders;
}
