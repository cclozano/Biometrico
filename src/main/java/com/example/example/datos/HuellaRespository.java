package com.example.example.datos;

import com.example.example.dominio.Huella;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HuellaRespository extends JpaRepository<Huella,Long> {
    Huella findByRegMin(byte[] regMin);
}
