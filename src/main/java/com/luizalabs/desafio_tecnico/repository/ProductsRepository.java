package com.luizalabs.desafio_tecnico.repository;

import com.luizalabs.desafio_tecnico.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Product, Long> {
}
