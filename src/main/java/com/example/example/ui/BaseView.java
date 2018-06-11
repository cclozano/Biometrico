package com.example.example.ui;

import com.example.example.dominio.EntidadBase;
import com.example.example.dominio.Persona;
import com.example.example.servicios.ServicioUsuario;
import com.example.example.ui.componentes.ExitView;
import com.example.example.ui.componentes.ToolBar;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.FooterRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.List;

public abstract class BaseView<E extends EntidadBase> extends VerticalLayout implements View {



    protected FooterRow footer;

    protected Grid<E> dataGrid;

    protected ListDataProvider<E> dataProvider;

    protected ToolBar toolBar = new ToolBar();
    protected HorizontalLayout busquedaLayout;

    protected  Window editWindow;

    @Autowired
    protected ServicioUsuario servicioUsuario;

    protected E selectedBean;

    JpaRepository<E,Long> repository;

    public BaseView(JpaRepository<E,Long> repository)
    {
        this.repository = repository;
        this.dataGrid = getDataGrid();
        this.setWidth("100%");
        this.setHeight("100%");
        this.dataGrid.setWidth("100%");
        this.dataGrid.setHeight("100%");
        this.addComponents( toolBar,this.dataGrid);
        this.setExpandRatio(this.dataGrid,1);
        footer = dataGrid.appendFooterRow();

        toolBar.addDerecha(getNewButton());
        toolBar.addDerecha(getEditButton());
        toolBar.addDerecha(getEliminarButton());
        busquedaLayout = new HorizontalLayout(getActualizarButton());
        busquedaLayout.setComponentAlignment(getActualizarButton(), Alignment.BOTTOM_LEFT);
        busquedaLayout.setSpacing(true);
        toolBar.addIzquierda(busquedaLayout);
        getEliminarButton().setEnabled(false);
        getEditButton().setEnabled(false);
        dataGrid.asSingleSelect().addValueChangeListener(e -> {

            selectedBean = e.getValue();
            if (selectedBean != null){
                getEliminarButton().setEnabled(true);
                getEditButton().setEnabled(true);
            }else{
                getEliminarButton().setEnabled(false);
                getEditButton().setEnabled(false);
            }
        });
    }

    private Button actualizarButton;
    public Button getActualizarButton()
    {
        if(actualizarButton ==null)
        {
           this.actualizarButton = new Button(FontAwesome.REFRESH) ;
           this.actualizarButton.addClickListener(clickEvent -> {
               List lista = getList();
               Grid.Column columna = this.dataGrid.getColumns().stream().findFirst().get();
               footer.getCell(columna).setText(lista.size() + "");
               this.dataProvider =  DataProvider.ofCollection(lista);
               this.dataGrid.setDataProvider(this.dataProvider);
           });
        }
        return actualizarButton;
    }

    protected void actualizarGrid()
    {
        this.dataProvider =  DataProvider.ofCollection(getList());
        this.dataGrid.setDataProvider(this.dataProvider);
    }



    private Button editButton;
    protected Button getEditButton()
    {
        if(editButton ==null)
        {
            editButton = new Button(FontAwesome.EDIT);
            editButton.setDescription("Editar");
            editButton.addClickListener(clickEvent -> {
                editWindow = new Window();
                editWindow.setContent(getForm(selectedBean));
                UI.getCurrent().addWindow(editWindow);
                editWindow.setModal(true);
            });
        }
        return editButton;
    }

    private Button eliminarButton;
    public Button getEliminarButton()
    {
        if(eliminarButton ==null)
        {
            eliminarButton = new Button(FontAwesome.REMOVE);
            eliminarButton.addClickListener(clickEvent -> {
                ConfirmDialog.show(UI.getCurrent(),
                        "Confirmar",
                        "Desea eliminar el registro?",
                        "Aceptar",
                        "Cancelar",
                        confirmDialog -> {
                            if(confirmDialog.isConfirmed())
                            {
                                repository.delete(selectedBean);
                                actualizarGrid();
                            }
                        });
        });
            eliminarButton.setDescription("Eliminar");
        }
        return this.eliminarButton;
    }

    public abstract BeanEditor getEditor();

    private Button newButton;
    public Button getNewButton()
    {
        if(newButton ==null)
        {
            newButton = new Button(FontAwesome.FILE);
            newButton.setDescription("Crear Nuevo");
            newButton.addClickListener(clickEvent ->{
                editWindow = new Window();
                editWindow.setContent(getForm(getNew()));
                UI.getCurrent().addWindow(editWindow);
                editWindow.setModal(true);
            });
        }
        return newButton;
    }

    public abstract E getSelected();



    /*public void mostrarEstadoWindows(E comprobante)
    {

        Window subWindow = new Window("Registros");
        VerticalLayout verContent =new VerticalLayout();
        subWindow.setContent(verContent);
        verContent.addComponent(new Label());

        HorizontalLayout contenido = new HorizontalLayout();
        verContent.addComponent(contenido);
        TextField tfNombre = new TextField("Nombre: ");
        tfNombre.setValue(p.getNombre());
        contenido.addComponentsAndExpand(tfNombre);
        TextField tfI = new TextField("C.I: ");
        tfI.setValue(p.getIdentificacion());
        contenido.addComponentsAndExpand(tfI);

        Huella h = huellaRespository.findAll().stream().findFirst().get();
        Grid<Huella> gridHuella = new Grid<>();
        gridHuella.setItems(h);
        gridHuella.addColumn(Huella::getId).setCaption("ID");
        gridHuella.addColumn(Huella::getPersona).setCaption("Persona");
        gridHuella.addColumn(Huella::getQuality).setCaption("Calidad");

        verContent.addComponent(gridHuella);
        //verContent.addComponent(new Image("Titulo imagen",h.getResource()));
        subWindow.setContent(verContent);

        subWindow.center();
        UI.getCurrent().addWindow(subWindow);
    }*/

    public abstract Grid<E> getDataGrid();
    public abstract List<E> getList();
    public abstract VerticalLayout getForm(E bean);
    public abstract E getNew();

    private void updateGrid()
    {
        List lista = getList();
        this.dataProvider =  DataProvider.ofCollection(lista);
        Grid.Column columna = this.dataGrid.getColumns().stream().findFirst().get();
        footer.getCell(columna).setText(lista.size() + "");
        this.dataGrid.setDataProvider(this.dataProvider);
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent var1)
    {
        updateGrid();
    }

}