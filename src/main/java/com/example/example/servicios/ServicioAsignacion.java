package com.example.example.servicios;

import com.example.example.dominio.Articulo;
import com.example.example.dominio.Asignacion;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;

public interface ServicioAsignacion {
    Asignacion asignar(String codigo, Long idPersona, String novedadesAsignacion) throws Exception;
    Asignacion asignar(Articulo articulo, Persona persona) throws Exception;
    Asignacion desasociar(Asignacion asignacion, String novedadesAsignacion);
}
