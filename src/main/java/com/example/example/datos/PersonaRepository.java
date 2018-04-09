package com.example.example.datos;

import com.example.example.dominio.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona,Long> {
    public  Persona findByNombreStartsWithIgnoreCase(String filterText);

}
