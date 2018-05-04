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
        /*long iError,ierrorTemplate;
        boolean[] matched = new boolean[1];
        matched[0] = false;
        ierrorTemplate = fplib.CreateTemplate(huella.getHuellaInfo().getInfo(), huella.getImageBuffer(), huella.getRegMin());
        if (ierrorTemplate == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            for (Huella h :
                    huellas) {
                ierrorTemplate = fplib.CreateTemplate(h.getHuellaInfo().getInfo(), h.getImageBuffer(), h.getRegMin());
                if (ierrorTemplate == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                    iError = fplib.MatchTemplate(huella.getRegMin(), h.getRegMin(), nivelSeguridad, matched);
                    if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
                    {
                        if (matched[0])
                            return h;
                    }
                    else throw new InfraestructuraException("Verification Attempt 1 Fail - MatchTemplate() Error : " + iError);
                } else throw new InfraestructuraException("verificarHuella() Error : " + ierrorTemplate);
            }
        }
        else throw  new InfraestructuraException("verificarHuella() Error : " + ierrorTemplate);
        throw new InfraestructuraException("Huella no identificada");*/


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
