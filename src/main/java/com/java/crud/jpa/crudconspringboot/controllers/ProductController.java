package com.java.crud.jpa.crudconspringboot.controllers;

import com.java.crud.jpa.crudconspringboot.validation.ProductValidation;
import com.java.crud.jpa.crudconspringboot.entities.Product;
import com.java.crud.jpa.crudconspringboot.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductValidation productValidation;

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
    public ResponseEntity<?> create(@Valid @RequestBody Product product, BindingResult result){  //con un responseEntity de producto y dentro del cuerpo con requestBody para mandar el json de producto
        //una forma de calidar con una clase personalizada
        //productValidation.validate(product, result);  //se valida los campos con nuestra calse personalizada le pasmaos el objeto y el error
        if(result.hasFieldErrors()){
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));  //se lo pasamos al status created y desntro del cuerpo guardamos el producto en la bd
    }


    //el blindingresult se ocupara para personalizar los mensaje de error para la validacionde campos debe ir siempre despues del objeto a validar
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Product product,BindingResult result, @PathVariable Long id){  //primero pasamos por pathVariabel el id que se actualizara y despues con requesBody(json) pasamos el cuerpo del obejto producto que se modificaran
        //una forma de calidar con una clase personalizada
        //productValidation.validate(product, result);  //se valida los campos con nuestra calse personalizada le pasmaos el objeto y el error
        if(result.hasFieldErrors()){
            return validation(result);
        }
        Optional<Product> productOptional = productService.update(id, product); //creamos un optional de producto y le pasamos con el productoservice los datos del path y del request
        if(productOptional.isPresent()){   //validamos que exista con un if y pasadole el optional
            return ResponseEntity.status(HttpStatus.CREATED).body(productOptional.orElseThrow());   //ya que se valido que exista se pasa el optional para devolver la respuesta la actulizacion en json
        }
        return ResponseEntity.notFound().build();  // si no lo encuentra evuelve un 404 not found
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){  //se coloca como responseentity ya que puede que nos regrese un ok o un 404 notfound
        Optional<Product> productOptional =  productService.delete(id);  //le pasamos con un optional el producto nuevo al que se le asigno el id
        if (productOptional.isPresent()){  //validamos con un if que este presente
            return ResponseEntity.ok(productOptional.orElseThrow());   //en caso de que este presente pasamos retornamos un ok al producto eliminado con exito
        }
        return ResponseEntity.notFound().build();  //en caso no que no exista retornamos un notfound
    }

    //creamos un metodo privado para la validacion
    private ResponseEntity<?> validation(BindingResult result) {
        //creamos un map en el cual se guardaran en la variable los mensajes de error de cada campo que se obtienen atraves del objeto result el primer string es para la key (nombre del campo a validar) y el segundo es para em mensaje de error
        Map<String, String> errors = new HashMap<>();
        //primero obtenemos los campos con get field en forma de lista, despues conb un foreach lo recorremos, y con un exprecion lamnda y put le pasamos los errores en forma de string primero la key (el campo) y despues concatenando le pasamos el msj de error
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        //le pasamos al bad_request que es el status 400, dentro del cuerpo el errors personalizado
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        return ResponseEntity.badRequest().body(errors);
    }
}
