package com.java.crud.jpa.crudconspringboot.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.crud.jpa.crudconspringboot.validation.ExistsByUsername;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ExistsByUsername //ocupamos esta anotacion personalizada para validar si existe el usuario.
    @NotBlank
    @Size(min = 5, max = 20)
    @Column(name = "user_name", unique = true)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  //con esto le damos acceso solo cuando se escribe, pero cuando lee ese campo lo oculta y asi ya no muestra la info de esecampos en un get
    private String password;

    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})  //se ocupa para que no se ciclen al consultar por la relacion many to many cuando este en ambas clases, lo que hace es ignorar los roles en la clase o excluir la contraparte del many to many
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "rol_id"})}
    )
    private List<Role> roles;

    private boolean enabled; //este es para habilita y deshabilitar al usuario por default da 1 que es habilitado

    //se crea este metodo para que al crear el usuario este se habilite
    @PrePersist
    public void prePersist(){
        enabled = true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient  //con esto le decimos a hibernate que no es un campo en la tabla de la bd solo es de la clase
    private boolean admin;  //para pasarlo en postman se coloca admin y se debe mandar en true para que asigne al usuario el rol de admin y el de user


    public User() {
        this.roles = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
