package com.java.crud.jpa.crudconspringboot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequiredValidation.class)  //agregamos nuestra clase al constraint
@Retention(RetentionPolicy.RUNTIME)  // se ejecuta en tiempo de ejecucion
@Target({ElementType.FIELD, ElementType.METHOD})  //un arreglo de element type para poder ejecutar en mas de n tarjet (para que se ejecute encima de un atirbitu, metodo o clase)
public @interface IsRequired {
    String message() default "es requerido usando anotaciones";   // aqui se pasa el msj que mostrara en la validacion

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
