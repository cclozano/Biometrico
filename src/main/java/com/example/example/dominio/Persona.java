package com.example.example.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Collection<Huella> huellas = new ArrayList<>();


    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Collection<Asignacion> asignacions = new ArrayList<>();


    public void agregarHuella(Huella huella)
    {
        huella.setPersona(this);
        this.huellas.add(huella);
    }

    public Huella quitarHuella(Long idHuella)
    {
        for (Huella h :
                huellas) {
            if (idHuella.equals(h.getId())) {
                huellas.remove(h);
                h.setPersona(null);
                return h;
            }
        }
        return null;
    }


    public void agregarAsignacion(Asignacion asignacion)
    {
        this.asignacions.add(asignacion);
    }


    @Override
    public String toString() {
        return this.getId() + "-" +identificacion + "-" +  getNombre() + " " + getSegundoNombre() + " " + getApellidoPaterno() + " " + getApellidoMaterno();
    }
}
