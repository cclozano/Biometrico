package com.example.example.ui;

import com.example.example.datos.HuellaRespository;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Huella;
import com.example.example.dominio.Persona;
import com.example.example.infraestructura.DeviceControllerImp;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


public class VaadinUI extends UI {

	private final PersonaRepository repo;

	private final PersonaEditor editor;

	final Grid<Persona> grid;
	final Grid<Huella> gridHuella;

	final TextField filter;

	private final Button addNewBtn;
	private final Button verificarButton;
	private final Button verInfo;
	private Persona personaSeleccionada;
	private Huella huellaPersona;

	@Autowired
	PersonaRepository personaRepository;

    @Autowired
    HuellaRespository huellaRespository;

	@Autowired
    DeviceControllerImp controller;

	@Autowired
	public VaadinUI(PersonaRepository repo, PersonaEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Persona.class);
		this.gridHuella = new Grid<>(Huella.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("Nuevo", FontAwesome.PLUS);
        this.verificarButton = new Button("Identificar", FontAwesome.CHECK);
        this.verInfo = new Button("" , FontAwesome.INFO);
	}



	@Override
	protected void init(VaadinRequest request) {

		verInfo.setEnabled(true);
		verInfo.addClickListener(clickEvent -> {
			Persona p = personaRepository.findAll().stream().findFirst().get();
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

			Grid<Persona> grid = new Grid<>();
			grid.setItems(p);
			grid.addColumn(Persona::getId).setCaption("ID");
			grid.addColumn(Persona::getNombre).setCaption("Nombre");
			grid.addColumn(Persona::getHuellas).setCaption("Huellas");
			grid.addColumn(Persona::getIdentificacion).setCaption("Identificacion");


			verContent.addComponents(grid);

			Huella h = huellaRespository.findAll().stream().findFirst().get();
			Grid<Huella> gridHuella = new Grid<>();
			gridHuella.setItems(h);
			gridHuella.addColumn(Huella::getId).setCaption("ID");
			gridHuella.addColumn(Huella::getPersona).setCaption("Persona");
			//gridHuella.addColumn(new Image(Huella::getImageBuffer));
			/*Grid.Column<Huella, ThemeResource> imageColumn = grid.addColumn(
					huella -> new ThemeResource("img/"+Huella::getImageJpg+".jpg"),
					new ImageRenderer());*/
			gridHuella.addColumn(Huella::getcalidad).setCaption("Calidad");


			verContent.addComponent(gridHuella);
			//verContent.addComponent(new Image("Titulo imagen",h.getResource()));
			subWindow.setContent(verContent);

			subWindow.center();
			addWindow(subWindow);

		});

		verificarButton.addClickListener(clickEvent -> {

			//UI.getCurrent().addWindow(subWindow);

            /*Collection<Huella> huellas = huellaRespository.findAll();
            try {
                Huella huellaNueva = controller.capturar(5,50);
                Huella h = controller.verificarHuella(5,huellaNueva,huellas);
                if (h!= null)
                {
                    Notification.show("Persona: " + h.getPersona(), Notification.Type.ERROR_MESSAGE);
                }
                else {
                    Notification.show("Persona no encontrada ", Notification.Type.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
		});

		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn,verificarButton, verInfo);
		VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
		VerticalLayout principal = new VerticalLayout(mainLayout);
		setContent(principal);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "codigo", "descripcion");

		filter.setPlaceholder("filtrar por codigo");

		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listPersonas(e.getValue()));


		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editPersona(e.getValue());

			personaSeleccionada = e.getValue();
			if (personaSeleccionada != null){
				verInfo.setEnabled(true);
			}else{
				verInfo.setEnabled(false);
			}
		});

		addNewBtn.addClickListener(e -> editor.editPersona(new Persona("", "")));

		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listPersonas(filter.getValue());
		});

		listPersonas(null);
	}

	void listPersonas(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(repo.findAll());
		}
		else {
			grid.setItems(repo.findByNombreStartsWithIgnoreCase(filterText));
		}
	}


}
