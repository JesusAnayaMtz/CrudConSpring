package com.java.crud.jpa.crudconspringboot.security.filter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.crud.jpa.crudconspringboot.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.java.crud.jpa.crudconspringboot.security.TokenJwtConfig.*;   //importamos esta clase para poder acceder a la configuracion

//esta es para crear el token y validarlo
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //creamos este atributo
    private AuthenticationManager authenticationManager;




    //el cual pasaremos mediante el constructor
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    //implementamos un metodo para realizar la autenticacion, este se pasa con un json y debemos capturarlo y convertirlo a un objeto
   //recibimos el request por el cual obtenemos el username y el password
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        User user = null;  //creamos una instancia de user null
        String username = null;   //creamos dos parametros que s ocuparan en el object mapper para obtener y asiganr el username y el pasword
        String password = null;


        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);  //obtendemos el user con la clase objectmapper, con readvalue lo leemos
            //y recibimos dos parametros el request que es el json lo capturamos con request.getInputStream, y luego al tipo de dato al que lo convertiremos en este caso User.class
            username = user.getUsername();   //se le pasa a los parametros creado el username con get y tambien el password
            password = user.getPassword();
        } catch (StreamReadException e) {
            e.printStackTrace();
        }catch (DatabindException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        //ya que hicimos la convecion instanciamos un UsernamePasswordAuthenticationToken y le pasamos por parametro el username y el password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        //retornamos la autenticacion con authenticationManager y le pasamos el autentication toket que contiene el username y el password
        return authenticationManager.authenticate(authenticationToken);
    }


    //este metodo es por si salio bien la autenticacion
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
       //para obtener el user name desde el autresult y get principal devuelve un objeto el cual se debe castear al tipo user de spring securty
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();

        String username = user.getUsername();   //y aqui obtenemos el user name
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();  //obtenemos los roles esto devuelve un tipo colletion de grantedautorities

        Claims claims = Jwts.claims().add("authorities", roles)
                .add("username", username)  // le agragemas tambie  el username
                .build();  //creamos un claims y le pasamos los roles

        //se generan el token pasandole el username y el secretkey como firma y compat lo genera
        String token = Jwts.builder()
                .subject(username)
                .claims(claims)   //pasamos los claims
                .expiration(new Date(System.currentTimeMillis() + 3600000))  //expiracion del token se coloca la fecha actual +  una hora en milisegundos esto significa que el token expira en una hora
                .issuedAt(new Date()) //esto es para la fecha en la que se creo el token
                .signWith(SECRET_KEY)
                .compact();

        //devolvemos el token a la vista o al cliente con reponse
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        //creamos un map para hacer un json para pasarlo a la vista
        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("username", username);
        body.put("message", String.format("Hola %s has iniciado sesion con exito", username));

        //escribirmos el json en la respuesta convertiremos el map en un  json
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);
    }

    //metodo para en caso de que la autenticacion falle
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        //creamos un map el cual le pasaremos los mensaje de error para convertirlos a un json
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticacion username o password erroneos");
        body.put("error", failed.getMessage());

        //agregamos la respuesta y convetirmos el body o el map a un json
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);   //le pasamos la resptesa en esta caso 401 que es no autorizado
        response.setContentType(CONTENT_TYPE);  //y le pasamos el contenttype
    }
}
