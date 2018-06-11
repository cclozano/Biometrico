package com.example.example.apirest;

import com.example.example.apirest.util.BasicOkResponse;
import com.example.example.apirest.util.CustomErrorType;
import com.example.example.datos.UsuarioRepository;
import com.example.example.dominio.Usuario;
import com.example.example.dominio.UsuarioComun;
import com.example.example.exceptions.AuthException;
import com.example.example.exceptions.ServiceException;
import com.example.example.servicios.ServicioUsuario;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("biometrico/v1/usuarios")
@Api(value="biometrico/v1/usuarios", description="Manejo de usuarios")
public class UsuarioController {
    public static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    ServicioUsuario userService;

    @Autowired
    UsuarioRepository usuarioRepository;


    @ApiOperation(value = "Ingresar nuevo registro de Usuario",response = UsuarioComun.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> ingresar(@RequestBody UsuarioComun usuarioComun) {
        logger.info("Creando Usuario {}", usuarioComun);

        UsuarioComun p;
        try {
            p = usuarioRepository.findByIdentificacion(usuarioComun.getIdentificacion());
            if (p!=null)
                throw new ServiceException("La Usuario ya esta registrado");
            else
                usuarioRepository.save(usuarioComun);
        }
        catch (ServiceException s)
        {
            logger.error(s.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al crear Usuaro:" + s.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al crear Usuario" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(p, HttpStatus.OK);
    }




    @ApiOperation(value = "Actualizar Usuario")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity modificar(@PathVariable Long id, @RequestBody UsuarioComun persona){
        UsuarioComun p = usuarioRepository.findOne(id);
        if(p == null)
        {
            return new ResponseEntity(new CustomErrorType("Error al crear Usuario"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        p.setApellidoMaterno(persona.getApellidoMaterno());
        p.setApellidoPaterno(persona.getApellidoPaterno());
        p.setFoto(persona.getFoto());
        p.setIdentificacion(persona.getIdentificacion());
        p.setNombre(persona.getNombre());
        p.setSegundoNombre(persona.getSegundoNombre());
        p.setCorreo(persona.getCorreo());
        p.setNombreUsuario(persona.getNombreUsuario());
        p.setPassword(persona.getPassword());
        usuarioRepository.save(p);
        return new ResponseEntity(new BasicOkResponse("Usuario Actualizado"), HttpStatus.OK);
    }

    @ApiOperation(value = "Eliminar Usuario")
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity delete(@PathVariable Long id){
        userService.eliminar(id);
        return new ResponseEntity(new BasicOkResponse(), HttpStatus.OK);

    }
    

    @ApiOperation(value = "Buscar Usuario por ID",response = Usuario.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUsuario(@PathVariable("id") long id) {
        logger.info("Fetching Usuario with id {}", id);
        UsuarioComun user = usuarioRepository.findOne(id);
        if (user == null) {
            logger.error("Usuario with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("usuario con id " + id
                    + " no encontrado"), HttpStatus.OK);
        }
        return new ResponseEntity<Usuario>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "Listar Usuario",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "", method= RequestMethod.GET, produces = "application/json")
    public Iterable<UsuarioComun> list(Model model){
        Iterable<UsuarioComun> UsuarioComuns = usuarioRepository.findAll();
        return UsuarioComuns;
    }

    @ApiOperation(value = "Login de Usuario",response = UsuarioComun.class)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody UsuarioComun usuarioComun) {
        logger.info("Login Usuario {}", usuarioComun);
        Usuario u;
        try {
            u = userService.autenticar(usuarioComun.getUsuario(),usuarioComun.getPassword());
            if(u==null)
            {
                logger.error("error usuario nulo");
                return new ResponseEntity(new CustomErrorType("Error al autenticar: Usuario nulo"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        catch (AuthException s)
        {
            logger.error(s.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al autenticar:" + s.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(u, HttpStatus.OK);
    }





}
