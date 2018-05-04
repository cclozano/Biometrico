package com.example.example.ui;

import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.infraestructura.DeviceController;
import com.example.example.ui.componentes.MyImageSource;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@SpringComponent
@UIScope
public class PersonaEditor extends VerticalLayout {

    private DeviceController controller;
	private final PersonaRepository repository;

	private Persona persona;

	TextField nombre = new TextField("Nombre Completo");
	TextField identificacion = new TextField("Identificacion");

	/* Action buttons */
	Button save = new Button("Guardar", FontAwesome.SAVE);
	Button cancel = new Button("Cancelar");
	//Button delete = new Button("Eliminar", FontAwesome.TRASH_O);
	CssLayout actions = new CssLayout(save, cancel);

	Binder<Persona> binder = new Binder<>(Persona.class);

	final Grid<Huella> huellas;
	HorizontalLayout mainLayout = new HorizontalLayout();
    VerticalLayout camposLayout = new VerticalLayout();
    VerticalLayout huellasLayout = new VerticalLayout();
    VerticalLayout imagenHuellaLayout = new VerticalLayout();
    Image imagen = new Image();


    Button addFinger = new Button("Agregar", FontAwesome.FIGHTER_JET);
    Button removeFinger = new Button("Eliminar ", FontAwesome.REMOVE);
    HorizontalLayout botonesHuellasLayout = new HorizontalLayout(addFinger,removeFinger);

    ProgressBar bar = new ProgressBar(0.0f);


	@Autowired
	public PersonaEditor(PersonaRepository repository, DeviceController controller) {
	    this.controller = controller;
        try {
            //controller.inicializarUSB();
        } catch (Exception e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
		this.repository = repository;
		this.huellas = new Grid<>(Huella.class);
		huellas.removeAllColumns();
		huellas.addColumn(Huella::getId).setCaption("ID");
		//huellas.addColumn(Huella::getCalidad).setCaption("Calidad");
        removeFinger.setEnabled(false);
        huellas.asSingleSelect().addValueChangeListener(e -> {
            if(e.getValue() != null)
            {
                removeFinger.setEnabled(true);
                actualizarImagen(e.getValue());
            }
            else {
                removeFinger.setEnabled(false);
                actualizarImagen(null);
            }
        });

        addFinger.addClickListener(clickEvent -> {
            try {

                bar.setIndeterminate(true);
                Huella huella = controller.capturar(5,50);
                bar.setIndeterminate(false);
                actualizarImagen(huella);
                huella.setPersona(this.persona);
                this.persona.getHuellas().add(huella);
                this.huellas.setItems(this.persona.getHuellas());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        removeFinger.addClickListener(clickEvent -> {
           Huella h = huellas.getSelectedItems().stream().findFirst().get();
           this.persona.getHuellas().remove(h);
           huellas.setItems(this.persona.getHuellas());
        });


        camposLayout.addComponents(nombre, identificacion, actions);
        bar.setCaption("% Calidad");
		imagenHuellaLayout.addComponents(imagen,bar);
		huellasLayout.addComponents(botonesHuellasLayout,huellas);
		mainLayout.addComponents(camposLayout,huellasLayout,imagenHuellaLayout);

		addComponent(mainLayout);
		binder.bindInstanceFields(this);


		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);


		save.addClickListener(e ->
				repository.save(persona));
		//delete.addClickListener(e -> repository.delete(persona));
		cancel.addClickListener(e -> editPersona(persona));
		setVisible(false);
	}

	public void actualizarImagen(Huella huella)
	{
	    if (huella == null)
        {
            this.imagenHuellaLayout.removeComponent(imagen);
            this.imagen = new Image("Huella",null);
            this.imagenHuellaLayout.addComponent(imagen);
            bar.setValue(0.0f);
            return;
        }
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
        //this.bar.setValue(l);
		this.imagenHuellaLayout.removeComponent(imagen);
		this.imagen = new Image("",resource);
		this.imagenHuellaLayout.addComponent(imagen);
		//huella.setResource(resource);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editPersona(Persona c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		actualizarImagen(null);
		final boolean persisted = c.getId() != null;
		if (persisted) {

			persona = repository.findOne(c.getId());
		}
		else {
			persona = c;
		}
		cancel.setVisible(persisted);


		binder.setBean(persona);
		this.huellas.setItems(persona.getHuellas());

		setVisible(true);


		save.focus();

		nombre.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		save.addClickListener(e -> h.onChange());
		//delete.addClickListener(e -> h.onChange());
	}

}
