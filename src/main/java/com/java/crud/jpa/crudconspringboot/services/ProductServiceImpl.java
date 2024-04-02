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
    public Optional<Product> update(Long id, Product product) {
        Optional<Product> productOptional = productRepository.findById(id);  //primero buscamos el producto con un optional pasandole el id
        if(productOptional.isPresent()) {  //validamos que exista con in if pasandole el productoptional
            Product productDb = productOptional.orElseThrow();   //instanciamos un objeto producto para obtener el producto de se busco y asiganrselo
            //seteamos los datos que se vayan autualizar
            productDb.setName(product.getName());
            productDb.setDescription(product.getDescription());
            productDb.setPrice(product.getPrice());
            productDb.setSku(product.getSku());
            return Optional.of(productRepository.save(productDb));   //retornamos el opcional pasandole el productdb actualizado
        }
        return  productOptional;
    }

    @Transactional
    @Override
    public Optional<Product> delete(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);  //primero buscamos el producto con un optional pasandole el id
        productOptional.ifPresent(productDb -> {  //validamos que exista
            productRepository.delete(productDb);  //una ves encontrado eliminamos e producto
        });
        return productOptional; //devolvemos los datos del producto que se elimino
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }
}
