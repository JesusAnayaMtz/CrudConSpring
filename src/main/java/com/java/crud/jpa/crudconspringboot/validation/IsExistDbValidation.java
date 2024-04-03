package com.java.crud.jpa.crudconspringboot.validation;

import com.java.crud.jpa.crudconspringboot.repositories.ProductRepository;
import com.java.crud.jpa.crudconspringboot.services.ProductService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsExistDbValidation implements ConstraintValidator<IsExistDb, String> {

    @Autowired
    private ProductService productService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //validamos si product service es igual a null retornamos true y asi no invoque el metodo exist
        if (productService == null){
            return  true;
        }
        return !productService.existsBySku(value);  //validamos que valor del sku no exista ya en la bd
    }
}
