package com.java.crud.jpa.crudconspringboot.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class RequiredValidation implements ConstraintValidator<IsRequired, String> {
    //recordemes que con esto podemos validar cualquier campo del tipo string
    //validamos que sea diferente de vacio para que retorne true si algun campo por si algun campo esta vacio retorne false
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
//        if (value != null && !value.isEmpty() && !value.isBlank()){
//            return true;
//        }
//        return false;
        //otra forma de validar seria siempre y cuando sea string
        return StringUtils.hasText(value);
    }
}
