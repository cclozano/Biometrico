package com.example.example.apirest;

import com.example.example.apirest.util.CustomErrorType;
import com.example.example.dominio.Usuario;
import com.example.example.servicios.ServicioUsuario;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@Api(value="biometrico", description="Login de usuario")
public class UsuarioController {
    public static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    ServicioUsuario userService;



    // -------------------Retrieve Single Usuario------------------------------------------

    @ApiOperation(value = "Search a user with an ID",response = Usuario.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUsuario(@PathVariable("id") long id) {
        logger.info("Fetching Usuario with id {}", id);
        Usuario user = userService.findById(id);
        if (user == null) {
            logger.error("Usuario with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Usuario with id " + id
                    + " not found"), HttpStatus.OK);
        }
        return new ResponseEntity<Usuario>(user, HttpStatus.OK);
    }



    



}
