package com.java.crud.jpa.crudconspringboot.repositories;

import com.java.crud.jpa.crudconspringboot.entities.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
