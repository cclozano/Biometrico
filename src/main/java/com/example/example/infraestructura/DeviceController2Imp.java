package com.example.example.infraestructura;

import SecuGen.FDxSDKPro.jni.*;
import com.example.example.dominio.Huella;
import com.example.example.dominio.HuellaInfo;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

@Component
public class DeviceController2Imp implements  DeviceController {

    @Getter
    private JSGFPLib fplib = null;
    @Getter
    private long deviceName;
    @Getter
    private long devicePort;
    @Getter
    private boolean estaInicializado = false;

    private boolean bLEDOn = false;
    private long ret;
    private SGDeviceInfoParam deviceInfo = new SGDeviceInfoParam();

    @Override
    public boolean inicializarUSB() throws InfraestructuraException {
        if (estaInicializado) return estaInicializado;
        this.deviceName = SGFDxDeviceName.SG_DEV_AUTO;
        fplib = new JSGFPLib();
        ret = fplib.Init(this.deviceName);
        if ((fplib != null) && (ret  == SGFDxErrorCode.SGFDX_ERROR_NONE))
        {
            this.devicePort = SGPPPortAddr.USB_AUTO_DETECT;
            ret = fplib.OpenDevice(devicePort);
            if (ret == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                ret = fplib.GetDeviceInfo(deviceInfo);
                if (ret == SGFDxErrorCode.SGFDX_ERROR_NONE)
                {
                    //imgRegistration1 = new BufferedImage(deviceInfo.imageWidth, deviceInfo.imageHeight, BufferedImage.TYPE_BYTE_GRAY);
                    estaInicializado = true;
                    return true;
                }
                else
                    throw new InfraestructuraException("GetDeviceInfo() Error [" + ret + "]");
            }
            else
                throw new InfraestructuraException("OpenDevice() Error [" + ret + "]");
        }
        else
            throw new InfraestructuraException("JSGFPLib Initialization Failed");
    }

    @Override
    public Huella capturar(long segundos, long calidad) throws InfraestructuraException {
        int[] quality = new int[1];
        BufferedImage imgVerification = new BufferedImage(deviceInfo.imageWidth, deviceInfo.imageHeight, BufferedImage.TYPE_BYTE_GRAY);
        byte[] imageBuffer1 = ((DataBufferByte) imgVerification.getRaster().getDataBuffer()).getData();
        long iError = SGFDxErrorCode.SGFDX_ERROR_NONE;
        iError = fplib.GetImageEx(imageBuffer1,segundos * 1000, 0, calidad);
        fplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);
        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;

        byte[] vrfMin  = new byte[400];

        if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            //this.jLabelVerifyImage.setIcon(new ImageIcon(imgVerification.getScaledInstance(130,150, Image.SCALE_DEFAULT)));
            if (quality[0] == 0)
                throw  new InfraestructuraException("GetImageEx() Success [" + ret + "] but image quality is [" + quality[0] + "]. Please try again");
            else
            {
                //this.jLabelStatus.setText("GetImageEx() Success [" + ret + "]");

                iError = fplib.CreateTemplate(fingerInfo, imageBuffer1, vrfMin);
                if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
                {
                    Huella huella = new Huella();
                    huella.setImageBuffer(imageBuffer1);
                    huella.setRegMin(vrfMin);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(imgVerification, "jpg", baos);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new InfraestructuraException(e.getMessage());
                    }
                    byte[] bytes = baos.toByteArray();
                    huella.setImageJpg(bytes);
                    HuellaInfo huellaInfo = new HuellaInfo(fingerInfo,huella);
                    huella.setHuellaInfo(huellaInfo);
                    return huella;
                }
                else
                    throw  new InfraestructuraException("CreateTemplate() Error : " + iError);
            }
        }
        else
            throw  new InfraestructuraException("GetImageEx() Error : " + iError);
    }

    @Override
    public boolean inicializar(long tipoConexion) throws InfraestructuraException {
        return false;
    }

    @Override
    public Huella verificarHuella(long nivelSeguridad, Huella huella, Collection<Huella> huellas) throws InfraestructuraException {
        long iError,ierrorTemplate;
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
        throw new InfraestructuraException("Huella no identificada");
    }

    @Override
    public boolean cerrarConexion() throws InfraestructuraException {
        return false;
    }

    @Override
    public boolean led() throws InfraestructuraException {
        if (fplib != null)
        {
            bLEDOn = !bLEDOn;
            ret = fplib.SetLedOn(bLEDOn);
            if (ret == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                //this.jLabelStatus.setText("SetLedOn(" + bLEDOn + ") Success [" + ret + "]");
            }
            else
            {
                throw new InfraestructuraException("SetLedOn(" + bLEDOn + ") Error [" + ret + "]");
            }
        }
        else
        {
            throw new InfraestructuraException("JSGFPLib is not Initialized");
        }
        return bLEDOn;
    }
}
