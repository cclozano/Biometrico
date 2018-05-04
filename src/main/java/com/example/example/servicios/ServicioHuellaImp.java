package com.example.example.servicios;

import com.example.example.datos.HuellaRespository;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.infraestructura.DeviceController;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServicioHuellaImp implements ServicioHuella {
    @Autowired
    HuellaRespository huellaRespository;

    @Autowired
    DeviceController controller;

    @Override
    public Persona encontrarPersona(Huella huella) throws Exception {
        Collection<Huella> huellas = huellaRespository.findAll();
            Huella h = controller.verificarHuella(5,huella,huellas);
            if (h!= null)
            {
                return h.getPersona();
            }
            else {
                throw new ServiceException("No se encontraron coincidencias");
            }
    }
}
