package com.example.example.ui.componentes.test;

import com.vaadin.ui.*;

public class BaseWindows extends Window {

    private VerticalLayout maintLayaut = new VerticalLayout();
    private Button aceptar ;
    private Button cancelar ;

    public BaseWindows()
    {
        this.setWidth("60%");
        this.setModal(true);
        this.center();

        maintLayaut.setWidth("100%");
        maintLayaut.setHeight("-1px");
        maintLayaut.setMargin(true);
        maintLayaut.setSpacing(true);
        this.aceptar =  new Button("Aceptar");
        this.cancelar =  new Button("Cancelar", clickEvent -> UI.getCurrent().removeWindow(this));
    }

    public BaseWindows(String title)
    {
        this();
        setCaption(title);
    }


    public void setContenido(Component component)
    {
        HorizontalLayout hl = new HorizontalLayout(this.aceptar,this.cancelar);
        hl.setSpacing(true);
        HorizontalLayout footBar = new HorizontalLayout(hl);
        footBar.setComponentAlignment(hl, Alignment.BOTTOM_RIGHT);
        footBar.setWidth("100%");
        this.maintLayaut.addComponents(component,footBar);
        super.setContent(this.maintLayaut);
    }

    public void addAceptarListener(Button.ClickListener listener)
    {
        aceptar.addClickListener(listener);
    }

    public void addCancelarListener(Button.ClickListener listener)
    {
        cancelar.addClickListener(listener);
    }

}
