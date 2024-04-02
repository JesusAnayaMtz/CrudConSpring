package com.java.crud.jpa.crudconspringboot.security;

import com.java.crud.jpa.crudconspringboot.security.filter.JwtAuthenticationFilter;
import com.java.crud.jpa.crudconspringboot.security.filter.JwtValidationFilter;
import jakarta.servlet.FilterRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity   //se debe poner esta anotacion cuando se ocupen anotaciones para los roles en os ednpoints
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
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll() //aqui le decimos que el metodo post de regiter se accesible a todos
//                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN") //solo usuarios con rol admin pueden crear otros usuarios con rol admin
//                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasRole("ADMIN")  //solo admin puede modificar productos
//                        .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasRole("ADMIN") //lo mismo para eliminar solo los admin
//                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/{id}").hasAnyRole("ADMIN", "USER") //ambos roles pueden consultar productos
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))  //añadimos el filtro al cual le pasamos el authentication manager
                .addFilter(new JwtValidationFilter(authenticationManager())) //agregamos otro filtro
                .csrf(config -> config.disable())   //se debe habilitar cuando se ocupa para vistas si es apirest se debe deshabilitar
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  //se coloca stateless ya que la sesion de va enviar en el token
                .build();  //cualquier otro endpoint debe autenticarse
    }

    //se configura el cors mediante un metodo desdtro de la clase scurity
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("*"));  //aqui se configura las rutas o urls permitidas en este caso tiene que recibir una lista por eso dentro se le pasa un arreglo
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));   //acceso a los metodos
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));  //acceso a las cabezeras para jwt
        corsConfiguration.setAllowCredentials(true);

        //creamos una instancia de url que seria ya la imprementacion
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/+*", corsConfiguration);  //le pasamos la ruta donde se aplicara en esta caso a todos y le pasamos la configuracion
        return source;  //retornamos el source
    }

    //ahora le pasaremos un filtro de spring se ejecute siempre en a ruta y le pasamos dentro del cors la configuracion creada de corsconfiguration
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter(){
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}
