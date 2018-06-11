package com.example.example.util.jasper;

import com.example.example.ui.componentes.ToolBar;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.FooterRow;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class ReportesBaseLayout<T> extends VerticalLayout{

    protected SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


    protected FooterRow footer;

    protected Grid<T> dataGrid;

    protected ListDataProvider<T> dataProvider;

    protected ToolBar toolBar = new ToolBar();

    protected Grid<T> grid;

    protected ReportsUtil reportsUtil;


    public abstract  Grid<T> getDataGrid();
    public abstract List<T> getList();
    public abstract void mostrarReporte();

    public ReportesBaseLayout(ReportsUtil rp) {
        reportsUtil = rp;
        this.dataGrid = getDataGrid();
        this.setWidth("100%");
        this.setHeight("100%");
        this.dataGrid.setWidth("100%");
        this.dataGrid.setHeight("100%");
        this.addComponents( toolBar,this.dataGrid);
        this.setExpandRatio(this.dataGrid,1);
        footer = dataGrid.appendFooterRow();
        toolBar.addDerecha(getReporteButton());
        toolBar.addDerecha(getVerEstadoButton());
        getVerEstadoButton().setEnabled(false);
        this.getDataGrid().addSelectionListener(selectionEvent -> {
            boolean enable = !this.getDataGrid().getSelectedItems().isEmpty();
            getVerEstadoButton().setEnabled(enable);
        });

    }

    public void mostrarReporte(Collection dataSource,
                               String tituloReporte,
                               String subtitulo,
                               String nombreReportFile,
                               String nombreArchivoSalida)
    {

        if (dataSource == null || dataSource.size() == 0) {
            Notification.show("No se encontraron datos para el reporte!");
            return;
        }
        Window window = new Window("Mostrar Reporte");
        window.setModal(true);
        Button pdfButton = new Button("Pdf");
        pdfButton.setIcon(FontAwesome.FILE_PDF_O);
        Button excelButton = new Button("Excel");
        excelButton.setIcon(FontAwesome.FILE_EXCEL_O);
        HorizontalLayout hl = new HorizontalLayout(pdfButton,excelButton);
        hl.setMargin(true);
        hl.setSpacing(true);
        window.setContent(hl);
        Map datosReporte = new HashMap();

        datosReporte.put("P_TITULO", tituloReporte);
        datosReporte.put("P_SUBTITULO", subtitulo);
        datosReporte.put("LOGO_URL", getPathLogo());
        datosReporte.put("PATH", reportsUtil.getReportsPath());

        BrowserWindowOpener openerExcel =
                new BrowserWindowOpener(reportsUtil.prepareForExportReport(
                        nombreReportFile+"_xls",
                        nombreArchivoSalida,
                        Formato.EXCEL,
                        dataSource,
                        datosReporte));
        BrowserWindowOpener openerPdf =
                new BrowserWindowOpener(reportsUtil.prepareForExportReport(
                        nombreReportFile,
                        nombreArchivoSalida,
                        Formato.PDF,
                        dataSource,
                        datosReporte));

        openerExcel.extend(excelButton);
        openerPdf.extend(pdfButton);
        UI.getCurrent().addWindow(window);
    }

    public String getPathLogo()
    {
        return reportsUtil.getReportsPath();
    }

    private Button verEstadoButtion;

    public Button getVerEstadoButton()
    {
        if(verEstadoButtion==null)
        {
            verEstadoButtion = new Button(FontAwesome.EYE);
            verEstadoButtion.setDescription("Ver Estado");
            verEstadoButtion.addClickListener(clickEvent ->{
                T comp = getSelected();
                mostrarEstadoWindows(comp);
            });
        }
        return verEstadoButtion;
    }

    private Button reporteButton;
    protected Button getReporteButton()
    {
        if(reporteButton == null)
        {
            this.reporteButton = new Button(FontAwesome.PRINT);
            this.reporteButton.setDescription("Mostrar reporte");
            this.reporteButton.addClickListener(event -> mostrarReporte());
        }
        return this.reporteButton;
    }



    protected void updateGrid()
    {
        List lista = getList();
        this.dataProvider =  DataProvider.ofCollection(lista);
        Grid.Column columna = this.dataGrid.getColumns().stream().findFirst().get();
        footer.getCell(columna).setText(lista.size() + "");
        this.dataGrid.setDataProvider(this.dataProvider);
    }

    public T getSelected() {
        Set<T> setCom = this.grid.getSelectedItems();
        if(setCom.isEmpty())return null;
        return setCom.stream().findFirst().get();
    }




    public abstract void mostrarEstadoWindows(T bean);


}
