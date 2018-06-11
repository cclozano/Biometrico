package com.example.example.datos;

import com.example.example.dominio.Huella;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface HuellaRespository extends JpaRepository<Huella,Long> {
    Huella findByRegMin(byte[] regMin);


    @Query("SELECT o FROM Huella o " +
            "WHERE o.persona is not null " )
    Collection<Huella> findActivas();
}
