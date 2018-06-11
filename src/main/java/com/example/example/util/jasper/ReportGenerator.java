package com.example.example.util.jasper;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {
    private Log log= LogFactory.getLog(this.getClass());

    public ReportGenerator() {
    }

    public void executeReport(String templatePath, OutputStream outputStream, JRBeanCollectionDataSource dataSource, Map datosReporte, Formato formato) throws JRException {

        JasperDesign jasperDesign=loadTemplate(templatePath);
        setTempDirectory(templatePath);
        //JasperReport jasperReport=compileReport(jasperDesign);
        JasperReport jasperReport=JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint=fillReport(jasperReport,dataSource,datosReporte);

        switch (formato)
        {
            case PDF:
                exportReportToPdf(jasperPrint, outputStream);
                break;
            case HTML:
                exportReportToHtml(jasperPrint, outputStream);
                break;
            case EXCEL:
                exportReportToXls(jasperPrint, outputStream);
                break;
            default:
                exportReportToPdf(jasperPrint, outputStream);
                break;
        }
        //exportReport(jasperPrint, outputStream);
    }

    /**
     * Load the template (defined by templatePath) and return a JasperDesign object representing the template
     * @return JasperDesign
     */
    private JasperDesign loadTemplate(String templatePath){
        JasperDesign jasperDesign=null;
        File templateFile=new File(templatePath);
        System.out.println("ABSOLUTE PATH: "+templateFile.getAbsolutePath());
        if(templateFile.exists()){
            try {
                jasperDesign= JRXmlLoader.load(templateFile);
            } catch (JRException e) {
                log.error("Error in loading the template... "+e.getMessage());
            }
        }
        else
            log.error("Error, the file dont exists");
        return(jasperDesign);
    }

    /**
     * Compile the report and generates a binary version of it
     * @param jasperDesign The report design
     * @return JasperReport
     */
    private JasperReport compileReport(JasperDesign jasperDesign){
        JasperReport jasperReport=null;
        try {
            jasperReport= JasperCompileManager.compileReport(jasperDesign);
        } catch (JRException e) {
            e.printStackTrace();
            log.error("Error in compiling the report.... "+e.getMessage());
        }
        return(jasperReport);
    }

    /**
     * Fill the report and generates a binary version of it
     * @param jasperReport The Compiled report design
     * @return JasperPrint
     */
    private JasperPrint fillReport(JasperReport jasperReport, JRBeanCollectionDataSource dataSource, Map datosReporte){
        JasperPrint jasperPrint=null;
        try {
            jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    datosReporte,
                    dataSource);
        } catch (JRException e) {
            //Notification.show("Error llenando el reporte..... "+e.getMessage());
            log.error("Error in filling the report..... "+e.getMessage());
            e.printStackTrace();
        }
        return(jasperPrint);
    }

    /**
     * Prepare a JRExporter for the filled report (to HTML)
     * @param jasperPrint The jasperPrint
     * @return The HTML text
     */
    private void exportReportToPdf(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
    }

    private void exportReportToXls(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
    }

    private void exportReportToHtml(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
    }
    /**
     * Set the temp directory for report generation
     */
    private void setTempDirectory(String templatePath){
        File templateFile=new File(templatePath);
        if(templateFile.exists()){
            log.info("Setting parentDirectory: "+templateFile.getParent());
            System.setProperty("jasper.reports.compile.temp", templateFile.getParent());
        }
    }

    //exporta a xls
    private void exportXls(JasperPrint jasperPrint, OutputStream outputStream)throws JRException
    {
        Map<String, String> dateFormats = new HashMap<String, String>();
        dateFormats.put("EEE, MMM d, yyyy", "ddd, mmm d, yyyy");

        JRXlsExporter exporter = new JRXlsExporter();
        SimpleXlsReportConfiguration repConfig = new SimpleXlsReportConfiguration();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        repConfig.setDetectCellType(Boolean.TRUE);
        repConfig.setFormatPatternsMap(dateFormats);
        exporter.setConfiguration(repConfig);
        exporter.exportReport();

    }
}