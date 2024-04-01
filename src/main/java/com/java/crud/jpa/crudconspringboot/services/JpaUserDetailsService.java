package com.java.crud.jpa.crudconspringboot.services;

import com.java.crud.jpa.crudconspringboot.entities.User;
import com.java.crud.jpa.crudconspringboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//esta clase se ocupa para buscar el usuario en bd, en otro microservicio etc por el username cuando relizar el login. se debe anotar con service

@Service
public class JpaUserDetailsService implements UserDetailsService {

    //inyectamos el componente que nos permita obtener el user name
    @Autowired
    private UserRepository userRepository;

    //este metodo lo que hace es ir a buscar el usuario por username y cargarlo se debe buscar y convertirle a user details
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()){  //validamos si no existe (es vacio) el usuario mandamos una exceptio con el mensaje de que el usuario no existe
            //
            throw  new UsernameNotFoundException(String.format("username %s no existe en el sistema!", username));  //lanzamos al exception
        }

        User user = userOptional.orElseThrow();  //si existe obtenemos el usuario

        //si existe el usuario se hace el siguiente proceso
        //devolvemos una lista con los roles ya que userdetails ocupa la lista del tipo granteauthority y la devemos convertir a ese tipo de lista con stream
        List<GrantedAuthority> authoritiesRoles = user.getRoles().stream()   //obtenemos los roles y con stream lo ocupamos para poder convertir a lista de grantedautoriti
                .map(role -> new SimpleGrantedAuthority(role.getName()))//con map se le pasa el role con una expresion lanmnda y creamos una instancia de grantedautoritie y obtenemos el nombre del rol
                .collect(Collectors.toList());  //con collect y con la calse colletctor convertiimos a lista ya que map es un stream

        //retornamos el user pero de springsecurity se importa y dentro de el se pasa el username, el password, y el estado, se le mandan
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authoritiesRoles);
    }
}
