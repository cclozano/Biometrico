package com.example.example.servicios;

import com.example.example.datos.HuellaRespository;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.infraestructura.DeviceController;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
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
        Collection<Huella> huellas = huellaRespository.findActivas();
            Huella h = controller.verificarHuella(5,huella,huellas);
            if (h!= null)
            {
                return h.getPersona();
            }
            else {
                throw new ServiceException("No se encontraron coincidencias");
            }
    }

    @Override
    public boolean compararHuellas(byte[] h1, byte[] h2) {
        FingerprintTemplate probe = new FingerprintTemplate()
                .dpi(500)
                .create(h1);


            FingerprintTemplate candidate = new FingerprintTemplate()
                    .dpi(500)
                    .create(h2);
            double score = new FingerprintMatcher()
                    .index(probe)
                    .match(candidate);
            double threshold = 40;
            boolean matches = score >= threshold;
            return matches;

    }
}
