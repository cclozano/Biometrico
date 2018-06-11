package com.example.example.apirest;


import com.example.example.apirest.util.BasicOkResponse;
import com.example.example.apirest.util.CustomErrorType;
import com.example.example.datos.HuellaRespository;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.servicios.ServicioHuella;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("biometrico/v1/huellas")
@Api(value="biometrico/v1/huellas", description="Asignacion Biometrica")
public class HuellaController {

    public static final Logger logger = LoggerFactory.getLogger(HuellaController.class);

    @Autowired
    HuellaRespository huellaRespository;

    @Autowired
    ServicioHuella servicioHuella;

    @ApiOperation(value = "Buscar Huella por ID",response = Huella.class)
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public Huella showProduct(@PathVariable Long id, Model model){
        Huella huella = huellaRespository.findOne(id);
        if (huella.getPersona()==null)return null;
        return huella;
    }


    @ApiOperation(value = "Actualizar Huella")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity modificar(@PathVariable Long id, @RequestBody Huella huella){
        Huella p = huellaRespository.findOne(id);
        if(p == null)
        {
            return new ResponseEntity(new CustomErrorType("No se encontro la Huella"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        p.setDedo(huella.getDedo());
        p.setFingerNumber(huella.getFingerNumber());
        p.setImageJpg(huella.getImageJpg());
        p.setImageQuality(huella.getImageQuality());
        p.setImpressionType(huella.getImpressionType());
        p.setMano(huella.getMano());
        p.setRegMin(huella.getRegMin());
        p.setViewNumber(huella.getViewNumber());
        huellaRespository.save(p);
        return new ResponseEntity(p, HttpStatus.OK);
    }

    @ApiOperation(value = "Eliminar Huella")
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity delete(@PathVariable Long id){
        huellaRespository.delete(id);
        return new ResponseEntity(new BasicOkResponse(), HttpStatus.OK);

    }


    @ApiOperation(value = "Comparar Huellas",response = Persona.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> asignar(@RequestBody List<Huella> huellas) {
        logger.info("comparando huellas {} ", huellas);
        if(huellas.size()!=2)
            return new ResponseEntity(new CustomErrorType("Se deben enviar 2 y solo 2 huellas para comparar"), HttpStatus.INTERNAL_SERVER_ERROR);
        boolean b = servicioHuella.compararHuellas(huellas.get(0).getImageJpg(),huellas.get(1).getImageJpg());
        return new ResponseEntity<>(b, HttpStatus.OK);
    }

}
