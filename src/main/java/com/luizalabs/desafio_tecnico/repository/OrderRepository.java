package com.luizalabs.desafio_tecnico.repository;

import com.luizalabs.desafio_tecnico.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
