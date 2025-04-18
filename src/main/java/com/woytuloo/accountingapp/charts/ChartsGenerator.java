/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.charts;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.swing.*;

import com.woytuloo.accountingapp.handlers.FontHandler;
import com.woytuloo.accountingapp.main.AppFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartsGenerator {

    private static int incomeBound;
    private static int invoiceBound;
    private static final Font montserratFont = FontHandler.montserrat18.deriveFont(17f);
    private static final Map<String, String> monthToRoman;

    static {


        monthToRoman = Map.ofEntries(
                Map.entry("styczeń", "I"),
                Map.entry("luty", "II"),
                Map.entry("marzec", "III"),
                Map.entry("kwiecień", "IV"),
                Map.entry("maj", "V"),
                Map.entry("czerwiec", "VI"),
                Map.entry("lipiec", "VII"),
                Map.entry("sierpień", "VIII"),
                Map.entry("wrzesień", "IX"),
                Map.entry("październik", "X"),
                Map.entry("listopad", "XI"),
                Map.entry("grudzień", "XII")
        );
    }

    public static void showIncomeChart(JPanel p,Map<String, Double > monthMoneyMap) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        incomeBound = 1000;
        monthMoneyMap.forEach((month,money)->{
            dataset.setValue(money, "", monthToRoman.get(month));
            incomeBound = (int) Math.max(incomeBound, money);
        });
        if(invoiceBound > 1000)
            incomeBound += 1000;


        JFreeChart chart = ChartFactory.createBarChart("", "", "Dochód",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(new Color(15, 15, 15));

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.getDomainAxis().setLabelPaint(new Color(255, 0, 0));
        categoryPlot.getRangeAxis().setTickLabelPaint(new Color(255, 0, 0));
        categoryPlot.setRangeGridlinePaint(new Color(15, 15, 15));

        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setTickLabelPaint(new Color(240, 240, 240));
        domainAxis.setLabelPaint(new Color(240, 240, 240));
        domainAxis.setLabelFont(montserratFont);
        domainAxis.setTickLabelFont(montserratFont);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
        rangeAxis.setTickLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelFont(montserratFont);
        rangeAxis.setTickLabelFont(montserratFont);
        rangeAxis.setLowerBound(0);
        rangeAxis.setUpperBound(incomeBound);

        categoryPlot.setBackgroundPaint(new Color(15, 15, 15));

        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();

        Color clr3 = new Color(0, 50, 160);
        renderer.setSeriesPaint(0, clr3);
        renderer.setDefaultLegendTextPaint(new Color(255, 0, 0));


        ChartPanel barpChartPanel = new ChartPanel(chart);
        p.removeAll();
        p.add(barpChartPanel, BorderLayout.CENTER);
        p.validate();
        p.repaint();


    }


    public static void showWorkDoneChart(JPanel p, Map<String, Integer> monthWorkMap) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();



        invoiceBound = 8;
        monthWorkMap.forEach((month,work)->{
            dataset.setValue(work, "", monthToRoman.get(month));
            invoiceBound = Math.max(invoiceBound, work);
        });
        invoiceBound += 2;

        JFreeChart chart = ChartFactory.createBarChart("", "", "Faktury",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(new Color(15, 15, 15));

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.getDomainAxis().setLabelPaint(new Color(255, 0, 0));
        categoryPlot.getRangeAxis().setTickLabelPaint(new Color(255, 0, 0));
        categoryPlot.setRangeGridlinePaint(new Color(15, 15, 15));

        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setTickLabelPaint(new Color(240, 240, 240));
        domainAxis.setLabelPaint(new Color(240, 240, 240));
        domainAxis.setLabelFont(montserratFont);
        domainAxis.setTickLabelFont(montserratFont);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
        rangeAxis.setTickLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelFont(montserratFont);
        rangeAxis.setTickLabelFont(montserratFont);
        rangeAxis.setLowerBound(0);
        rangeAxis.setUpperBound(invoiceBound);

        categoryPlot.setBackgroundPaint(new Color(15, 15, 15));
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();

        Color clr3 = new Color(0, 50, 160);
        renderer.setSeriesPaint(0, clr3);
        renderer.setDefaultLegendTextPaint(new Color(255, 0, 0));
        ChartPanel barpChartPanel = new ChartPanel(chart);
        p.removeAll();
        p.add(barpChartPanel, BorderLayout.CENTER);
        p.validate();
        p.repaint();


    }

}
