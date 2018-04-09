package com.example.example.infraestructura;

import com.example.example.dominio.Huella;

import java.util.Collection;

public interface DeviceController {
    boolean inicializarUSB()throws InfraestructuraException;
    boolean inicializar(long tipoConexion)throws InfraestructuraException;
    Huella verificarHuella(long nivelSeguridad, Huella huella, Collection<Huella> huellas) throws InfraestructuraException ;
    Huella capturar(long segundos, long calidad) throws InfraestructuraException;
    boolean cerrarConexion()throws InfraestructuraException;
    boolean led() throws InfraestructuraException;
}
