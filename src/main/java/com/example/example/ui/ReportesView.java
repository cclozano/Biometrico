package com.example.example.ui;

import com.example.example.datos.ArticuloRepository;
import com.example.example.datos.AsignacionRepository;
import com.example.example.datos.PersonaRepository;
import com.example.example.ui.reportes.ReporteAgentesLayout;
import com.example.example.ui.reportes.ReporteArticulosLayout;
import com.example.example.ui.reportes.ReporteAsignacionesLayout;
import com.example.example.util.jasper.ReportsUtil;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by carlos on 20/07/17.
 */
@SpringView(name = ReportesView.VIEW_NAME)
public class ReportesView  extends VerticalLayout implements View {
    public static final String VIEW_NAME = "reportesView";

    TabSheet tabSheet = new TabSheet();


    ReporteAsignacionesLayout reporteGeneralLayout;
    ReporteAgentesLayout reporteAgentesLayout;
    ReporteArticulosLayout reporteArticulosLayout;

    @Autowired
    protected AsignacionRepository asignacionRepository;

    @Autowired
    protected PersonaRepository personaRepository;

    @Autowired
    protected ArticuloRepository articuloRepository;

    @Autowired
    ReportsUtil reportsUtil;

    @PostConstruct
    public void init()
    {
        this.setWidth("100%");
        this.setHeight("100%");
        this.tabSheet.setWidth("100%");
        this.tabSheet.setHeight("100%");
        this.addComponent(tabSheet);
        this.setMargin(false);
        reporteGeneralLayout = new ReporteAsignacionesLayout( asignacionRepository, reportsUtil);
        reporteGeneralLayout.setCaption("Reporte Asignaciones");
        tabSheet.addTab(reporteGeneralLayout);
        reporteAgentesLayout = new ReporteAgentesLayout( personaRepository, reportsUtil);
        reporteAgentesLayout.setCaption("Reporte Agentes");
        tabSheet.addTab(reporteAgentesLayout);
        reporteArticulosLayout = new ReporteArticulosLayout( articuloRepository, reportsUtil);
        reporteArticulosLayout.setCaption("Reporte Articulos");
        tabSheet.addTab(reporteArticulosLayout);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
