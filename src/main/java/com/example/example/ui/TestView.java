package com.example.example.ui;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.applet.AppletIntegration;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringView(name = TestView.VIEW_NAME)
public class TestView extends VerticalLayout implements View {

    public static final String VIEW_NAME= "testView";

    @PostConstruct
    public void init() {
        /*AppletIntegration applet = new AppletIntegration() {
            private static final long serialVersionUID = 1L;
            @Override
            public void attach() {
                setAppletArchives(Arrays.asList(new String[]{"TestApplet.class"}));
                setCodebase("/home/clozano/IdeaProjects/Biometrico/target/classes/com/example/example/config/under/");
                setAppletClass("TestApplet.class");
                setWidth("800px");
                setHeight("500px");
            }
        };*/

        com.example.example.ui.applet.integration.AppletIntegration app = new com.example.example.ui.applet.integration.AppletIntegration();

        this.addComponent(app);
    }

}
