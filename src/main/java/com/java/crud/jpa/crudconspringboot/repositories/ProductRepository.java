package com.java.crud.jpa.crudconspringboot.repositories;

import com.java.crud.jpa.crudconspringboot.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

    //validamos en la base de datos si un sku existe en este caso
    boolean existsBySku(String sku);
}
