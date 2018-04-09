package com.example.example.infraestructura;

import SecuGen.FDxSDKPro.jni.*;
import com.example.example.dominio.Huella;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;


public class DeviceControllerImp implements  DeviceController {
    private long deviceName;
    private long devicePort;
    private JSGFPLib fplib = null;
    private long ret;
    private boolean bLEDOn = false;
    private byte[] regMin1 = new byte[400];
    private byte[] regMin2 = new byte[400];
    private byte[] vrfMin  = new byte[400];
    private SGDeviceInfoParam deviceInfo = new SGDeviceInfoParam();
    private BufferedImage imgRegistration1;
    private BufferedImage imgRegistration2;
    private BufferedImage imgVerification;


    private boolean estaInicializado = false;


    public JSGFPLib getFplib() {
        return fplib;
    }

    public void setFplib(JSGFPLib fplib) {
        this.fplib = fplib;
    }

    public boolean cerrarConexion() throws InfraestructuraException {//GEN-FIRST:event_jButtonCloseActionPerformed
        long iError;
        iError = fplib.Close();
        if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            return  true;
        }
        else
            throw new InfraestructuraException("Error : " + iError);
    }//GEN-LAST:event_jButtonCloseActionPerformed

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

    public String getDeviceInfo() {//GEN-FIRST:event_jButtonGetDeviceInfoActionPerformed
        long iError;

        iError = fplib.GetDeviceInfo(deviceInfo);
        if (ret == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            String res =
             "GetDeviceInfo() Success"+ " \n" +  new String(deviceInfo.deviceSN())+ " \n" +
                     new String(Integer.toString(deviceInfo.brightness))+ " \n" +
                     new String(Integer.toString((int)deviceInfo.contrast))+ " \n" +
                     new String(Integer.toString(deviceInfo.deviceID))+ " \n" +
                     new String(Integer.toHexString(deviceInfo.FWVersion))+ " \n" +
                     new String(Integer.toString(deviceInfo.gain))+ " \n" +
                     new String(Integer.toString(deviceInfo.imageDPI))+ " \n" +
                     new String(Integer.toString(deviceInfo.imageHeight))+ " \n" +
                     new String(Integer.toString(deviceInfo.imageWidth));
            return res;
        }
        else
            return  "GetDeviceInfo() Error : " + iError;

    }//GEN-LAST:event_jButtonGetDeviceInfoActionPerformed

    public String config() {//GEN-FIRST:event_jButtonConfigActionPerformed
        long iError;

        iError = fplib.Configure(0);
        if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            return  "Configure() Success";
        }
        else if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
            return  "Configure() not supported on this platform";
        else
            return  "Configure() Error : " + iError;


    }//GEN-LAST:event_jButtonConfigActionPerformed

    public Huella verificarHuella(long nivelSeguridad, Huella huella, Collection<Huella> huellas) throws InfraestructuraException {//GEN-FIRST:event_jButtonVerifyActionPerformed
        long iError;
        boolean[] matched = new boolean[1];
        matched[0] = false;

        for (Huella h :
                huellas) {
            iError = fplib.MatchTemplate(h.getRegMin(), huella.getRegMin(), nivelSeguridad, matched);
            if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                if (matched[0])
                    return  h;
            }
        }
        throw new InfraestructuraException("Huella no identificada");
    }//GEN-LAST:event_jButtonVerifyActionPerformed

    public String registrar(long nivelSeguridad) {
        int[] matchScore = new int[1];
        boolean[] matched = new boolean[1];
        long iError;
        matched[0] = false;
        iError = fplib.MatchTemplate(regMin1,regMin2, nivelSeguridad, matched);
        if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            matchScore[0] = 0;
            iError = fplib.GetMatchingScore(regMin1, regMin2, matchScore);

            if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                if (matched[0])
                    return  "Registration Success, Matching Score: " + matchScore[0];
                else
                    return  "Registration Fail, Matching Score: " + matchScore[0];

            }
            else
                return  "Registration Fail, GetMatchingScore() Error : " + iError;
        }
        else
            return  "Registration Fail, MatchTemplate() Error : " + iError;

    }

    public Huella captureV1(long segundos, long calidad) throws Exception {//GEN-FIRST:event_jButtonCaptureV1ActionPerformed
        Huella huella = new Huella();
        int[] quality = new int[1];
        byte[] imageBuffer1 = ((DataBufferByte) imgVerification.getRaster().getDataBuffer()).getData();
        long iError = SGFDxErrorCode.SGFDX_ERROR_NONE;

        iError = fplib.GetImageEx(imageBuffer1,segundos * 1000, 0, calidad);
        fplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);

        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;

        if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            //this.jLabelVerifyImage.setIcon(new ImageIcon(imgVerification.getScaledInstance(130,150, Image.SCALE_DEFAULT)));

            if (quality[0] == 0)
                throw new Exception("GetImageEx() Success [" + ret + "] but image quality is [" + quality[0] + "]. Please try again");
            else
            {
                //this.jLabelStatus.setText("GetImageEx() Success [" + ret + "]");

                iError = fplib.CreateTemplate(fingerInfo, imageBuffer1, vrfMin);

                if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
                {
                    huella.setRegMin(vrfMin);
                    huella.setImageBuffer(imageBuffer1);
                    //huella.setSgFingerInfo(fingerInfo);
                    return huella;
                    //this.jLabelStatus.setText("Verification image was captured");
                    //v1Captured = true;
                    //this.enableRegisterAndVerifyControls();
                }
                else
                    throw new Exception("CreateTemplate() Error : " + iError);
            }
        }
        else
            throw new Exception("GetImageEx() Error : " + iError);


    }//GEN-LAST:event_jButtonCaptureV1ActionPerformed

    public Huella capturar(long segundos, long calidad) throws InfraestructuraException {//GEN-FIRST:event_jButtonCaptureR2ActionPerformed

        Huella huella = new Huella();
        int[] quality = new int[1];
        byte[] imageBuffer1 = ((DataBufferByte) imgRegistration2.getRaster().getDataBuffer()).getData();
        long iError = SGFDxErrorCode.SGFDX_ERROR_NONE;

        iError = fplib.GetImageEx(imageBuffer1,segundos * 1000, 0, calidad);
        fplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);
        //huella.setQuality(quality);
        //this.jProgressBarR2.setValue(quality[0]);
        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;

        if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            //this.jLabelRegisterImage2.setIcon(new ImageIcon(imgRegistration2.getScaledInstance(130,150,Image.SCALE_DEFAULT)));
            if (quality[0] == 0)
                throw new InfraestructuraException("GetImageEx() Success [" + ret + "] but image quality is [" + quality[0] + "]. Please try again");
            else
            {
                //throw new Exception("GetImageEx() Success [" + ret + "]");

                iError = fplib.CreateTemplate(fingerInfo, imageBuffer1, regMin2);
                if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
                {
                    huella.setImageBuffer(imageBuffer1);
                    huella.setRegMin(regMin2);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(imgRegistration2, "jpg", baos);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new InfraestructuraException(e.getMessage());
                    }
                    byte[] bytes = baos.toByteArray();
                    huella.setImageJpg(bytes);
                    //huella.setSgFingerInfo(fingerInfo);
                    return huella;
                    /*this.jLabelStatus.setText("Second registration image was captured");
                    r2Captured = true;
                    this.enableRegisterAndVerifyControls();*/
                }
                else
                    throw new InfraestructuraException("CreateTemplate() Error : " + iError);
            }
        }
        else
            throw new InfraestructuraException("GetImageEx() Error : " + iError);


    }//GEN-LAST:event_jButtonCaptureR2ActionPerformed

    /*private void jButtonCaptureR1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCaptureR1ActionPerformed
        int[] quality = new int[1];
        byte[] imageBuffer1 = ((DataBufferByte) imgRegistration1.getRaster().getDataBuffer()).getData();
        long iError = SGFDxErrorCode.SGFDX_ERROR_NONE;

        iError = fplib.GetImageEx(imageBuffer1,jSliderSeconds.getValue() * 1000, 0, jSliderQuality.getValue());
        fplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);
        this.jProgressBarR1.setValue(quality[0]);
        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;

        if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
        {
            this.jLabelRegisterImage1.setIcon(new ImageIcon(imgRegistration1.getScaledInstance(130,150,Image.SCALE_DEFAULT)));
            if (quality[0] == 0)
                this.jLabelStatus.setText("GetImageEx() Success [" + ret + "] but image quality is [" + quality[0] + "]. Please try again");
            else
            {

                this.jLabelStatus.setText("GetImageEx() Success [" + ret + "]");

                iError = fplib.CreateTemplate(fingerInfo, imageBuffer1, regMin1);
                if (iError == SGFDxErrorCode.SGFDX_ERROR_NONE)
                {
                    this.jLabelStatus.setText("First registration image was captured");
                    r1Captured = true;
                    this.enableRegisterAndVerifyControls();
                }
                else
                    this.jLabelStatus.setText("CreateTemplate() Error : " + iError);
            }
        }
        else
            this.jLabelStatus.setText("GetImageEx() Error : " + iError);

    }//GEN-LAST:event_jButtonCaptureR1ActionPerformed*/

    public byte[] capture(long segundos, long calidad) throws Exception {//GEN-FIRST:event_jButtonCaptureActionPerformed
        int[] quality = new int[1];
        BufferedImage img1gray = new BufferedImage(deviceInfo.imageWidth, deviceInfo.imageHeight, BufferedImage.TYPE_BYTE_GRAY);
        byte[] imageBuffer1 = ((DataBufferByte) img1gray.getRaster().getDataBuffer()).getData();
        if (fplib != null)
        {
            ret = fplib.GetImageEx(imageBuffer1,segundos * 1000, 0, calidad);
            if (ret == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                //this.jLabelImage.setIcon(new ImageIcon(img1gray));
                long ret2 = fplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img1gray, "jpg", baos);
                byte[] bytes = baos.toByteArray();
                return bytes;
                //return img1gray;
                //this.jLabelStatus.setText("GetImageEx() Success [" + ret + "]" + " Image Quality [" + quality[0] + "]");
            }
            else
            {
                throw new Exception("GetImageEx() Error [" + ret + "]");
            }
        }
        else
        {
            throw new Exception("JSGFPLib is not Initialized");
        }

    }//GEN-LAST:event_jButtonCaptureActionPerformed

    public void toggleLEDActionPerformed(java.awt.event.ActionEvent evt) throws Exception {//GEN-FIRST:event_jButtonToggleLEDActionPerformed
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
                throw new Exception("SetLedOn(" + bLEDOn + ") Error [" + ret + "]");
            }
        }
        else
        {
            throw new Exception("JSGFPLib is not Initialized");
        }
    }

    public boolean inicializarUSB() throws InfraestructuraException {
        if(estaInicializado) return true;
        int selectedDevice = 0;
        switch(selectedDevice)
        {
            case 0: //USB
            default:
                this.deviceName = SGFDxDeviceName.SG_DEV_AUTO;
                break;
            case 1: //FDU04
                this.deviceName = SGFDxDeviceName.SG_DEV_FDU04;
                break;
            case 2: //CN_FDU03
                this.deviceName = SGFDxDeviceName.SG_DEV_FDU03;
                break;
            case 3: //CN_FDU02
                this.deviceName = SGFDxDeviceName.SG_DEV_FDU02;
                break;
        }
        fplib = new JSGFPLib();

        ret = fplib.Init(this.deviceName);
        if ((fplib != null) && (ret  == SGFDxErrorCode.SGFDX_ERROR_NONE))
        {
            //this.jLabelStatus.setText("JSGFPLib Initialization Success");
            this.devicePort = SGPPPortAddr.USB_AUTO_DETECT;
            /*switch (this.jComboBoxUSBPort.getSelectedIndex())
            {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    this.devicePort = this.jComboBoxUSBPort.getSelectedIndex() - 1;
                    break;
            }*/
            ret = fplib.OpenDevice(devicePort);
            if (ret == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                //this.jLabelStatus.setText("OpenDevice() Success [" + ret + "]");
                ret = fplib.GetDeviceInfo(deviceInfo);
                if (ret == SGFDxErrorCode.SGFDX_ERROR_NONE)
                {

                    /*this.jTextFieldSerialNumber.setText(new String(deviceInfo.deviceSN()));
                    this.jTextFieldBrightness.setText(new String(Integer.toString(deviceInfo.brightness)));
                    this.jTextFieldContrast.setText(new String(Integer.toString((int)deviceInfo.contrast)));
                    this.jTextFieldDeviceID.setText(new String(Integer.toString(deviceInfo.deviceID)));
                    this.jTextFieldFWVersion.setText(new String(Integer.toHexString(deviceInfo.FWVersion)));
                    this.jTextFieldGain.setText(new String(Integer.toString(deviceInfo.gain)));
                    this.jTextFieldImageDPI.setText(new String(Integer.toString(deviceInfo.imageDPI)));
                    this.jTextFieldImageHeight.setText(new String(Integer.toString(deviceInfo.imageHeight)));
                    this.jTextFieldImageWidth.setText(new String(Integer.toString(deviceInfo.imageWidth)));*/
                    //imgRegistration1 = new BufferedImage(deviceInfo.imageWidth, deviceInfo.imageHeight, BufferedImage.TYPE_BYTE_GRAY);
                    imgRegistration2 = new BufferedImage(deviceInfo.imageWidth, deviceInfo.imageHeight, BufferedImage.TYPE_BYTE_GRAY);
                    //imgVerification = new BufferedImage(deviceInfo.imageWidth, deviceInfo.imageHeight, BufferedImage.TYPE_BYTE_GRAY);
                    //this.enableControls();
                    estaInicializado = true;
                    return estaInicializado;
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
    public boolean inicializar(long tipoConexion) throws InfraestructuraException {
        return false;
    }


}
