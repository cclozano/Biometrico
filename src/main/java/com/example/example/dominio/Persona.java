package com.example.example.dominio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter @Setter
public class Persona extends  EntidadBase{

    private String nombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private  String identificacion;


    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private byte[] foto ;

    public Persona() {

    }

    public Persona(String nombre, String identificacion) {
        this.nombre = nombre;
        this.identificacion = identificacion;
    }

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Collection<Huella> huellas = new ArrayList<>();


    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Collection<Asignacion> asignacions = new ArrayList<>();


    public void agregarHuella(Huella huella)
    {
        huella.setPersona(this);
        this.huellas.add(huella);
    }

    /*public Asignacion recibirArticulo(Articulo articulo) throws Exception {
        if (articulo.getAsignacion()!=null)
        {
            throw new DominioException("El articulo ya se encuentra asignado a: " + articulo.getAsignacion().getPersona());
        }
        Asignacion asignacion = new Asignacion(this,articulo);
        this.asignacions.add(asignacion);
        articulo.setAsignacion(asignacion);
        return asignacion;
    }*/

    public void agregarAsignacion(Asignacion asignacion)
    {
        this.asignacions.add(asignacion);
    }


    @Override
    public String toString() {
        return identificacion + " - " + nombre;
    }
}
