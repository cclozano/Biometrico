package com.example.example.ui;

import com.example.example.datos.ArticuloRepository;
import com.example.example.dominio.Articulo;
import com.example.example.infraestructura.DeviceController;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class ArticuloEditor extends VerticalLayout {

    private DeviceController controller;
	private final ArticuloRepository repository;

	private Articulo articulo;

	TextField codigo = new TextField("Codigo");
	TextField descripcion = new TextField("Descripcion");

	/* Action buttons */
	Button save = new Button("Guardar", FontAwesome.SAVE);
	Button cancel = new Button("Cancelar");
	Button delete = new Button("Eliminar", FontAwesome.TRASH_O);
	CssLayout actions = new CssLayout(save, cancel, delete);

	Binder<Articulo> binder = new Binder<>(Articulo.class);

	@Autowired
	public ArticuloEditor(ArticuloRepository repository, DeviceController controller) {
		this.repository = repository;
		addComponents(codigo, descripcion, actions);
		binder.bindInstanceFields(this);
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);


		save.addClickListener(e ->
				repository.save(articulo));
		delete.addClickListener(e -> repository.delete(articulo));
		cancel.addClickListener(e -> editArticulo(articulo));
		setVisible(false);
	}



	public interface ChangeHandler {

		void onChange();
	}

	public final void editArticulo(Articulo c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			articulo = repository.findOne(c.getId());
		}
		else {
			articulo = c;
		}
		cancel.setVisible(persisted);


		binder.setBean(articulo);

		setVisible(true);


		save.focus();

		codigo.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		save.addClickListener(e -> h.onChange());
		delete.addClickListener(e -> h.onChange());
	}

}
