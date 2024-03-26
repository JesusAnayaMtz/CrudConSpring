package com.java.crud.jpa.crudconspringboot.services;

import com.java.crud.jpa.crudconspringboot.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    //clase para crear los metodos que utilizaremos para el crud
    List<Product> findAll();
    Optional<Product> findById(Long id);

    Product save(Product product);

    Optional<Product> delete(Product product);
}
