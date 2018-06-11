package com.example.example.ui.reportes;
import com.example.example.datos.PersonaRepository;
import com.example.example.dominio.Persona;
import com.example.example.util.jasper.ReportesBaseLayout;
import com.example.example.util.jasper.ReportsUtil;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReporteAgentesLayout extends ReportesBaseLayout<Persona> {


    DateField fechaInicio = new DateField("Fecha Inicio");
    DateField fechaFin= new DateField("Fecha Fin");
    Button buscar = new Button(FontAwesome.REFRESH);

    PersonaRepository repositorio;

    public ReporteAgentesLayout(PersonaRepository repository, ReportsUtil rp) {
        super(rp);
        repositorio = repository;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        fechaInicio.setValue(LocalDate.now());
        fechaFin.setValue(LocalDate.now());

        toolBar.setExpandRatio(toolBar.getContentDerecha(),1);
        buscar.addClickListener(event -> {
            List lista = getList();
            /*Grid.Column columna = this.dataGrid.getColumns().stream().findFirst().get();
            footer.getCell(columna).setText(lista.size() + "");*/
            this.dataProvider =  DataProvider.ofCollection(lista);
            this.dataGrid.setDataProvider(this.dataProvider);
        });
        toolBar.getContentIzquierda().addComponents(buscar);
        toolBar.getContentIzquierda().setComponentAlignment(buscar, Alignment.BOTTOM_RIGHT);
        buscar.click();
    }

    @Override
    public void mostrarEstadoWindows(Persona bean) {
        
    }

    @Override
    public Grid<Persona> getDataGrid() {
        if(grid ==null)
        {
            grid = new Grid<>();
            Grid.Column personaColumn = grid.addColumn(comp -> comp.getIdentificacion()).setCaption("Identificacion");
            Grid.Column articuloColumn = grid.addColumn(comp -> comp.getNombre()).setCaption("Nombre");
            Grid.Column fechaColumn =  grid.addColumn(comprobante -> comprobante.getApellidoPaterno()).setCaption("Apellido");

            HeaderRow filterRow = grid.appendHeaderRow();
            TextField puntoEmisionFilter = new TextField();
            puntoEmisionFilter.setWidth("100%");
            filterRow.getCell(personaColumn).setComponent(puntoEmisionFilter);
            puntoEmisionFilter.addValueChangeListener(valueChangeEvent -> {
                dataProvider.addFilter(comprobante -> {
                    if(puntoEmisionFilter.getValue() == null)return true;
                    return  comprobante.getIdentificacion().toString()
                            .toLowerCase()
                            .contains(puntoEmisionFilter.getValue().toLowerCase());
                });
            });
            TextField numeroFilter = new TextField();
            numeroFilter.setWidth("100%");
            filterRow.getCell(articuloColumn).setComponent(numeroFilter);
            numeroFilter.addValueChangeListener(valueChangeEvent -> {
                dataProvider.addFilter(comprobante -> {
                    if(numeroFilter.getValue() == null)return true;
                    return  comprobante.getNombre().toString()
                            .toLowerCase()
                            .contains(numeroFilter.getValue().toLowerCase());
                });
            });

        }
        return grid;
    }

    public List<Persona> getList() {
        return  repositorio.findAll();
    }





    public void mostrarReporte() {
        Date fInicio = Date.from(fechaInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fFin = Date.from(fechaFin.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        String fIni = "*", fFi = "*";
        if(fInicio != null && fFin != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fIni = sdf.format(fInicio);
            fFi = sdf.format(fFin);
        }
        String subtitulo = "Desde: " + fIni + "  Hasta: " + fFi ;
        String nombreReporte = "reporteAgentes";
        Collection<Persona> listaGrid = this.dataProvider.fetch(new Query<>()).collect(Collectors.toList());
        mostrarReporte(listaGrid,
                    "Reporte de Asignaciones: " ,
                    subtitulo, nombreReporte, "agentes");

    }

}
