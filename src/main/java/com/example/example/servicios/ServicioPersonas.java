package com.example.example.servicios;

import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.exceptions.ServiceException;

import java.util.List;

public interface ServicioPersonas {
    Persona agregar(Persona persona) throws ServiceException;
    Persona actualizar(Long id, Persona persona) throws ServiceException;
    void eliminar(Long id);
    List<Persona> consultarTodos();
    Persona consultar(Long id);
    Persona consultar(String id);
    Persona agregarHuella(Long id, Huella huella);
    Persona eliminarHuella(Long id, Long idHuella) throws ServiceException;

}
