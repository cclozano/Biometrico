package com.example.example.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter @Setter
public class Asignacion extends EntidadBase{

    @Temporal(value = TemporalType.TIMESTAMP)
    @NotNull
    private Date fechaAsignacion;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date fechaEliminacion;

    private boolean activo;

    @JsonIgnore
    @ManyToOne
    private Persona persona;

    @OneToOne
    private Articulo articulo;

    public Asignacion() {
    }
    public Asignacion(Persona persona, Articulo articulo) {
        this.persona = persona;
        this.articulo = articulo;
        fechaAsignacion = new Date();
        activo = true;
    }

    public void desasociar()
    {
        fechaEliminacion = new Date();
        activo = false;
    }
}
