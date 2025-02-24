/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.charts;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

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

    public static void setMaxIncomeBound(int maxBound) {
        ChartsGenerator.incomeBound = maxBound;
    }
    public static void setMaxInvoiceBound(int maxBound) {
        ChartsGenerator.invoiceBound = maxBound;
    }

    public static void showIncomeChart(JPanel p,Map<String, Integer> monthMoneyMap) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        monthMoneyMap.forEach((month,money)->{
            dataset.setValue(money, "", month);
        });


        JFreeChart chart = ChartFactory.createBarChart("", "", "Dochód",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(new Color(15, 15, 15));

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.getDomainAxis().setLabelPaint(new Color(255, 0, 0));
        categoryPlot.getRangeAxis().setTickLabelPaint(new Color(255, 0, 0));
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);

        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setTickLabelPaint(new Color(230, 230, 230));
        domainAxis.setLabelPaint(new Color(230, 230, 230));
        domainAxis.setLabelFont(new Font("Arial", 1, 14));
        domainAxis.setTickLabelFont(new Font("Arial", 1, 14));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
        rangeAxis.setTickLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelFont(new Font("Arial", 1, 14));
        rangeAxis.setTickLabelFont(new Font("Arial", 1, 14));
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

        monthWorkMap.forEach((month,work)->{
            dataset.setValue(work, "", month);
        });


        JFreeChart chart = ChartFactory.createBarChart("", "", "Faktury",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(new Color(15, 15, 15));

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.getDomainAxis().setLabelPaint(new Color(255, 0, 0));
        categoryPlot.getRangeAxis().setTickLabelPaint(new Color(255, 0, 0));


        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setTickLabelPaint(new Color(230, 230, 230));
        domainAxis.setLabelPaint(new Color(230, 230, 230));
        domainAxis.setLabelFont(new Font("Arial", 1, 14));
        domainAxis.setTickLabelFont(new Font("Arial", 1, 14));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
        rangeAxis.setTickLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelPaint(new Color(0, 255, 0));
        rangeAxis.setLabelFont(new Font("Arial", 1, 14));
        rangeAxis.setTickLabelFont(new Font("Arial", 1, 14));
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
