package com.example.example.util.jasper;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by carlos on 27/07/16.
 */
public class WindowReportes extends Window {

    protected String baseReportsPath;

    protected Map datosReporte = new HashMap();
    ReportsUtil rp;

    public WindowReportes(ReportsUtil rpu)
    {
        String path=System.getProperty("user.dir");
        this.baseReportsPath = rpu.getReportsPath() == null? path : rp.getReportsPath();
        System.out.println("Directorio: "+this.baseReportsPath);
        rp = rpu;
    }

    protected void mostrarReporte(List dataSource,
                                  String tituloReporte,
                                  String subtitulo,
                                  String nombreReportFile,
                                  Formato formato,
                                  Button button)
    {
        if (dataSource == null || dataSource.size() == 0) {
            Notification.show("No se encontraron datos para el reporte!");
            return;
        }
        Map datosReporte = new HashMap();

        datosReporte.put("P_TITULO", tituloReporte);
        datosReporte.put("P_SUBTITULO", subtitulo);
        datosReporte.put("LOGO_URL", rp.getReportsPath()+"/files/LOGO_JARDINES.png");
        datosReporte.put("PATH", baseReportsPath);

        BrowserWindowOpener opener =
                new BrowserWindowOpener(rp.prepareForExportReport(
                        nombreReportFile,
                        "doc",
                        formato,
                        dataSource,
                        datosReporte));
        opener.extend(button);
    }

    public void mostrarReporteConParametros(List dataSource,
                               Map parametros,
                               String tituloReporte,
                               String subtitulo,
                               String nombreReportFile,
                               String nombreArchivoSalida)
    {
        if (parametros!=null) {

            if (parametros.get("PATH")==null)
                datosReporte.put("PATH", baseReportsPath);

            for (Object o : parametros.keySet())
            {
                String s = (String) o;
                this.datosReporte.put(s,parametros.get(s));
            }
        }
        else {
            datosReporte.put("P_TITULO", tituloReporte);
            datosReporte.put("P_SUBTITULO", subtitulo);
            datosReporte.put("PATH", baseReportsPath);
        }
        datosReporte.put("LOGO_URL", rp.getReportsPath() + "/files/LOGO_JARDINES.png");

        this.mostrarReporte(dataSource,tituloReporte,
                 subtitulo,
                 nombreReportFile,
                 nombreArchivoSalida);
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
//        datosReporte.put("PATH", baseReportsPath);

        BrowserWindowOpener openerPdf =
                new BrowserWindowOpener(rp.prepareForExportReport(
                        nombreReportFile,
                        nombreArchivoSalida,
                        Formato.PDF,
                        dataSource,
                        datosReporte));
        BrowserWindowOpener openerExcel =
                new BrowserWindowOpener(rp.prepareForExportReport(
                        nombreReportFile,
                        nombreArchivoSalida,
                        Formato.EXCEL,
                        dataSource,
                        datosReporte));
        openerPdf.extend(pdfButton);
        openerExcel.extend(excelButton);
        UI.getCurrent().addWindow(window);
    }
}
