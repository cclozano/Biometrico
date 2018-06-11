package com.example.example.ui.reportes;

import com.example.example.datos.AsignacionRepository;
import com.example.example.dominio.Asignacion;
import com.example.example.util.jasper.Formato;
import com.example.example.util.jasper.ReportesBaseLayout;
import com.example.example.util.jasper.ReportsUtil;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Extension;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ReporteAsignacionesLayout extends ReportesBaseLayout<Asignacion> {


    DateField fechaInicio = new DateField("Fecha Inicio");
    DateField fechaFin= new DateField("Fecha Fin");
    Button buscar = new Button(FontAwesome.REFRESH);

    AsignacionRepository repositorio;
    Button reciboButton = new Button(FontAwesome.PENCIL);

    public ReporteAsignacionesLayout(AsignacionRepository repository, ReportsUtil rp) {
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


        this.toolBar.addDerecha(reciboButton);
        reciboButton.setEnabled(false);
        this.grid.addSelectionListener(selectionEvent -> {
            Asignacion asig = getSelected();
            reciboButton.setEnabled(asig!=null);
        });
        reciboButton.addClickListener(clickEvent -> {
            Asignacion asig = getSelected();
            mostrarReporte(Arrays.asList(asig,asig),
                    "Comprobante de asignacion: " ,
                    "", "comprobanteAsignacion", "comp");
        });
    }

    @Override
    public void mostrarEstadoWindows(Asignacion bean) {

    }

    @Override
    public Grid<Asignacion> getDataGrid() {
        if(grid ==null)
        {
            grid = new Grid<>();
            Grid.Column personaColumn = grid.addColumn(comp -> comp.getPersona()).setCaption("Persona");
            Grid.Column articuloColumn = grid.addColumn(comp -> comp.getArticulo()).setCaption("Articulo");
            Grid.Column fechaColumn =  grid.addColumn(comprobante -> comprobante.getFechaAsignacion()).setCaption("Fecha");

            HeaderRow filterRow = grid.appendHeaderRow();
            TextField puntoEmisionFilter = new TextField();
            puntoEmisionFilter.setWidth("100%");
            filterRow.getCell(personaColumn).setComponent(puntoEmisionFilter);
            puntoEmisionFilter.addValueChangeListener(valueChangeEvent -> {
                dataProvider.addFilter(comprobante -> {
                    if(puntoEmisionFilter.getValue() == null)return true;
                    return  comprobante.getPersona().toString()
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
                    return  comprobante.getArticulo().toString()
                            .toLowerCase()
                            .contains(numeroFilter.getValue().toLowerCase());
                });
            });

        }
        return grid;
    }

    public List<Asignacion> getList() {
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
        String nombreReporte = "reporteAsignaciones";
        Collection<Asignacion> listaGrid = this.dataProvider.fetch(new Query<>()).collect(Collectors.toList());
        mostrarReporte(listaGrid,
                    "Reporte de Asignaciones: " ,
                    subtitulo, nombreReporte, "asignaciones");

    }

}
