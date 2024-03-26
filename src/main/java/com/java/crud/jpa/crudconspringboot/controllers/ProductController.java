package com.java.crud.jpa.crudconspringboot.controllers;

import com.java.crud.jpa.crudconspringboot.entities.Product;
import com.java.crud.jpa.crudconspringboot.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> list(){
        return productService.findAll();
    }

    @GetMapping("/{id}")  //se anota con pathvariable por que recibiri un id
    public ResponseEntity<?> viewProduct(@PathVariable Long id){  //se coloca como responseentity ya que puede que nos regrese un ok o un 404 notfound
        Optional<Product> productOptional =  productService.findById(id);  //le pasamos con un optional el producto con el id.
        if (productOptional.isPresent()){  //validamos con un if que este presente
            return ResponseEntity.ok(productOptional.orElseThrow());   //en caso de que este prrsente reportanamos el prodcuto
        }
        return ResponseEntity.notFound().build();  //en caso no que no exista retornamos un notfound
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product){  //con un responseEntity de producto y dentro del cuerpo con requestBody para mandar el json de producto
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));  //se lo pasamos al status created y desntro del cuerpo guardamos el producto en la bd
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product){  //primero pasamos por pathVariabel el id que se actualizara y despues con requesBody(json) pasamos el cuerpo del obejto producto que se modificaran
        product.setId(id);  //le pasamos a product el id que se va actualizar
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));   //ya que tenemos el objeto producto le pasamos los nuevo datos a actualizar
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){  //se coloca como responseentity ya que puede que nos regrese un ok o un 404 notfound
        Product product = new Product();  //creamos un producto
        product.setId(id); //le seteamos el id que le dimos por pathvariable al nuevo producto
        Optional<Product> productOptional =  productService.delete(product);  //le pasamos con un optional el producto nuevo al que se le asigno el id
        if (productOptional.isPresent()){  //validamos con un if que este presente
            return ResponseEntity.ok(productOptional.orElseThrow());   //en caso de que este presente pasamos retornamos un ok al producto eliminado con exito
        }
        return ResponseEntity.notFound().build();  //en caso no que no exista retornamos un notfound
    }
}
