package com.example.example.ui;

import com.example.example.infraestructura.DeviceController;
import com.example.example.infraestructura.InfraestructuraException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;


@SpringView(name = ConfigView.VIEW_NAME)
public class ConfigView extends VerticalLayout implements View {

    public static final String VIEW_NAME= "configView";

    @Autowired
    DeviceController controller;

    Button ledButton = new Button(FontAwesome.LIGHTBULB_O);

    public ConfigView()
    {
        /*this.addComponent(ledButton);

        ledButton.addClickListener(clickEvent -> {
            try {
                controller.inicializarUSB();
                controller.led();
            } catch (InfraestructuraException e) {
                e.printStackTrace();
                Notification.show( "Error - " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });*/
    }

}
