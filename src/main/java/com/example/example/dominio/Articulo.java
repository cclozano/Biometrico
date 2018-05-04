package com.example.example.dominio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;


@Getter @Setter
@Entity
public class Articulo extends  EntidadBase{

    private  String codigo;
    private  String descripcion;

    @OneToOne
    private Asignacion asignacion;

    @Override
    public String toString() {
        return codigo + " " + descripcion;
    }

    public String getAsignado()
    {
        return asignacion == null?"No":"Si";
    }

    public String getPersona()
    {
        return asignacion==null?"":asignacion.getPersona().toString();
    }
}
