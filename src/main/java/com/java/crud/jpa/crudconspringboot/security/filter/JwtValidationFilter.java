package com.java.crud.jpa.crudconspringboot.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.crud.jpa.crudconspringboot.security.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.java.crud.jpa.crudconspringboot.security.TokenJwtConfig.*;

public class JwtValidationFilter extends BasicAuthenticationFilter {
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //obtenemos el token por el header
        String header = request.getHeader(HEADER_AUTHORIZATION);

        //validamos con un if que la cabezera sea distinto a null o que no contenga la palabra bearer o inicie con bearer
        if(header == null || !header.startsWith(PREFIX_TOKEN)){
            chain.doFilter(request, response);
            return;
        }
        //nos interesa solo el token por eso remplazamos el bearer por datos vacios
        String token = header.replace(PREFIX_TOKEN,"");

        //empezamos a validar los claims pasamos la llave secreta contruimos y le pasamos el token  y con eso obtenemos el username roles etc
        try {
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
            String username = claims.getSubject();  //obtenemos el user name
           // String username2 = (String) claims.get("username"); segunda opcion
            Object authoritiesClaims = claims.get("authorities");  //capturamos los roles

            //procesamos los roles con collection, sera igual a un nuevo objectmapper leemos el valor los claims el cual devuelve un arreglo de roles el cual lo convertimos a un arraylista que es el que acepta colletions
            //para que se convierta en un tipo grandten authorties y le decimos en el mixin viene con el nombre authorities y lo pasamos con es ocnstructo como role
            Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class) //pasamos primero el autoriti de spring y despues el nuestro personalizado
                    .readValue(authoritiesClaims.toString()
                            .getBytes(), SimpleGrantedAuthority[].class));



            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null,authorities);  //validamos el username y los roles
            //nos devemos autenticas se ocupa lo siguiente
            SecurityContextHolder .getContext().setAuthentication(authenticationToken);
            //continuamos con la cadena filtro que es el chain del metodo
            chain.doFilter(request, response);
        }catch (JwtException e){  //manejamos los error en caso de alguno en este caso el token expire
            Map<String, String> body = new HashMap<>(); //creamos un map
            body.put("error", e.getMessage());  //pasamos el error
            body.put("message", "el token jwt es invalido");  //pasamos el msj del error al map
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));  //lo convertimos a un json
            response.setStatus(401);  //el error estatus que no esta autrizado
            response.setContentType(CONTENT_TYPE);  //y el content type

        }

    }

}
