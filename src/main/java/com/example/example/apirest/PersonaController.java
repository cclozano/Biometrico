package com.example.example.apirest;


import com.example.example.apirest.util.BasicOkResponse;
import com.example.example.apirest.util.CustomErrorType;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.exceptions.ServiceException;
import com.example.example.servicios.ServicioPersonas;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("biometrico/v1/personas")
@Api(value="biometrico/v1/personas", description="Asignacion Biometrica")
public class PersonaController {

    public static final Logger logger = LoggerFactory.getLogger(PersonaController.class);


    @Autowired
    private ServicioPersonas servicioPersonas;

    @ApiOperation(value = "Ingresar nuevo registro de persona",response = Persona.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> ingresar(@RequestBody Persona persona) {
        logger.info("Creando Persona {}", persona);

        try {
            servicioPersonas.agregar(persona);
        }
        catch (ServiceException s)
        {
            logger.error(s.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al actualizar Persona:" + s.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al actualizar Persona"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(persona, HttpStatus.OK);
    }


    @ApiOperation(value = "Actualizar Persona")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody Persona persona){
        Persona p;
        try {
            p=servicioPersonas.actualizar(id,persona);
        }
        catch (ServiceException s)
        {
            logger.error(s.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al actualizar:" + s.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(p, HttpStatus.OK);
    }

    @ApiOperation(value = "Eliminar Persona")
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable Long id){
        servicioPersonas.eliminar(id);
        return new ResponseEntity(new BasicOkResponse(), HttpStatus.OK);

    }





    @ApiOperation(value = "Listar Personas",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "", method= RequestMethod.GET, produces = "application/json")
    public Iterable<Persona> list(Model model){
        Iterable<Persona> personas = servicioPersonas.consultarTodos();
        return personas;
    }


    @ApiOperation(value = "Buscar Persona por ID",response = Persona.class)
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public Persona buscar(@PathVariable Long id, Model model){
        Persona product = servicioPersonas.consultar(id);
        return product;
    }

    @ApiOperation(value = "Buscar Persona por identificacion",response = Persona.class)
    @RequestMapping(value = "/consultar/{identificacion}", method= RequestMethod.GET, produces = "application/json")
    public Persona buscarPorCedula(@PathVariable String identificacion, Model model){
        Persona product =
                servicioPersonas.consultar(identificacion);
        return product;
    }


    @ApiOperation(value = "Asignar Huella a una Persona",response = Persona.class)
    @RequestMapping(value = "/{idPersona}", method = RequestMethod.POST)
    public ResponseEntity<?> asinarHuella(@PathVariable(value = "idPersona") Long idPersona, @RequestBody Huella huella) {
        logger.info("Asignando huella {}", huella.getRegMin());
        Persona p = null;
        try {
            p = servicioPersonas.agregarHuella(idPersona,huella);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al asignar huella"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (p == null) {
            logger.error("Persona con la huella {} no se encontro", huella);
            return new ResponseEntity(new CustomErrorType("Persona con la huella " + huella
                    + " no se encontro"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Persona>(p, HttpStatus.OK);
    }


    @ApiOperation(value = "Quitar Huella a una Persona",response = Persona.class)
    @RequestMapping(value = "/{idPersona}/huellas/{idHuella}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<?> quitarHuella(@PathVariable(value = "idPersona") Long idPersona, @PathVariable(value = "idHuella") Long idHuella) {
        logger.info("Quitando huella {}", idHuella);
        Persona p = null;
        try {
            p = servicioPersonas.eliminarHuella(idPersona,idHuella);
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al quitar huella"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Persona>(p, HttpStatus.OK);
    }

    @ApiOperation(value = "Consultar Huellas de una Persona",response = Persona.class)
    @RequestMapping(value = "/{idPersona}/huellas", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> consultarHuellas(@PathVariable(value = "idPersona") Long idPersona) {
        logger.info("consultando huellas {}", idPersona);
        Persona p = null;
        try {
            p = servicioPersonas.consultar(idPersona);
            if (p!=null) return new ResponseEntity<>(p.getHuellas(), HttpStatus.OK);
            else return new ResponseEntity(new CustomErrorType("No se encontro la persona"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al consultar huellas"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Consultar Huella de una Persona",response = Persona.class)
    @RequestMapping(value = "/{idPersona}/huellas/{idHuella}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> consultarHuella(@PathVariable(value = "idPersona") Long idPersona,@PathVariable(value = "idHuella") Long idHuella) {
        logger.info("consultando huellas {}", idPersona);
        Persona p = null;
        try {
            p = servicioPersonas.consultar(idPersona);
            if (p!=null) {
                for (Huella h :
                        p.getHuellas()) {
                    if (h.getId() == idHuella) return new ResponseEntity<>(h, HttpStatus.OK);
                }
            }
            else return new ResponseEntity(new CustomErrorType("No se encontro la persona"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al consultar huellas"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(new CustomErrorType("No se encontro"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value = "Consultar Huellas de una Persona",response = Persona.class)
    @RequestMapping(value = "/{idPersona}/asignaciones", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> consultarAsignaciones(@PathVariable(value = "idPersona") Long idPersona) {
        logger.info("consultando asignaciones {}", idPersona);
        Persona p = null;
        try {
            p = servicioPersonas.consultar(idPersona);
            if (p!=null) return new ResponseEntity<>(p.getAsignacions(), HttpStatus.OK);
            else return new ResponseEntity(new CustomErrorType("No se encontro la persona"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al quitar huella"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
