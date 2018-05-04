package com.example.example.ui;

import com.example.example.dominio.EntidadBase;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;

public abstract class BeanEditor<E extends EntidadBase> extends VerticalLayout {
    Class<E> type;
    Button save = new Button("Guardar", FontAwesome.SAVE);
    Button cancel = new Button("Cancelar");
    CssLayout actions = new CssLayout(save, cancel);
    Binder<E> binder;
    VerticalLayout mainLayout = new VerticalLayout();
    JpaRepository<E,Long> repository;
    private E bean;
    public  BeanEditor(Class<E> type, JpaRepository<E,Long> repository  )
    {
        this.repository = repository;
        this.type = type;
        binder  = new Binder<>(type);
        binder.bindInstanceFields(this);

        mainLayout.setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        save.addClickListener(e -> repository.save(bean));
        cancel.addClickListener(e -> editPersona(bean));
        setVisible(false);
    }


    private VerticalLayout generarCampos(Class<E> type)
    {
        Field[] fields = type.getDeclaredFields();
        String[] fieldsOrder = new String[fields.length];
        int n = 0;
        for ( Field field : fields  ) {
            if (field.getType() == String.class)
            {
                mainLayout.addComponent(new TextField(field.getName()));
            }
            fieldsOrder[n] = field.getName();
            n = n+1;
        }
        return this.mainLayout;
    }

    public interface ChangeHandler {

        void onChange();
    }

    public final void editPersona(E c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {

            bean = repository.findOne(c.getId());
        }
        else {
            bean = c;
        }
        cancel.setVisible(persisted);


        binder.setBean(bean);

        setVisible(true);


        save.focus();

    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(e -> h.onChange());
    }
}
