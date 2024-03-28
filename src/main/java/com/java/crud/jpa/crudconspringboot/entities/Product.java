package com.java.crud.jpa.crudconspringboot.entities;

import com.java.crud.jpa.crudconspringboot.validation.IsExistDb;
import com.java.crud.jpa.crudconspringboot.validation.IsRequired;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank(message = "{NotBlank.product.name}")  // se ocupa para string  se puede usar notBlack ya que valida aparte de no este vacio y que no tenga espacios en blanco
    @IsRequired(message = "{IsRequired.product.name}")
    @Size(min = 4, max = 25) // se utiliza para string para el min y max de caracteres
    private String name;
    @NotNull(message = "{NotNull.product.price}")   // solo se ocupa para enteros o fechas u objetos
    @Min(value = 20, message = "{Min.product.price}")  //se ocupa para que tenga un valor minimo solo en enteros o este tipo de datos numericos
    private Integer price;
    @IsRequired  //aqui ocupamos nuestra clase personalizada
    private String description;

    @IsRequired
    @IsExistDb
    private String sku;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
