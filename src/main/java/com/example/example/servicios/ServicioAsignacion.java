package com.example.example.servicios;

import com.example.example.dominio.Articulo;
import com.example.example.dominio.Asignacion;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;

public interface ServicioAsignacion {
    Asignacion asignar(Articulo articulo, Persona persona) throws Exception;
    void desasociar(Asignacion asignacion);
}
