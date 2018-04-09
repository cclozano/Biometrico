package com.example.example.ui.componentes.test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;

public class EditStartEvent extends Component.Event {

    public EditStartEvent(Component source) {
        super(source);
    }

    public Grid getGrid() {
        return (Grid) getSource();
    }
}
