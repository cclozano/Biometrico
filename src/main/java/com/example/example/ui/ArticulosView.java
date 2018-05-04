package com.example.example.ui;

import com.example.example.datos.ArticuloRepository;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Articulo;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.infraestructura.DeviceController;
import com.example.example.servicios.ServicioAsignacion;
import com.example.example.servicios.ServicioHuella;
import com.example.example.ui.componentes.MyImageSource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;


@SpringView(name = ArticulosView.VIEW_NAME)
public class ArticulosView extends BaseView<Articulo> {

    public static final String VIEW_NAME= "articulosView";

    private Grid<Articulo> articulosGrid;

    @Autowired
    ArticuloEditor articuloEditor;
    
    @Autowired
    ArticuloRepository articuloRepository;

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    DeviceController controller;

    @Autowired
    ServicioHuella servicioHuella;

    @Autowired
    ServicioAsignacion servicioAsignacion;

    private Huella huella;

    private Persona persona;

    @Autowired
    public ArticulosView(ArticuloRepository repository) {
        super(repository);
    }

    @Override
    public BeanEditor getEditor() {
        return null;
    }



    @Override
    public Articulo getSelected() {
        Set<Articulo> setCom = this.articulosGrid.getSelectedItems();
        if(setCom.isEmpty())return null;
        return setCom.stream().findFirst().get();
    }


    Image imagen;

    private boolean estaCapturando;
    private Label nombre = new Label();

    private Button asignarButton = new Button(FontAwesome.EXCHANGE);
    private Button desasociarButton = new Button(FontAwesome.BAN);
    @PostConstruct
    public void init()
    {
        try {
            //controller.inicializarUSB();
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
        asignarButton.setDescription("Asignar");
        this.toolBar.addDerecha(asignarButton);
        desasociarButton.setDescription("Desasociar");
        this.toolBar.addDerecha(desasociarButton);
        asignarButton.addClickListener(clickEvent -> {
            Window aw = new Window("Asignar Articulo");
            ProgressBar progressBar = new ProgressBar();
            VerticalLayout imagenHuellaLayout = new VerticalLayout();
            imagen = new Image();
            progressBar.setIndeterminate(true);
            aw.setContent(progressBar);
            aw.setModal(true);
            UI.getCurrent().addWindow(aw);
            aw.addFocusListener(fl -> {
                if(!estaCapturando){
                try {
                    estaCapturando = true;
                    huella = controller.capturar(5,50);
                    if (huella == null) return;
                    BufferedImage buff = null;
                    try {
                        buff = ImageIO.read(new ByteArrayInputStream(huella.getImageJpg()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StreamResource.StreamSource imagesource  = new MyImageSource(buff);
                    StreamResource resource =
                            new StreamResource(imagesource, "myimage.png");
                    //float l = huella.getcalidad();
                    progressBar.setIndeterminate(false);
                    //progressBar.setValue(l);
                    progressBar.setCaption("% Calidad");
                    imagen = new Image("",resource);
                    imagenHuellaLayout.addComponents(progressBar, imagen,nombre,new Button("Asignar",e -> {
                        try {
                            servicioAsignacion.asignar(selectedBean,persona);
                            Notification.show("se asigno " + selectedBean.getDescripcion() + " a " + persona.getNombre(), Notification.Type.ERROR_MESSAGE);
                            aw.close();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Notification.show(e1.getMessage(), Notification.Type.ERROR_MESSAGE);
                            aw.close();
                        }
                    }));
                    aw.setContent(imagenHuellaLayout);
                    aw.setPositionY(0);
                    persona = servicioHuella.encontrarPersona(huella);
                    if(persona!=null)
                    {
                        nombre.setValue("" + persona.getIdentificacion() + " - " + persona.getNombre());
                    }
                } catch (Exception e) {
                    aw.close();
                    e.printStackTrace();
                    Notification.show(e.getMessage());
                }
                }
            });

            aw.addCloseListener( l -> {
                actualizarGrid();
                estaCapturando = false;} );
        });

        desasociarButton.addClickListener(clickEvent -> {
            Window aw = new Window("Desasociar Articulo");
            ProgressBar progressBar = new ProgressBar();
            VerticalLayout imagenHuellaLayout = new VerticalLayout();
            imagen = new Image();
            progressBar.setIndeterminate(true);
            aw.setContent(progressBar);
            aw.setModal(true);
            UI.getCurrent().addWindow(aw);
            aw.addFocusListener(fl -> {
                if(!estaCapturando){
                    try {
                        estaCapturando = true;
                        huella = controller.capturar(5,50);
                        if (huella == null) return;
                        BufferedImage buff = null;
                        try {
                            buff = ImageIO.read(new ByteArrayInputStream(huella.getImageJpg()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        StreamResource.StreamSource imagesource  = new MyImageSource(buff);
                        StreamResource resource =
                                new StreamResource(imagesource, "myimage.png");
                        //float l = huella.getcalidad();
                        progressBar.setIndeterminate(false);
                        //progressBar.setValue(l);
                        progressBar.setCaption("% Calidad");
                        imagen = new Image("",resource);
                        persona = servicioHuella.encontrarPersona(huella);
                        imagenHuellaLayout.addComponents(progressBar, imagen,nombre,new Button("Desasociar",e -> {
                            try {
                                if (persona!=null && selectedBean.getAsignacion()!=null && persona.getId() == selectedBean.getAsignacion().getPersona().getId())
                                {
                                    servicioAsignacion.desasociar(selectedBean.getAsignacion());
                                    Notification.show("se desasocio " + selectedBean.getDescripcion() + " de " + persona.getNombre(), Notification.Type.ERROR_MESSAGE);
                                }
                                else if(persona== null){
                                    Notification.show("No se identifica la huella ", Notification.Type.ERROR_MESSAGE);
                                }
                                else {
                                    Notification.show("La huella ingresada corresponde a " + persona.getNombre() + " -- No es la asignada!", Notification.Type.ERROR_MESSAGE);
                                }
                                aw.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                Notification.show(e1.getMessage(), Notification.Type.ERROR_MESSAGE);
                                aw.close();
                            }
                        }));
                        aw.setContent(imagenHuellaLayout);
                        aw.setPositionY(0);
                        if(persona!=null)
                        {
                            nombre.setValue("" + persona.getIdentificacion() + " - " + persona.getNombre());
                        }
                    } catch (Exception e) {
                        aw.close();
                        e.printStackTrace();
                        Notification.show(e.getMessage());
                    }
                }
            });

            aw.addCloseListener( l -> {
                actualizarGrid();
                estaCapturando = false;} );
        });



        articuloEditor.setChangeHandler(() -> {
            if(editWindow!= null)editWindow.close();
            actualizarGrid();
        });

        asignarButton.setEnabled(false);
        dataGrid.asSingleSelect().addValueChangeListener(e ->
                {
                    asignarButton.setEnabled(e.getValue() != null && e.getValue().getAsignacion()== null);
                    desasociarButton.setEnabled(e.getValue() != null && e.getValue().getAsignacion()!= null);
                });
    }

    @Override
    public Grid<Articulo> getDataGrid() {
        if(articulosGrid ==null)
        {
            articulosGrid = new Grid<>();
            Grid.Column idColumn = articulosGrid.addColumn(fac -> fac.getCodigo()).setCaption("Identificacion");
            Grid.Column nombreColumn = articulosGrid.addColumn(fac -> fac.getDescripcion()).setCaption("Nombre");
            HeaderRow filterRow = articulosGrid.appendHeaderRow();
            articulosGrid.addColumn(Articulo::getPersona).setCaption("Asignado");

            TextField nombreFilter = new TextField();
            nombreFilter.setWidth("100%");
            filterRow.getCell(nombreColumn).setComponent(nombreFilter);
            nombreFilter.addValueChangeListener(valueChangeEvent -> {
                dataProvider.addFilter(articuloEntity -> {
                    if(nombreFilter.getValue() == null)return true;
                    return  articuloEntity.getDescripcion()
                            .toLowerCase()
                            .contains(nombreFilter.getValue().toLowerCase());
                });
            });

            TextField identificacionFilter = new TextField();
            identificacionFilter.setWidth("100%");
            filterRow.getCell(idColumn).setComponent(identificacionFilter);
            identificacionFilter.addValueChangeListener(valueChangeEvent -> {
                dataProvider.addFilter(articuloEntity -> {
                    if(identificacionFilter.getValue() == null)return true;
                    return  articuloEntity.getCodigo()
                            .toLowerCase()
                            .contains(identificacionFilter.getValue().toLowerCase());
                });
            });
        return articulosGrid;
    }
    return  null;
    }

    @Override
    public List<Articulo> getList() {
        return articuloRepository.findAll();
    }

    @Override
    public VerticalLayout getForm(Articulo bean) {
        articuloEditor.editArticulo(bean);
        return articuloEditor;
    }

    @Override
    public Articulo getNew() {
        return new Articulo();
    }



}
