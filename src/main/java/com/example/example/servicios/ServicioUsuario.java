package com.example.example.servicios;

import com.example.example.dominio.Usuario;
import com.example.example.dominio.UsuarioComun;
import com.example.example.exceptions.AuthException;
import com.example.example.exceptions.ServiceException;

/**
 * Created by max on 20/06/17.
 */
public interface ServicioUsuario {
    Usuario autenticar(String usuario, String clave) throws AuthException;
    Usuario getUsuario();
    void eliminar(Long id);
    Usuario actualizat(Usuario usuario);
    void crearUsuarioComun(UsuarioComun usuarioComun) throws ServiceException;
    boolean recuperarCredenciales(String identificacion, String correo);
    Usuario findById(long id);

}
