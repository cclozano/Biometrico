package com.example.example.servicios;

import com.example.example.datos.ArticuloRepository;
import com.example.example.datos.AsignacionRepository;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServicioAsignacionImp implements ServicioAsignacion {
    @Autowired
    AsignacionRepository asignacionRepository;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    ArticuloRepository articuloRepository;

    @Override
    public Asignacion asignar(Articulo articulo, Persona persona) throws Exception {
        if (articulo.getAsignacion()!=null)
        {
            throw new com.example.example.servicios.ServiceException("El articulo ya se encuentra asignado a: " + articulo.getAsignacion().getPersona());
        }
        Asignacion asignacion = new Asignacion(persona,articulo);
        persona.getAsignacions().add(asignacion);
        articulo.setAsignacion(asignacion);
        asignacionRepository.save(asignacion);
        personaRepository.save(persona);
        articuloRepository.save(articulo);
        return asignacion;
    }

    @Override
    public void desasociar(Asignacion asignacion) {
        asignacion.desasociar();
        asignacion.getArticulo().setAsignacion(null);
        asignacionRepository.save(asignacion);
        articuloRepository.save(asignacion.getArticulo());
    }
}
