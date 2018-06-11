package com.example.example.dominio;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date fechaAsignacion;
    @Temporal(value = TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date fechaEliminacion;

    private boolean activo;



    private String novedadesAsignacion;
    private String novedadesDevolucion;

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
