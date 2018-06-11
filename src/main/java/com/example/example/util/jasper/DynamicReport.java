package com.example.example.util.jasper;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DynamicReport {


    public JasperDesign createDynamicDesign(List<String> columnsName, List dataSource)
            throws JRException, IOException {
        JRDesignStaticText staticText = null;
        JRDesignTextField textField = null;
        JRDesignBand band = null;
        JRDesignExpression expression = null;
        @SuppressWarnings("unused")
        JRDesignLine line = null;
        JRDesignField field = null;
        @SuppressWarnings("unused")
        JRDesignConditionalStyle conditionalStyle = null;
        JRLineBox lineBox = null;
        JRDesignVariable variable = null;

        int x;
        int y;
        final int ROW_HEIGHT = 11;
        final int COLUMN_WIDTH = 90;

        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("dynamicColumns");
        jasperDesign.setPageWidth(columnsName.size() * 100);
        jasperDesign.setPageHeight(500);

        jasperDesign.setColumnWidth(COLUMN_WIDTH);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(40);
        jasperDesign.setRightMargin(40);
        jasperDesign.setTopMargin(40);
        jasperDesign.setBottomMargin(40);
        jasperDesign.setIgnorePagination(true);
        jasperDesign
                .setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("normal");
        normalStyle.setDefault(true);
        normalStyle.setFontName("Arial");
        normalStyle.setFontSize(8.5f);
        lineBox = normalStyle.getLineBox();
        lineBox.getTopPen().setLineWidth(0.5f);
        lineBox.getRightPen().setLineWidth(0.5f);
        lineBox.getBottomPen().setLineWidth(0.5f);
        lineBox.getLeftPen().setLineWidth(0.5f);
        jasperDesign.addStyle(normalStyle);

        JRDesignStyle headerStyle = new JRDesignStyle();
        headerStyle.setName("header");
        headerStyle.setDefault(true);
        headerStyle.setFontName("Arial");
        headerStyle.setFontSize(8.5f);
        headerStyle.setBold(true);
        lineBox = headerStyle.getLineBox();
        lineBox.getTopPen().setLineWidth(0.5f);
        lineBox.getRightPen().setLineWidth(0.5f);
        lineBox.getBottomPen().setLineWidth(0.5f);
        lineBox.getLeftPen().setLineWidth(0.5f);
        jasperDesign.addStyle(headerStyle);

        JRDesignParameter startDateParameter = new JRDesignParameter();
        startDateParameter.setName("startDate");
        startDateParameter.setValueClass(Date.class);
        jasperDesign.addParameter(startDateParameter);

        JRDesignParameter endDateParameter = new JRDesignParameter();
        endDateParameter.setName("endDate");
        endDateParameter.setValueClass(Date.class);
        jasperDesign.addParameter(endDateParameter);


        field = new JRDesignField();
        field.setName("name");
        field.setValueClass(String.class);
        jasperDesign.addField(field);

        field = new JRDesignField();
        field.setName("value");
        field.setValueClass(Integer.class);
        jasperDesign.addField(field);

        variable = new JRDesignVariable();
        variable.setResetType(ResetTypeEnum.REPORT);
        variable.setValueClass(Integer.class);
        expression = new JRDesignExpression();
        expression.setText("$F{"+columnsName.get(0)+"}");
        variable.setExpression(expression);
        variable.setCalculation(CalculationEnum.SUM);
        expression = new JRDesignExpression();
        expression.setText("0");
        variable.setInitialValueExpression(expression);
        variable.setName("summary");
        jasperDesign.addVariable(variable);

        // Title band
        band = new JRDesignBand();

        jasperDesign.setTitle(band);

        x = 0;
        y = 0;

        for (String columnName :
                columnsName) {
            band = new JRDesignBand();
            band.setHeight(ROW_HEIGHT);
            staticText = new JRDesignStaticText();
            staticText.setX(x);
            staticText.setY(y);
            staticText.setWidth(COLUMN_WIDTH);
            staticText.setHeight(ROW_HEIGHT);
            staticText.setStyle(headerStyle);
            staticText.setText(columnName);
            band.addElement(staticText);
            x += staticText.getWidth();
        }


        // Detail band
        band = new JRDesignBand();
        band.setHeight(ROW_HEIGHT);
        x = 0;
        y = 0;
        for (String column :
                columnsName) {
            textField = new JRDesignTextField();
            textField.setX(x);
            textField.setY(y);
            textField.setWidth(COLUMN_WIDTH);
            textField.setHeight(ROW_HEIGHT);
            expression = new JRDesignExpression();
            expression.setText("$F{"+column+"}");
            textField.setExpression(expression);
            textField.setStyle(normalStyle);
            band.addElement(textField);
            x += textField.getWidth();
        }

        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

        band = new JRDesignBand();
        jasperDesign.setColumnFooter(band);

        band = new JRDesignBand();
        jasperDesign.setPageFooter(band);

        // Summary band
        band = new JRDesignBand();
        band.setHeight(ROW_HEIGHT);
        x = 0;
        y = 0;

        staticText = new JRDesignStaticText();
        staticText.setX(x);
        staticText.setY(y);
        staticText.setWidth(COLUMN_WIDTH);
        staticText.setHeight(ROW_HEIGHT);
        staticText.setStyle(headerStyle);
        staticText.setText("TOTAL:");
        band.addElement(staticText);
        x+=staticText.getWidth();

        textField = new JRDesignTextField();
        textField.setX(x);
        textField.setY(y);
        textField.setWidth(COLUMN_WIDTH);
        textField.setHeight(ROW_HEIGHT);
        expression = new JRDesignExpression();
        expression.setText("$V{summary}");
        textField.setExpression(expression);
        textField.setStyle(headerStyle);
        band.addElement(textField);
        x += textField.getWidth();
        jasperDesign.setSummary(band);
        return jasperDesign;
    }


}
