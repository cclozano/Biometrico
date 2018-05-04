package com.example.example;

import com.example.example.dominio.Usuario;
import com.example.example.servicios.ServicioUsuario;
import com.example.example.ui.*;
import com.example.example.ui.componentes.DefaultView;
import com.example.example.ui.componentes.ExitView;
import com.example.example.ui.componentes.LoginView;
import com.example.example.ui.componentes.OptionMenu;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by max on 09/06/17.
 */
@SpringUI
@Theme("valo")
//@Theme("dashboard")
@Title("Asignacion Biometrica")
@SpringViewDisplay
public class BiometricoUI extends UI implements ViewDisplay{



    @Autowired
    private ServicioUsuario servicioUsuario;


    private CssLayout menu = new CssLayout();
    private ValoMenuLayout root = new ValoMenuLayout();
    private CssLayout menuItemsLayout = new CssLayout();


    private boolean testMode;

    @Override
    protected void init(VaadinRequest request) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        if (request.getParameter("test") != null) {
            testMode = true;
            if (browserCantRenderFontsConsistently()) {
                getPage().getStyles().add(
                        ".v-app.v-app.v-app {font-family: Sans-Serif;}");
            }
        }
        if (getPage().getWebBrowser().isIE()
                && getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }

        if (!testMode) {
            Responsive.makeResponsive(this);
        }
        getNavigator().addView("", DefaultView.class);



        if(servicioUsuario.getUsuario()==null) {
            setContent(this.getLoginView());
            addStyleName("loginview");
        }
        else
        {
            loadRoot();
        }

           }


    private void loadRoot()
    {
        setTheme("tests-valo-facebook");
        root.setWidth("100%");
        root.addMenu(buildMenu());
        Notification.show("Hola","Bienvenido(a) " +
                this.servicioUsuario.getUsuario().getNombreCompleto(), Notification.Type.TRAY_NOTIFICATION);
        getNavigator().navigateTo(DefaultView.VIEW_NAME);
        setContent(root);
    }

    private LoginView loginView;
    private LoginView getLoginView()
    {
        if(loginView ==null) {
            loginView = new LoginView((usuario, clave) -> {

                if(this.servicioUsuario.autenticar(usuario,clave)!=null)
                {
                    loadRoot();
                }
                else {
                    Notification.show("Usuario o Clave Incorrectas");

                }
            },this.servicioUsuario);


            //setContent(loginView);
        }
        return this.loginView;
    }



    private boolean browserCantRenderFontsConsistently() {

        return getPage().getWebBrowser().getBrowserApplication()
                .contains("PhantomJS")
                || (getPage().getWebBrowser().isIE() && getPage()
                .getWebBrowser().getBrowserMajorVersion() <= 9);
    }


    private CssLayout buildMenu() {
        final HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName("valo-menu-title");
        menu.addComponent(top);
        //menu.addComponent(createThemeSelect());
        final Button showMenu = new Button("Menu", new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                if (menu.getStyleName().contains("valo-menu-visible")) {
                    menu.removeStyleName("valo-menu-visible");
                } else {
                    menu.addStyleName("valo-menu-visible");
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName("valo-menu-toggle");
        showMenu.setIcon(FontAwesome.LIST);
        menu.addComponent(showMenu);
        final Label title = new Label(
                "<h3><strong>Biometrico </strong></h3>", ContentMode.HTML);
        title.setSizeUndefined();
        top.addComponent(title);
        top.setExpandRatio(title, 1);
        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        // final StringGenerator sg = new StringGenerator();
        //User user = (User)this.authentication.getPrincipal();
        final MenuBar.MenuItem settingsItem = settings.addItem( this.servicioUsuario.getUsuario().getUsuario(),
                new ThemeResource("../tests-valo/img/profile-pic-300px.jpg"),
                null);
        Usuario usuario = this.servicioUsuario.getUsuario();


        settingsItem.addSeparator();
        settingsItem.addItem("Cerrar Session", x-> {

            ConfirmDialog.show(UI.getCurrent(),
                    "Salir",
                    "Desea cerrar session?",
                    "Salir",
                    "Cancelar",
                    confirmDialog -> {
                        if(confirmDialog.isConfirmed())
                        {
                            UI.getCurrent().getSession().close();
                            UI.getCurrent().getSession().getService().closeSession(VaadinSession.getCurrent());
                            UI.getCurrent().close();

                            setContent(new ExitView());
                        }
                    });
        });



        menu.addComponent(settings);
        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);
        // Label label = null;
        int count = -1;
        for (OptionMenu item :getOptionsMenu()) {

            final Button b = new Button(item.getNombre(), new Button.ClickListener() {
                @Override
                public void buttonClick(final Button.ClickEvent event) {
                    String key = item.getId();
                    getNavigator().navigateTo(key);
                }
            });

            b.setHtmlContentAllowed(true);
            b.setPrimaryStyleName("valo-menu-item");
            b.setIcon(item.getIcon());
            menuItemsLayout.addComponent(b);
            count++;
        }
        return menu;
    }



    @Override
    public void showView(View view) {
        root.getContentContainer().removeAllComponents();
        root.getContentContainer().addComponent((Component) view);
    }


    private List<OptionMenu> getOptionsMenu()
    {


        OptionMenu optionPersona = new OptionMenu(FontAwesome.SMILE_O, PersonaView.VIEW_NAME,"Personas");
        OptionMenu optionArticulo = new OptionMenu(FontAwesome.PENCIL, ArticulosView.VIEW_NAME,"Articulos");
        OptionMenu optionTest = new OptionMenu(FontAwesome.SMILE_O, TestView.VIEW_NAME,"Test");
        OptionMenu optionconfig = new OptionMenu(FontAwesome.GEARS, ConfigView.VIEW_NAME,"Ajustes");
        ArrayList<OptionMenu> menus = new ArrayList<>();

        Usuario usuario = this.servicioUsuario.getUsuario();
        switch (usuario.getTipoUsuario())
        {
            case ADMINISTRADOR:
                menus.add(optionPersona);
                menus.add(optionArticulo);
                menus.add(optionconfig);
                menus.add(optionTest);
                break;
            case REGISTRADOR:
                menus.add(optionPersona);
                menus.add(optionArticulo);
            default:
                menus.add(optionPersona);
                menus.add(optionArticulo);

        }
        return new ArrayList<>(menus);
    }

}
