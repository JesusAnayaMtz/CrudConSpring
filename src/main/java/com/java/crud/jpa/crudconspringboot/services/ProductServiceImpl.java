package com.java.crud.jpa.crudconspringboot.services;

import com.java.crud.jpa.crudconspringboot.entities.Product;
import com.java.crud.jpa.crudconspringboot.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    //Clase para tener la logica del uso del crud

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)  //par que solo pueda leer datos
    @Override
    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();  //devuelve un iterable por eso se castea a una lista de productos
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
    @Transactional
    @Override
    public Optional<Product> delete(Product product) {
        Optional<Product> productOptional = productRepository.findById(product.getId());  //primero buscamos el producto con un optional pasandole el id
        productOptional.ifPresent(productDb -> {  //validamos que exista
            productRepository.delete(productDb);  //una ves encontrado eliminamos e producto
        });
        return productOptional; //devulvemos el product db para confirma que se elimino
    }
}
