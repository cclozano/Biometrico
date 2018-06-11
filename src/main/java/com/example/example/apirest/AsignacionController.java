package com.example.example.apirest;

import com.example.example.apirest.util.AsignacionRequest;
import com.example.example.apirest.util.CustomErrorType;
import com.example.example.apirest.util.ReciboResponse;
import com.example.example.datos.ArticuloRepository;
import com.example.example.datos.AsignacionRepository;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Articulo;
import com.example.example.dominio.Asignacion;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.servicios.ServicioAsignacion;
import com.example.example.servicios.ServicioHuella;
import com.example.example.util.jasper.Formato;
import com.example.example.util.jasper.ReportsUtil;
import com.vaadin.server.StreamResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

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

    @Autowired
    private AsignacionRepository asignacionRepository;


    @Autowired
    ReportsUtil reportsUtil;


    @ApiOperation(value = "Buscar Persona con la huella",response = Persona.class)
    @RequestMapping(value = "/buscar", method = RequestMethod.POST)
    public ResponseEntity<?> verificar(@RequestBody Huella huella) {
        logger.info("Buscando Persona por Huella {}", huella.getImageJpg());
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
    public ResponseEntity<?> asignar(@RequestBody AsignacionRequest asg) {
        logger.info("asignando articulo {} ", asg);
        if(asg.getCodigoArticulo()==null)
        {
            return new ResponseEntity(new CustomErrorType("codigo articulo nulo" ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(asg.getIdPersona()==null)
        {
            return new ResponseEntity(new CustomErrorType("id persona incorrecta" ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(asg.getNovedad()==null)
        {
            return new ResponseEntity(new CustomErrorType("Debe enviar la novedad" ), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Asignacion asignacion= null;
            try {
                asignacion = servicioAsignacion.asignar(asg.getCodigoArticulo(),asg.getIdPersona(),asg.getNovedad());
            } catch (Exception e) {
                logger.error(e.getMessage());
                return new ResponseEntity(new CustomErrorType("Error al asignar" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        return new ResponseEntity<>(asignacion, HttpStatus.OK);
    }

    @ApiOperation(value = "Desasociar articulo de una persona",response = Persona.class)
    @RequestMapping(value = "/asignar", method = RequestMethod.DELETE)
    public ResponseEntity<?> desasociar(@RequestBody AsignacionRequest asg) {
        logger.info("asignando articulo {} ", asg);
        if(asg.getCodigoArticulo()==null)
        {
            return new ResponseEntity(new CustomErrorType("codigo articulo nulo" ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(asg.getIdPersona()==null)
        {
            return new ResponseEntity(new CustomErrorType("id persona incorrecta" ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(asg.getNovedad()==null)
        {
            return new ResponseEntity(new CustomErrorType("Debe enviar la novedad" ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Articulo articulo = articuloRepository.findByCodigo(asg.getCodigoArticulo());
        if(articulo == null)
        {
            logger.error("No se encontro el articulo con codigo " + asg.getCodigoArticulo());
            return new ResponseEntity(new CustomErrorType("No se encontro el con codigo " + asg.getCodigoArticulo()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(articulo.getAsignacion()== null)
        {
            logger.error("El articulo no esta asignado:  " + asg.getCodigoArticulo());
            return new ResponseEntity(new CustomErrorType("El articulo no esta asignado: " + asg.getCodigoArticulo()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Persona persona = null;

        Asignacion asignacion = null;
        if(articulo!= null)
        {
            try {
                persona = personaRepository.findOne(asg.getIdPersona());
                if(persona == null)
                {
                    logger.error("No se encontro la persona ");
                    return new ResponseEntity(new CustomErrorType("No se encontro el la persona"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if(persona.getId().equals(articulo.getAsignacion().getPersona().getId()))
                {
                    asignacion = servicioAsignacion.desasociar(articulo.getAsignacion(),asg.getNovedad());
                }
                else
                    return new ResponseEntity(new CustomErrorType("El articulo esta asignado a otra persona"), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return new ResponseEntity(new CustomErrorType("Error al buscar por huella"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(asignacion, HttpStatus.OK);
    }



    @ApiOperation(value = "Consultar Historial de Asignaciones",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "/{idPersona}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> list(@PathVariable Long idPersona){
        Persona persona = personaRepository.findOne(idPersona);
        if(persona == null) {
            logger.error("Persona con el ID {} no se encontro", idPersona);
            return new ResponseEntity(new CustomErrorType("Persona el ID " + idPersona +" no se encontro"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Collection>(persona.getAsignacions(), HttpStatus.OK);
    }



    @RequestMapping(value = "reciboasignar/{idAsignacion}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> reciboAsignar(@PathVariable Long idAsignacion){
        Asignacion asig = asignacionRepository.findOne(idAsignacion);
        if(asig == null) {
            logger.error("Asignacion con el ID {} no se encontro", idAsignacion);
            return new ResponseEntity(new CustomErrorType("Asignacion con el ID " + idAsignacion +" no se encontro"), HttpStatus.NOT_FOUND);
        }
        byte[] result = reportsUtil.getBytesPdf("comprobanteAsignacion",Formato.PDF,Arrays.asList(asig,asig), new HashMap());
        String base64EncodedData =      Base64.encodeBase64URLSafeString(result);
        ReciboResponse response = new ReciboResponse();
        response.setBytesFile(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "recibodevolver/{idAsignacion}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> reciboDevolver(@PathVariable Long idAsignacion){
        Asignacion asig = asignacionRepository.findOne(idAsignacion);
        if(asig == null) {
            logger.error("Asignacion con el ID {} no se encontro", idAsignacion);
            return new ResponseEntity(new CustomErrorType("Asignacion con el ID " + idAsignacion +" no se encontro"), HttpStatus.NOT_FOUND);
        }
        byte[] result = reportsUtil.getBytesPdf("comprobanteDevolucion",Formato.PDF,Arrays.asList(asig,asig), new HashMap());
        ReciboResponse response = new ReciboResponse();
        response.setBytesFile(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
