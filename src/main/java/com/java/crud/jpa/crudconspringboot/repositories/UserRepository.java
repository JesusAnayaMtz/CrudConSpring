package com.java.crud.jpa.crudconspringboot.repositories;

import com.java.crud.jpa.crudconspringboot.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
