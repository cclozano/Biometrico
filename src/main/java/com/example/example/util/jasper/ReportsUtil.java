package com.example.example.util.jasper;

import com.example.example.util.config.Parametros;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

@Component
public class ReportsUtil {


    @Autowired
    Parametros parametros;

    public ReportsUtil() {

    }



    public String getReportsPath()
    {
        return parametros.getReportPath();
    }

    /**
     * Get database connection, call report generation method and export's report to Vaadin's FileDownloader
     * @param reportTemplate Nombre de la plantilla JasperReports
     * @param formato Tipo de salida del archivo
     */
    public StreamResource prepareForExportReport(String reportTemplate, String reportFileName,
                                                 Formato formato, Collection dataSource, Map datosReporte){
        StreamResource myResource;
        JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(dataSource);
        myResource = createResource(reportTemplate + ".jrxml",reportFileName,formato,data,datosReporte);
        return  myResource;
    }

    /**
     * Generate pdf report, and return it as a StreamResource
     * @param templatePath Report template path
     * @param reportFileName Pdf output file name
     * @return StreamResource with the generated pdf report
     */
    private StreamResource createResource(final String templatePath,
                                          String reportFileName,
                                          Formato formato,
                                          JRBeanCollectionDataSource dataSource,
                                          Map datosReporte) {
        String reportOutputFilename = reportFileName + ("_"+getDateAsString()+formato.value());
        return new StreamResource((StreamResource.StreamSource) () -> {
            ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();
            ReportGenerator reportGenerator=new ReportGenerator();
            try {

                reportGenerator.executeReport(parametros.getReportPath()+templatePath, pdfBuffer,dataSource,datosReporte,formato);
            } catch (JRException e) {
                e.printStackTrace();
            }
            // Return a stream from the buffer.
            return new ByteArrayInputStream(
                    pdfBuffer.toByteArray());
        }, reportOutputFilename);
    }


    public byte[] getBytesPdf(final String templatePath,
                                          Formato formato,
                                          Collection dataSource,
                                          Map datosReporte) {
            ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();
            JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(dataSource);
            ReportGenerator reportGenerator=new ReportGenerator();
            try {
                reportGenerator.executeReport(parametros.getReportPath()+templatePath+".jrxml", pdfBuffer,data,datosReporte,formato);
            } catch (JRException e) {
                e.printStackTrace();
            }
            return pdfBuffer.toByteArray();
    }


    private StreamResource createXResource(final String templatePath, Formato formato, JRBeanCollectionDataSource dataSource, Map datosReporte) {
        String reportOutputFilename = ("_"+getDateAsString()+formato.value());
        StreamResource str = new StreamResource((StreamResource.StreamSource) () -> {
            ByteArrayOutputStream Buffer = new ByteArrayOutputStream();
            ReportGenerator reportGenerator=new ReportGenerator();
            try {
                reportGenerator.executeReport(parametros.getReportPath()+templatePath, Buffer,dataSource,datosReporte,formato);
            } catch (JRException e) {
                e.printStackTrace();
            }
            return new ByteArrayInputStream(
                    Buffer.toByteArray());
        }, reportOutputFilename);
        return str;
    }

    /**
     * Convert a date to String
     * @return String with date
     */
    private String getDateAsString(){
        return(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))+"_"+
                String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1)+"_"+
                String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))+"_"+
                String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))+"_"+
                String.valueOf(Calendar.getInstance().get(Calendar.MINUTE))+"_"+
                String.valueOf(Calendar.getInstance().get(Calendar.SECOND)));
    }



    public Window getReportWindow(Collection dataSource,
                                  String nombreArchivoJRXML,
                                  String nombreArchivoSalida,
                                  Map parametros)
    {
        StreamResource sr = prepareForExportReport(
                nombreArchivoJRXML,
                nombreArchivoSalida,
                Formato.PDF,
                dataSource,
                parametros);

        StreamResource srExcel = prepareForExportReport(
                nombreArchivoJRXML,
                nombreArchivoSalida,
                Formato.EXCEL,
                dataSource,
                parametros);


        VerticalLayout v = new VerticalLayout();
        HorizontalLayout barmenu = new HorizontalLayout();
        Button button = new Button(FontAwesome.FILE_EXCEL_O);
        BrowserWindowOpener opener = new BrowserWindowOpener(srExcel);
        opener.extend(button);
        barmenu.addComponent(button);
        barmenu.setComponentAlignment(button,Alignment.MIDDLE_CENTER);
        v.addComponent(barmenu);
        v.setWidth("100%");
        v.setSpacing(true);
        v.setComponentAlignment(barmenu,Alignment.MIDDLE_CENTER);
        Embedded e = new Embedded("", sr);
        e.setSizeFull();
        e.setType(Embedded.TYPE_BROWSER);
        v.addComponent(e);
        v.setExpandRatio(e,-1);
        Window w = new Window();
        w.setContent(v);
        v.setHeight("100%");
        w.setWidth("80%");
        w.setHeight("90%");
        w.setModal(true);
        return w;
    }


}
