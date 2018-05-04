package com.example.example.dominio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Huella extends  EntidadBase{

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] regMin = null;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] imageJpg ;

    private Dedo dedo;
    private Mano mano;


    private int fingerNumber = 0;
    private int viewNumber = 0;
    private int impressionType = 0;
    private int imageQuality = 0;


    @ManyToOne
    private Persona persona;

}
