package com.java.crud.jpa.crudconspringboot.repositories;

import com.java.crud.jpa.crudconspringboot.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
