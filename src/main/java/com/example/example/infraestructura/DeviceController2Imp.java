package com.example.example.infraestructura;

import com.example.example.dominio.Huella;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DeviceController2Imp implements  DeviceController {


    @Getter
    private long deviceName;
    @Getter
    private long devicePort;
    @Getter
    private boolean estaInicializado = false;

    private boolean bLEDOn = false;

    @Override
    public boolean inicializarUSB() throws InfraestructuraException {

                throw new InfraestructuraException("OpenDevice() Error [" + "]");

    }

    @Override
    public Huella capturar(long segundos, long calidad) throws InfraestructuraException {

            throw  new InfraestructuraException("GetImageEx() Error : ");
    }

    @Override
    public boolean inicializar(long tipoConexion) throws InfraestructuraException {
        return false;
    }

    @Override
    public Huella verificarHuella(long nivelSeguridad, Huella huella, Collection<Huella> huellas) throws InfraestructuraException {

        FingerprintTemplate probe = new FingerprintTemplate()
                .dpi(500)
                .create(huella.getImageJpg());

        for(Huella h : huellas)
        {
            FingerprintTemplate candidate = new FingerprintTemplate()
                    .dpi(500)
                    .create(h.getImageJpg());
            double score = new FingerprintMatcher()
                    .index(probe)
                    .match(candidate);
            double threshold = 40;
            boolean matches = score >= threshold;
            if (matches) return h;
        }
        return huella;
    }

    @Override
    public boolean cerrarConexion() throws InfraestructuraException {
        return false;
    }

    @Override
    public boolean led() throws InfraestructuraException {

        return bLEDOn;
    }
}
