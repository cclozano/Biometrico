package com.example.example.ui.componentes.test;

import com.example.example.servicios.ServicioUsuario;
import com.example.example.ui.componentes.ToolBar;
import com.vaadin.navigator.View;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BasicView<T> extends VerticalLayout implements View {

    protected Class<T> type;
    protected Grid<T> grid;
    protected ToolBar toolBar;


    @Autowired
    protected ServicioUsuario servicioUsuario;


    public BasicView(Class<T> type) {
        this.type = type;
        this.setMargin(true);
        this.setSpacing(true);
        this.grid = new Grid<>(type);
        this.setWidth("100%");
        this.setHeight("100%");
        this.toolBar = new ToolBar();
        this.addComponent(this.toolBar);
        this.addComponent(this.grid);
        this.setExpandRatio(this.grid,1);
        this.grid.setWidth("100%");
        this.grid.setHeight("100%");

    }


    protected abstract String getTitle();





}