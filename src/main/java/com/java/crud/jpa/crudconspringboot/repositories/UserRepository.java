package com.java.crud.jpa.crudconspringboot.repositories;

import com.java.crud.jpa.crudconspringboot.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    //validamos en la base de datos si existe un nombre usuario
    boolean existsByUsername(String username);

    //creamos el metodo por que cual vamos a sbuacr al usuario para el details service
    Optional<User> findByUsername(String username);
}
