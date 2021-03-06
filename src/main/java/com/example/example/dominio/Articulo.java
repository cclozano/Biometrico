package com.example.example.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter @Setter
@Entity
public class Articulo extends  EntidadBase{

    private  String codigo;
    private  String descripcion;
    private String numeroSerie;
    private String modelo;
    private String fabricante;

    @JsonIgnore
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
