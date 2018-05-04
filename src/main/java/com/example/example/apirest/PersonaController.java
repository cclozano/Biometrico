package com.example.example.apirest;


import com.example.example.apirest.util.CustomErrorType;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Persona;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    private PersonaRepository personaRepository;

    @ApiOperation(value = "Ingresar nuevo registro de persona",response = Persona.class)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> ingresar(@RequestBody Persona persona) {
        logger.info("Creando Persona {}", persona);

        try {
            Persona p = personaRepository.findByIdentificacion(persona.getIdentificacion());
            if(p==null)personaRepository.save(persona);
            else {
                logger.error("Ya existe la Persona con la identificacion: " + persona.getIdentificacion());
                return new ResponseEntity(new CustomErrorType("Ya existe la Persona con esa identificacion"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al crear Persona"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Persona>(persona, HttpStatus.OK);
    }

    @ApiOperation(value = "Listar Personas",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "/", method= RequestMethod.GET, produces = "application/json")
    public Iterable<Persona> list(Model model){
        Iterable<Persona> personas = personaRepository.findAll();
        return personas;
    }


    @ApiOperation(value = "Buscar Persona por ID",response = Persona.class)
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public Persona showProduct(@PathVariable Long id, Model model){
        Persona product = personaRepository.findOne(id);
        return product;
    }
}
