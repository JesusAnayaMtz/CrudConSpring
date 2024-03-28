package com.java.crud.jpa.crudconspringboot.validation;

import com.java.crud.jpa.crudconspringboot.entities.Product;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Annotation;

@Component
public class ProductValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;  //casteamos el targen a tipo product
        //con validationUntils y reject validamos que no este vacio
        // y que no tenga espacios en blanco, se le pasa el errors, el campo, y el codigo del error que lo estamos obteniendo del properties
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name",null, "es requerido");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotBlank.product.description");
        //otra forma seria validando con un if, validamos que la descripcion sea igual a null o product description is blank
        if(product.getDescription() == null || product.getDescription().isBlank()){
            errors.rejectValue("description",null, "no puede estar vacio");  // una ves validados se pasa el campo y el cordigo de error se imprimen
        }
        if (product.getPrice() == null){
            errors.rejectValue("price",null, "no puede ser nulo");
        } else if (product.getPrice() < 20) {
            errors.rejectValue("price",null, "valor minimo mayor o igual a 20");
        }
    }
}
