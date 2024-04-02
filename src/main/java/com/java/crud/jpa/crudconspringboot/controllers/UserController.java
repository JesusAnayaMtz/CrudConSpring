package com.java.crud.jpa.crudconspringboot.controllers;

import com.java.crud.jpa.crudconspringboot.entities.User;
import com.java.crud.jpa.crudconspringboot.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:4200") /// para configurar que el front pueda acceder al back se pueden poner un url, una ip, o varias rutas
@CrossOrigin(originPatterns = "*") //al colocarlo asi le permitimos a cualqioer dominio, ip. local para que pueda acceder para la comunicacion del front
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> list(){  //creamos un metodo que nos devolvera una lista del tipo get
        return userService.findAll();   //con el service devolvemos con el findall
    }

    //create se ocupara cuando se cree un usuario y pueda ser admin
    @PreAuthorize("hasRole('ADMIN')")  //OTRA FORMA DE RESTIRNGIR LOS ENDPOINTS CON ANOTACIONES
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user,BindingResult result){  //creamos un metodo del tipo post del tipo respnse entity d user, y dentro del cuerpo le pasamos el user en json
        //una forma de calidar con una clase personalizada
        //productValidation.validate(product, result);  //se valida los campos con nuestra calse personalizada le pasmaos el objeto y el error
        if(result.hasFieldErrors()){
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));  //retornamos el status de http y en el cuerpo regresamos el usuarios guardado
    }

    //register se ocupara para crear un usuario normal el cual tendra cualquier persona acceso ya que no se le dara el rol de admin
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user,BindingResult result){  //creamos un metodo del tipo post del tipo respnse entity d user, y dentro del cuerpo le pasamos el user en json
        user.setAdmin(false);  // al user le pasamos en false campo admin
        return create(user, result);  //reutilizamos el create pero le pasamor el user creado sin admin y el result para validar los campos
    }

    //creamos un metodo privado para la validacion
    private ResponseEntity<?> validation(BindingResult result) {
        //creamos un map en el cual se guardaran en la variable los mensajes de error de cada campo que se obtienen atraves del objeto result el primer string es para la key (nombre del campo a validar) y el segundo es para em mensaje de error
        Map<String, String> errors = new HashMap<>();
        //primero obtenemos los campos con get field en forma de lista, despues conb un foreach lo recorremos, y con un exprecion lamnda y put le pasamos los errores en forma de string primero la key (el campo) y despues concatenando le pasamos el msj de error
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        //le pasamos al bad_request que es el status 400, dentro del cuerpo el errors personalizado
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        return ResponseEntity.badRequest().body(errors);
    }
}
