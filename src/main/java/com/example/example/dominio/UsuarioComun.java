package com.example.example.dominio;

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
public class UsuarioComun extends EntidadBase implements Usuario {

    @NotNull
    @Size(min = 4)
    @Column(name = "NOMBRE_USUARIO",length = 15, unique = true)
    private String nombreUsuario = "usuario";
    @NotNull
    @Size(min = 10, max = 15)
    @Column(name = "IDENTIFICACION",length = 15,unique = true)
    private String identificacion = "9999999999";
    @NotNull
    @Size(min = 4,max = 15)
    @Column(name = "PASSWORD",length = 25)
    private String password = "1234";
    @NotNull
    @Size(min = 4,max=50)
    @Column(name = "CORREO",length = 25)
    private String correo = "correo@dominio.com";
    @NotNull
    @Size(min = 4,max = 50)
    @Column(name = "NOMBRE_COMPLETO",length =50)
    private String nombreCompleto = "Nombre Completo";


    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.REGISTRADOR;
    }

    @Override
    public String getUsuario() {
        return nombreUsuario;
    }
}
