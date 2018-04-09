package com.example.example.ui.componentes;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by max on 11/07/17.
 */
public class ExitView extends VerticalLayout {

    public ExitView() {
        Label label = new Label("Sesion Terminada");
        addComponent(label);
        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
    }
}
