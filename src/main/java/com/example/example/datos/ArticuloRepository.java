package com.example.example.datos;

import com.example.example.dominio.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticuloRepository extends JpaRepository<Articulo,Long> {
    public  Articulo findByCodigoStartsWithIgnoreCase(String filterText);
    public  Articulo findByCodigo(String filterText);

}
