package com.example.example.ui.componentes.test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;

public class EditEndEvent extends Component.Event {

    public EditEndEvent(Component source) {
        super(source);
    }

    public Grid getGrid() {
        return (Grid) getSource();
    }
}