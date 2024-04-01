package com.java.crud.jpa.crudconspringboot.services;

import com.java.crud.jpa.crudconspringboot.entities.Role;
import com.java.crud.jpa.crudconspringboot.entities.User;
import com.java.crud.jpa.crudconspringboot.repositories.RoleRepository;
import com.java.crud.jpa.crudconspringboot.repositories.UserRepository;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER"); //creamos un optional para buscar el role
        List<Role> roles = new ArrayList<>();  //creamos una lista con los roles

        optionalRoleUser.ifPresent(role -> roles.add(role));  //con el if presente preguntamos si existe y si existe se agrega a la lista
        if (user.isAdmin()){  //validamos si en el json pasamos en true que es admin
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");  //con el optional se busca el admin
            optionalRoleAdmin.ifPresent(role -> roles.add(role));  // si existe le pasamos a la lista de roles el rol admin
        }

        user.setRoles(roles);  // con set le pasamos la lista de roles al usuario creado
        user.setPassword(passwordEncoder.encode(user.getPassword()));  //pasamos con set el password al usuario pero ya codificado con password enconder
        return userRepository.save(user);  //salvamos el usuario
    }
}
