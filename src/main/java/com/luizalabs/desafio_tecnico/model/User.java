package com.luizalabs.desafio_tecnico.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}
