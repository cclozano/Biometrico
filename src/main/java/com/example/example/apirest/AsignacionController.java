package com.example.example.apirest;

import com.example.example.apirest.util.CustomErrorType;
import com.example.example.datos.ArticuloRepository;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Articulo;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.servicios.ServicioAsignacion;
import com.example.example.servicios.ServicioHuella;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("biometrico/v1/asignaciones")
@Api(value="biometrico/v1/asignaciones", description="Asignacion Biometrica")
public class AsignacionController {
    public static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private ServicioAsignacion servicioAsignacion;

    @Autowired
    private ServicioHuella servicioHuella;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private PersonaRepository personaRepository;


    @ApiOperation(value = "Buscar Persona con la huella",response = Persona.class)
    @RequestMapping(value = "/buscar", method = RequestMethod.POST)
    public ResponseEntity<?> verificar(@RequestBody Huella huella) {
        logger.info("Buscando Persona por Huella {}", huella.getRegMin());
        Persona persona = null;
        try {
            persona = servicioHuella.encontrarPersona(huella);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al buscar por huella"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (persona == null) {
            logger.error("Persona con la huella {} no se encontro", huella);
            return new ResponseEntity(new CustomErrorType("Persona con la huella " + huella
                    + " no se encontro"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Persona>(persona, HttpStatus.OK);
    }


    @ApiOperation(value = "Asignar articulo a una persona",response = Persona.class)
    @RequestMapping(value = "/asignar", method = RequestMethod.POST)
    public ResponseEntity<?> asignar(@PathVariable(value = "codigo") String codigo,@RequestBody Huella huella) {
        logger.info("asignando articulo {} ", codigo);
        Articulo articulo = articuloRepository.findByCodigo(codigo);
        Persona persona = null;
        if(articulo!= null)
        {
            try {
                persona = servicioHuella.encontrarPersona(huella);
                servicioAsignacion.asignar(articulo,persona);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return new ResponseEntity(new CustomErrorType("Error al buscar por huella"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Persona>(persona, HttpStatus.OK);
    }



    @ApiOperation(value = "Asignar Huella a una Persona",response = Persona.class)
    @RequestMapping(value = "/nuevahuella", method = RequestMethod.POST)
    public ResponseEntity<?> asinarHuella(@PathVariable(value = "idPersona") String idPersona, @RequestBody Huella huella) {
        logger.info("Asignando huella {}", huella.getRegMin());
        Persona persona = null;
        try {
            persona = personaRepository.findByIdentificacion(idPersona);
            persona.agregarHuella(huella);
            personaRepository.save(persona);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al asignar la huella"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (persona == null) {
            logger.error("Persona con la huella {} no se encontro", huella);
            return new ResponseEntity(new CustomErrorType("Persona con la huella " + huella
                    + " no se encontro"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Persona>(persona, HttpStatus.OK);
    }



}
