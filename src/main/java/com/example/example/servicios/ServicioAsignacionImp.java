package com.example.example.servicios;

import com.example.example.datos.ArticuloRepository;
import com.example.example.datos.AsignacionRepository;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.*;
import com.example.example.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ServicioAsignacionImp implements ServicioAsignacion {
    @Autowired
    AsignacionRepository asignacionRepository;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    ArticuloRepository articuloRepository;

    @Transactional
    @Override
    public Asignacion asignar(String codigo, Long idPersona, String novedadesAsignacion) throws Exception {
        Articulo articulo = articuloRepository.findByCodigo(codigo);
        Persona persona = personaRepository.findOne(idPersona);
        if(articulo == null)
        {
            throw new ServiceException("No se encontro el articulo");
        }
        if(persona == null)
        {
            throw new ServiceException("No se encontro el articulo");
        }
        if (articulo.getAsignacion()!=null)
        {
            throw new ServiceException("El articulo ya se encuentra asignado a: " + articulo.getAsignacion().getPersona());
        }
        Asignacion asignacion = new Asignacion(persona,articulo);
        persona.getAsignacions().add(asignacion);
        articulo.setAsignacion(asignacion);
        asignacion.setNovedadesAsignacion(novedadesAsignacion);
        asignacionRepository.save(asignacion);
        personaRepository.save(persona);
        articuloRepository.save(articulo);
        return asignacion;
    }

    @Transactional
    @Override
    public Asignacion asignar(Articulo articulo, Persona persona) throws Exception {
        if (articulo.getAsignacion()!=null)
        {
            throw new ServiceException("El articulo ya se encuentra asignado a: " + articulo.getAsignacion().getPersona());
        }
        Asignacion asignacion = new Asignacion(persona,articulo);
        persona.getAsignacions().add(asignacion);
        articulo.setAsignacion(asignacion);
        asignacionRepository.save(asignacion);
        personaRepository.save(persona);
        articuloRepository.save(articulo);
        return asignacion;
    }

    @Transactional
    @Override
    public Asignacion desasociar(Asignacion asignacion, String novedadesAsignacion) {
        asignacion.desasociar();
        asignacion.getArticulo().setAsignacion(null);
        asignacion.setNovedadesDevolucion(novedadesAsignacion);
        asignacionRepository.save(asignacion);
        articuloRepository.save(asignacion.getArticulo());
        return asignacion;
    }
}
