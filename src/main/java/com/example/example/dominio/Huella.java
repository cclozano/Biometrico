package com.example.example.dominio;

//import com.vaadin.server.StreamResource;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter @Setter
public class Huella extends  EntidadBase{

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] regMin = new byte[400];

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] imageBuffer ;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] imageJpg ;

    //private StreamResource resource;
    //int[] quality = new int[1];

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private HuellaInfo huellaInfo;



    @ManyToOne
    private Persona persona;


    public String getCalidad(){
        return huellaInfo == null? "":"" + huellaInfo.getImageQuality()*0.01f + " %";
    }

    public float getcalidad()
    {
        return huellaInfo == null? 0: huellaInfo.getImageQuality()*0.01f;
    }


}
