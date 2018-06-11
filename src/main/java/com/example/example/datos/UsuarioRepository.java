package com.example.example.datos;

import com.example.example.dominio.UsuarioComun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioComun,Long> {
    UsuarioComun findByIdentificacion(String iden);
    UsuarioComun findByNombreUsuario(String username);
}
