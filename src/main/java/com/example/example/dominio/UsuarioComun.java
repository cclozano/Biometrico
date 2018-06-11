package com.example.example.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by max on 20/06/17.
 */
@Getter @Setter
@Entity
@Table(name = "USUARIO_COMUN")
public class UsuarioComun extends Persona implements Usuario {

    @NotNull
    @Size(min = 4)
    @Column(name = "NOMBRE_USUARIO",length = 15, unique = true)
    private String nombreUsuario = "usuario";
    @NotNull
    @Size(min = 4,max = 15)
    @Column(name = "PASSWORD",length = 25)
    private String password = "1234";
    @NotNull
    @Size(min = 4,max=50)
    @Column(name = "CORREO",length = 25)
    private String correo = "correo@dominio.com";


    @Override
    public String getNombreCompleto() {
        return getNombre() + " " + getSegundoNombre() + " " + getApellidoPaterno() + " " + getApellidoMaterno();
    }

    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.REGISTRADOR;
    }

    @JsonIgnore
    @Override
    public String getUsuario() {
        return nombreUsuario;
    }
}
