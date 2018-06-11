package com.example.example.apirest;

import com.example.example.apirest.util.BasicOkResponse;
import com.example.example.apirest.util.CustomErrorType;
import com.example.example.datos.ArticuloRepository;
import com.example.example.dominio.Articulo;
import com.example.example.exceptions.ServiceException;
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
@RequestMapping("biometrico/v1/articulos")
@Api(value="biometrico/v1/articulos", description="CRUD Articulos")
public class ArticuloController {


    public static final Logger logger = LoggerFactory.getLogger(ArticuloController.class);

    @Autowired
    ArticuloRepository articuloRepository;


    @ApiOperation(value = "Ingresar nuevo registro de Articulo",response = Articulo.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> ingresar(@RequestBody Articulo articulo) {
        logger.info("Creando Articulo {}", articulo);

        try {
            Articulo p = articuloRepository.findByCodigo(articulo.getCodigo());
            if (p!=null)
                throw new ServiceException("El Articulo ya esta registrado");
            else
                articuloRepository.save(articulo);
        }
        catch (ServiceException s)
        {
            logger.error(s.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al crear Articulo:" + s.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(new CustomErrorType("Error al crear Articulo"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(articulo, HttpStatus.OK);
    }


    @ApiOperation(value = "Listar Articulos",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "", method= RequestMethod.GET, produces = "application/json")
    public Iterable<Articulo> list(Model model){
        Iterable<Articulo> articulos = articuloRepository.findAll();
        return articulos;
    }
   

    @ApiOperation(value = "Buscar Articulo por ID",response = Articulo.class)
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public Articulo showProduct(@PathVariable Long id, Model model){
        Articulo articulo = articuloRepository.findOne(id);
        return articulo;
    }

    @ApiOperation(value = "Buscar Articulo por codigo",response = Articulo.class)
    @RequestMapping(value = "/buscar/{codigo}", method= RequestMethod.GET, produces = "application/json")
    public Articulo buscar(@PathVariable String codigo, Model model){
        Articulo articulo = articuloRepository.findByCodigo(codigo);
        return articulo;
    }


    @ApiOperation(value = "Actualizar Articulo")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody Articulo articulo){
        Articulo p = articuloRepository.findOne(id);
        Articulo p2 = articuloRepository.findByCodigo(articulo.getCodigo());
        if(p == null)
        {
            return new ResponseEntity(new CustomErrorType("No se encontro el Articulo"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(p2 != null && !p2.getCodigo().equals(p.getCodigo()))
        {
            return new ResponseEntity(new CustomErrorType("ya existe otro articulo con la identificacion " + articulo.getCodigo()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        p.setAsignacion(articulo.getAsignacion());
        p.setCodigo(articulo.getCodigo());
        p.setDescripcion(articulo.getDescripcion());
        p.setFabricante(articulo.getFabricante());
        p.setModelo(articulo.getModelo());
        p.setNumeroSerie(articulo.getNumeroSerie());
        articuloRepository.save(p);
        return new ResponseEntity(p, HttpStatus.OK);
    }

    @ApiOperation(value = "Eliminar Articulo")
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity delete(@PathVariable Long id){
        articuloRepository.delete(id);
        return new ResponseEntity(new BasicOkResponse(), HttpStatus.OK);

    }
}
