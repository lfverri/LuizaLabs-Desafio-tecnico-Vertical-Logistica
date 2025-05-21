package com.luizalabs.desafio_tecnico.repository;

import com.luizalabs.desafio_tecnico.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
