package com.example.example.dominio;

/**
 * Created by max on 20/06/17.
 */
public interface Usuario {
    String getNombreCompleto();
    TipoUsuario getTipoUsuario();
    String getUsuario();
    String getIdentificacion();


    enum TipoUsuario
    {
        ADMINISTRADOR,
        REGISTRADOR,
    }
}
