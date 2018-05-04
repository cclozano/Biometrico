package com.example.example.servicios;

import com.example.example.dominio.Usuario;
import com.example.example.dominio.UsuarioComun;

/**
 * Created by max on 20/06/17.
 */
public interface ServicioUsuario {
    Usuario autenticar(String usuario, String clave);
    Usuario getUsuario();
    void crearUsuarioComun(UsuarioComun usuarioComun) throws ServiceException;
    boolean recuperarCredenciales(String identificacion, String correo);
    Usuario findById(long id);

}
