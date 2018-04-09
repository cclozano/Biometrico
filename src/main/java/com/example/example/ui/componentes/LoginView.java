package com.example.example.ui.componentes;

import com.example.example.dominio.UsuarioComun;
import com.example.example.servicios.ServicioUsuario;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")

public class LoginView extends VerticalLayout {

    private CallBack callBack;
    final TextField username = new TextField("Usuario");
    final PasswordField password = new PasswordField("Contraseña");


    private ServicioUsuario servicioUsuario;


    public LoginView(CallBack callBack, ServicioUsuario servicioUsuario) {
        this.callBack = callBack;
        this.servicioUsuario = servicioUsuario;
        setSizeFull();
        setMargin(false);
        setSpacing(false);
        Component loginForm = buildLoginForm();
        addComponent(loginForm);
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
    }

    private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setMargin(false);
        Responsive.makeResponsive(loginPanel);
        loginPanel.addStyleName("login-panel");

        loginPanel.addComponent(buildLabels());
        loginPanel.addComponent(buildFields());
        Button registrar = new Button("Registrase");
        registrar.addClickListener(clickEvent -> mostarRegistroNuevoUsuario());
        registrar.addStyleName(ValoTheme.BUTTON_LINK);

        Button recuperarContrasena = new Button("Recuperar Credenciales");
        recuperarContrasena.addStyleName(ValoTheme.BUTTON_LINK);
        recuperarContrasena.addClickListener(clickEvent -> mostarRecuperarCredenciales());

        //loginPanel.addComponent(registrar);
        HorizontalLayout horizontalLayout = new HorizontalLayout(registrar,recuperarContrasena);
        horizontalLayout.setWidth("-1px");
        loginPanel.addComponents(horizontalLayout);
        return loginPanel;
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.addStyleName("fields");


        username.setIcon(FontAwesome.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);


        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final Button signin = new Button("Iniciar Sesión");
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(KeyCode.ENTER);
        signin.focus();

        fields.addComponents(username, password, signin);
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

        signin.addClickListener((ClickListener) event -> {
            this.callBack.autenticar(this.username.getValue(),this.password.getValue());
        });
        return fields;
    }

    private Component buildLabels() {
        CssLayout labels = new CssLayout();
        labels.addStyleName("labels");

        Label welcome = new Label("");
        welcome.setSizeUndefined();
        welcome.addStyleName(ValoTheme.LABEL_H4);
        welcome.addStyleName(ValoTheme.LABEL_COLORED);
        labels.addComponent(welcome);

        Label title = new Label("Asignacion Biometrica");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        labels.addComponent(title);
        return labels;
    }

    public interface CallBack
    {
        void autenticar(String usuario, String clave);
    }


    public void mostarRegistroNuevoUsuario()
    {
        Window window = new Window("Actualizar Datos");
        window.center();
        window.setModal(true);
        window.setWidth("50%");
        window.setHeightUndefined();

        TextField nombreUsuario = new TextField("Usuario:");
        TextField identificacion = new TextField("Identificacion:");
        PasswordField password = new PasswordField("Clave:");
        PasswordField password2 = new PasswordField("Confirmacion Clave:");
        TextField correo = new TextField("Correo:");
        TextField nombreCompleto = new TextField("Nombre Completo");

        Button registrarButton = new Button("Registrar");
        registrarButton.addStyleName(ValoTheme.BUTTON_LINK);
        Button regresarButon = new Button("Regresar");
        regresarButon.addClickListener(clickEvent -> UI.getCurrent().removeWindow(window));
        regresarButon.addStyleName(ValoTheme.BUTTON_LINK);

        BeanValidationBinder<UsuarioComun> binder
                = new BeanValidationBinder<>(UsuarioComun.class);
        UsuarioComun usuarioComun = new UsuarioComun();

        nombreCompleto.setWidth("100%");
        nombreUsuario.setWidth("100%");
        identificacion.setWidth("100%");
        password.setWidth("100%");
        password2.setWidth("100%");
        correo.setWidth("100%");

        HorizontalLayout vl  = new HorizontalLayout(registrarButton,regresarButon);
        vl.setSpacing(true);
        FormLayout formLayout = new FormLayout(nombreCompleto,identificacion,nombreUsuario,password,password2,correo,vl);
        binder.bind(nombreCompleto,"nombreCompleto");
        binder.bind(identificacion,"identificacion");
        binder.bind(nombreUsuario,"nombreUsuario");
        binder.bind(password,"password");
        binder.bind(correo,"correo");

        formLayout.setWidth("100%");
        formLayout.setHeightUndefined();
        formLayout.setMargin(true);
        binder.setBean(usuarioComun);

        registrarButton.setEnabled(false);
        binder.addValueChangeListener(valueChangeEvent -> {
             registrarButton.setEnabled(binder.isValid() );
        });
        registrarButton.addClickListener(clickEvent -> {
            if (!password2.getValue().equals(password.getValue()))
            {
                Notification.show("La clave y la confirmacion deben de ser iguales", Notification.Type.HUMANIZED_MESSAGE);
                return;
            }
            try {
                servicioUsuario.crearUsuarioComun(binder.getBean());
                binder.setBean(new UsuarioComun());
                password2.setValue("");
                Notification.show("Nuevo Usuario Creado", Notification.Type.HUMANIZED_MESSAGE);
                UI.getCurrent().getWindows().forEach(w-> UI.getCurrent().removeWindow(w));
            } catch (Exception e) {
                Notification.show("Error",e.getMessage(), Notification.Type.HUMANIZED_MESSAGE);
            }

        });

        window.setContent(formLayout);
        UI.getCurrent().addWindow(window);

    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void mostarRecuperarCredenciales()
    {
        ToolBar toolBar = new ToolBar();
        Button recuperar = new Button(FontAwesome.CHECK_SQUARE);
        toolBar.addDerecha(recuperar);

        TextField txtCorreo = new TextField("Correo:");
        TextField txtIdentificacion = new TextField("Identificacion:");
        txtCorreo.setWidth("100%");
        txtIdentificacion.setWidth("100%");
        FormLayout form  = new FormLayout(txtCorreo,txtIdentificacion);
        form.setWidth("100%");
        form.setHeightUndefined();
        VerticalLayout verticalLayout = new VerticalLayout(toolBar,form);
        verticalLayout.setWidth("100%");
        verticalLayout.setHeightUndefined();
        verticalLayout.setExpandRatio(form,1f);

        Window window = new Window("Recuperar Credenciales");
        window.center();
        window.setModal(true);
        window.setWidth("50%");
        //window.setHeightUndefined();
        window.setContent(verticalLayout);

        UI.getCurrent().addWindow(window);

        recuperar.addClickListener(clickEvent -> {

           if(txtCorreo.getValue()==null || txtIdentificacion.getValue() == null
                   || txtCorreo.getValue().length() == 0 || txtIdentificacion.getValue().length() == 0)
           {
               Notification.show("Ingrese un correo y una identificacion", Notification.Type.HUMANIZED_MESSAGE);
               return;
           }

           Boolean respuesta = this.servicioUsuario.recuperarCredenciales(txtIdentificacion.getValue(),txtCorreo.getValue());

           if(respuesta)
           {
               Notification.show("Por favor revise su correo, hemos enviado sus credenciales", Notification.Type.HUMANIZED_MESSAGE);
               UI.getCurrent().removeWindow(window);
           }
           else
           {
               Notification.show("No hemos encotrado registro con los datos enviados," +
                       " por favor intenete nuevamente o registrese", Notification.Type.HUMANIZED_MESSAGE);
           }
        });
    }

}
