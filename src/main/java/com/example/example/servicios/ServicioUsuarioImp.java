package com.example.example.servicios;

import com.example.example.datos.UsuarioRepository;
import com.example.example.dominio.Usuario;
import com.example.example.dominio.UsuarioComun;
import com.example.example.exceptions.AuthException;
import com.example.example.exceptions.ServiceException;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServicioUsuarioImp implements ServicioUsuario{


    private String usuarioAdmin = "admin";

    private String claveAdmin = "admin";


    @Autowired
    UsuarioRepository usuarioRepository;


    public Usuario autenticar(String usuario, String clave) throws AuthException {
        if(usuario.equals(usuarioAdmin) && clave.equals(claveAdmin))
        {
            Usuario u = new Usuario() {
                @Override
                public String getUsuario() {
                    return "admin";
                }

                @Override
                public String getIdentificacion() {
                    return "999999999";
                }

                @Override
                public String getNombreCompleto() {
                    return "Administrador del Sistema";
                }

                @Override
                public TipoUsuario getTipoUsuario() {
                    return TipoUsuario.ADMINISTRADOR;
                }
            };
            //setUsuario(u);
            return u;
        }
        UsuarioComun u = usuarioRepository.findByNombreUsuario(usuario);
        if(u==null) throw new AuthException("Usuario no existe");
        if(u.getPassword().equals(clave)) return u;
        else throw new AuthException("Clave incorrecta");


    }

    private void setUsuario(Usuario usuario)
    {
        UI.getCurrent().getSession().setAttribute("usuario",usuario);
    }

    public Usuario getUsuario()
    {
       return (Usuario) UI.getCurrent().getSession().getAttribute("usuario");
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.delete(id);
    }

    @Override
    public Usuario actualizat(Usuario usuario) {
        return null;
    }

    @Override
    public void crearUsuarioComun(UsuarioComun usuarioComun) throws ServiceException {

       return;
    }

    @Override
    public boolean recuperarCredenciales(String identificacion, String correo) {

        return false;
    }

    @Override
    public Usuario findById(long id) {
        Usuario u = new UsuarioComun();
        ((UsuarioComun) u).setNombre("Carlos Lozano");
        return u;
    }
}
