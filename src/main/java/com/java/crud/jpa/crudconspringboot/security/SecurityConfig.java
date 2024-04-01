package com.java.crud.jpa.crudconspringboot.security;

import com.java.crud.jpa.crudconspringboot.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //atributo de spring para la configuracion
    @Autowired
    private AuthenticationConfiguration autheticationConfiguration;

    //creamos un metodo genera y obtiene el authenticationmanager
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return autheticationConfiguration.getAuthenticationManager();
    }

    @Bean  // con bean se anota para que sea un componene de spring
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();  // se ocupa para pode codificar el password
    }

    //se crea un metodo del tipo securityfilterchain, se debe anotar con bean y se le debe inyectar un obejto del tipo httpsecurity
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.GET,"/api/users").permitAll()  //permitir a todos poder ver los usuarios se anota que solo get
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()  //aqui le decimos que el metodo post de regiter se accesible a todos
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))  //añadimos el filtro al cual le pasamos el authentication manager
                .csrf(config -> config.disable())   //se debe habilitar cuando se ocupa para vistas si es apirest se debe deshabilitar
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  //se coloca stateless ya que la sesion de va enviar en el token
                .build();  //cualquier otro endpoint debe autenticarse
    }
}
