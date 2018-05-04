package com.example.example.ui;

import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Asignacion;
import com.example.example.dominio.Persona;
import com.example.example.servicios.ServicioAsignacion;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;


@SpringView(name = PersonaView.VIEW_NAME)
public class PersonaView extends BaseView<Persona> {

    public static final String VIEW_NAME= "personaView";

    private Grid<Persona> personasGrid;

    @Autowired
    PersonaEditor personaEditor;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    ServicioAsignacion servicioAsignacion;

    private Button verInfo = new Button("",FontAwesome.EYE);

    Asignacion asignacion = null;
    @Autowired
    public PersonaView(PersonaRepository repository) {
        super(repository);
        toolBar.addDerecha(verInfo);
        verInfo.addClickListener(clickEvent -> {
           Window w = new Window();
           Button desasociarButton = new Button("Desasociar",FontAwesome.UNDO);

           desasociarButton.setEnabled(false);
            TextField tfNombre = new TextField("Nombre: ",selectedBean.getNombre());
            tfNombre.setEnabled(false);
            TextField tfI = new TextField("C.I: ",selectedBean.getIdentificacion());
            tfI.setEnabled(false);
            HorizontalLayout contenido = new HorizontalLayout(tfNombre,tfI);
            //contenido.setComponentAlignment(desasociarButton,Alignment.BOTTOM_LEFT);
            Grid<Asignacion> grid = new Grid<>(Asignacion.class);
            grid.setItems(selectedBean.getAsignacions());
            grid.removeAllColumns();
            grid.addColumn(Asignacion::getArticulo).setCaption("Articulo");
            grid.addColumn(Asignacion::getFechaAsignacion).setCaption("Fecha Asignacion");
            grid.addColumn(Asignacion::getFechaEliminacion).setCaption("Fecha Desasociado");
            grid.addColumn(Asignacion::isActivo).setCaption("Asignado");
            VerticalLayout vl = new VerticalLayout(contenido,grid);

            grid.asSingleSelect().addValueChangeListener(e ->
                    {
                        desasociarButton.setEnabled(e.getValue() != null && e.getValue().isActivo());
                        asignacion = e.getValue();
                    });
            desasociarButton.addClickListener(clickEvent1 -> {
                ConfirmDialog.show(UI.getCurrent(),
                        "Confirmar",
                        "Desea quitar la asignacion?",
                        "Aceptar",
                        "Cancelar",
                        confirmDialog -> {
                            if(confirmDialog.isConfirmed())
                            {
                                servicioAsignacion.desasociar(asignacion);
                                grid.setItems(selectedBean.getAsignacions());
                            }
                        });
            });
           w.setContent(vl);
           w.setModal(true);
           w.center();
           UI.getCurrent().addWindow(w);
        });
        verInfo.setEnabled(false);
        dataGrid.asSingleSelect().addValueChangeListener(e ->
                verInfo.setEnabled(e.getValue() != null));
    }

    @Override
    public BeanEditor getEditor() {
        return null;
    }

    @Override
    public Persona getSelected() {
        Set<Persona> setCom = this.personasGrid.getSelectedItems();
        if(setCom.isEmpty())return null;
        return setCom.stream().findFirst().get();
    }

    @PostConstruct
    public void init()
    {
        personaEditor.setChangeHandler(() -> {
            if(editWindow!= null)editWindow.close();
            actualizarGrid();
        });
    }


    @Override
    public Grid<Persona> getDataGrid() {
        if(personasGrid ==null)
        {
            personasGrid = new Grid<>();
            Grid.Column idColumn = personasGrid.addColumn(fac -> fac.getIdentificacion()).setCaption("Identificacion");
            Grid.Column nombreColumn = personasGrid.addColumn(fac -> fac.getNombre()).setCaption("Nombre");
            


            HeaderRow filterRow = personasGrid.appendHeaderRow();

            TextField nombreFilter = new TextField();
            nombreFilter.setWidth("100%");
            filterRow.getCell(nombreColumn).setComponent(nombreFilter);
            nombreFilter.addValueChangeListener(valueChangeEvent -> {
                dataProvider.addFilter(personaEntity -> {
                    if(nombreFilter.getValue() == null)return true;
                    return  personaEntity.getNombre()
                            .toLowerCase()
                            .contains(nombreFilter.getValue().toLowerCase());
                });
            });

            TextField identificacionFilter = new TextField();
            identificacionFilter.setWidth("100%");
            filterRow.getCell(idColumn).setComponent(identificacionFilter);
            identificacionFilter.addValueChangeListener(valueChangeEvent -> {
                dataProvider.addFilter(personaEntity -> {
                    if(identificacionFilter.getValue() == null)return true;
                    return  personaEntity.getIdentificacion()
                            .toLowerCase()
                            .contains(identificacionFilter.getValue().toLowerCase());
                });
            });
        return personasGrid;
    }
    return  null;
    }

    @Override
    public List<Persona> getList() {
        return personaRepository.findAll();
    }

    @Override
    public VerticalLayout getForm(Persona bean) {
        personaEditor.editPersona(bean);
        return personaEditor;
    }

    @Override
    public Persona getNew() {
        return new Persona();
    }


}
