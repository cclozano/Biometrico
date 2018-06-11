package com.example.example.servicios;

import com.example.example.datos.HuellaRespository;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServicioPersonasImp implements ServicioPersonas {


    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    HuellaRespository huellaRespository;



    @Override
    public Persona agregar(Persona persona) throws ServiceException {
        Persona p = personaRepository.findByIdentificacion(persona.getIdentificacion());
        if (p!=null)
            throw new ServiceException("La persona ya esta registrada");
        else {
            Persona pp = personaRepository.save(persona);
            return pp;
        }
    }

    @Override
    public Persona actualizar(Long id,Persona persona) throws ServiceException {
        Persona p2 = personaRepository.findByIdentificacion(persona.getIdentificacion());
        Persona p = personaRepository.findOne(id);
        if(p==null)
            throw new ServiceException("No se encontro la persona con el id " + id);
        if(p2 != null && !p2.getIdentificacion().equals(p.getIdentificacion()))
        {
            throw new ServiceException("ya existe otra persona con la identificacion " + persona.getIdentificacion());
        }
        p.setApellidoMaterno(persona.getApellidoMaterno());
        p.setApellidoPaterno(persona.getApellidoPaterno());
        p.setFoto(persona.getFoto());
        p.setIdentificacion(persona.getIdentificacion());
        p.setNombre(persona.getNombre());
        p.setSegundoNombre(persona.getSegundoNombre());

        personaRepository.save(p);
        return persona;
    }

    @Override
    public void eliminar(Long id) {
        personaRepository.delete(id);
    }

    @Override
    public List<Persona> consultarTodos() {
        return personaRepository.findAll();
    }

    @Override
    public Persona consultar(Long id) {
        return personaRepository.findOne(id);
    }

    @Override
    public Persona consultar(String id) {
        return personaRepository.findByIdentificacion(id);
    }

    @Override
    public Persona agregarHuella(Long idPersona, Huella huella) {
        Persona persona = personaRepository.findOne(idPersona);
        persona.agregarHuella(huella);
        personaRepository.save(persona);
        return persona;
    }

    @Override
    public Persona eliminarHuella(Long id, Long idHuella) throws ServiceException {
        Persona p =  personaRepository.findOne(id);
        if (p == null) {
            throw new ServiceException("Persona con ID "+id+" no se encontro");
        }
        Huella h = p.quitarHuella(idHuella);
        if(h!=null) {
            personaRepository.save(p);
            huellaRespository.save(h);
        }
        else
            throw new ServiceException("Huella con ID "+idHuella+" no se encontro asignada a la persona");
        return  p;
    }
}
