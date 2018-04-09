package com.example.example.ui.componentes;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class ToolBar extends HorizontalLayout {

    private HorizontalLayout contentDerecha;
    private HorizontalLayout contentIzquierda;

    public ToolBar(Component... components)
    {
        this();
        this.contentIzquierda.addComponents(components);
    }

    public HorizontalLayout getContentDerecha() {
        return contentDerecha;
    }

    public void setContentDerecha(HorizontalLayout contentDerecha) {
        this.contentDerecha = contentDerecha;
    }

    public HorizontalLayout getContentIzquierda() {
        return contentIzquierda;
    }

    public void setContentIzquierda(HorizontalLayout contentIzquierda) {
        this.contentIzquierda = contentIzquierda;
    }

    public ToolBar()
    {
        contentIzquierda = new HorizontalLayout();
        contentDerecha = new HorizontalLayout();
        this.setWidth("100%");
        this.setSpacing(true);
        this.addComponents(contentIzquierda,contentDerecha);
        this.setComponentAlignment(contentIzquierda, Alignment.BOTTOM_LEFT);
        this.setComponentAlignment(contentDerecha, Alignment.BOTTOM_RIGHT);

    }


    public Component addDerecha(Component component)
    {
        contentDerecha.addComponent(component);
        return component;
    }

    public void addIzquierda(Component component){

        contentIzquierda.addComponent(component);

    }

    public void clearDerecha()
    {
        contentDerecha.removeAllComponents();
    }

    public void clearIzquierda()
    {
        contentIzquierda.removeAllComponents();
    }

    public void removeIzquierda(Component component)
    {
        contentIzquierda.removeComponent(component);
    }
    public void removeDerecha(Component component)
    {
        contentDerecha.removeComponent(component);
    }

}