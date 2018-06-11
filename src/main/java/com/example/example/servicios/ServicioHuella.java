package com.example.example.servicios;

import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;

public interface ServicioHuella {
    Persona encontrarPersona(Huella huella) throws Exception;
    boolean compararHuellas(byte[] h1, byte[] h2);
}
