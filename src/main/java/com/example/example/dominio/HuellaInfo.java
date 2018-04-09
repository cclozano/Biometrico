package com.example.example.dominio;

import SecuGen.FDxSDKPro.jni.SGFingerInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
public class HuellaInfo extends EntidadBase {
    private int fingerNumber = 0;
    private int viewNumber = 0;
    private int impressionType = 0;
    private int imageQuality = 0;

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Huella huella;

    public HuellaInfo() {
    }

    public HuellaInfo(SGFingerInfo info, Huella huella) {
        this.fingerNumber = info.FingerNumber;
        this.viewNumber = info.ViewNumber;
        this.impressionType = info.ImpressionType;
        this.imageQuality = info.ImageQuality;
        this.huella = huella;
    }

    public SGFingerInfo getInfo()
    {
        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = fingerNumber;
        fingerInfo.ImageQuality = imageQuality;
        fingerInfo.ImpressionType = impressionType;
        fingerInfo.ViewNumber = viewNumber;
        return fingerInfo;
    }

}
