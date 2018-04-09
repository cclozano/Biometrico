package com.example.example.servicios;

import com.example.example.dominio.Usuario;
import com.example.example.dominio.UsuarioComun;
import com.vaadin.ui.UI;
import org.springframework.stereotype.Component;

@Component
public class ServicioUsuarioImp implements ServicioUsuario{


    private String usuarioAdmin = "admin";

    private String claveAdmin = "admin.123";




    public Usuario autenticar(String usuario, String clave){

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
            setUsuario(u);
            return u;
        }
       return  null;
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
    public void crearUsuarioComun(UsuarioComun usuarioComun) throws ServiceException {

       return;
    }

    @Override
    public boolean recuperarCredenciales(String identificacion, String correo) {

        return false;
    }
}
