/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.charts;

import java.awt.*;
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
    
    
    public static void showIncomeChart(JPanel p//,
            //HashMap<String, Double> monthMoneyMap
    ){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(200, "Amount", "january");
        dataset.setValue(150, "Amount", "february");
        dataset.setValue(18, "Amount", "march");
        dataset.setValue(100, "Amount", "april");
        dataset.setValue(80, "Amount", "may");
        dataset.setValue(250, "Amount", "june");
        dataset.setValue(200, "Amount", "july");
        dataset.setValue(150, "Amount", "august");
        dataset.setValue(18, "Amount", "september");
        dataset.setValue(100, "Amount", "october");
        dataset.setValue(80, "Amount", "november");
        dataset.setValue(250, "Amount", "december");
        
//        monthMoneyMap.forEach((month,money)->{
//            dataset.setValue(money, "", month);
//        });
        
        
        JFreeChart chart = ChartFactory.createBarChart("","","Dochód",
                dataset, PlotOrientation.VERTICAL, false,true,false);
        chart.setBackgroundPaint(new Color(20,20,20));
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.getDomainAxis().setLabelPaint(new Color(255,0,0) );
        categoryPlot.getRangeAxis().setTickLabelPaint(new Color(255,0,0));
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);

        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setTickLabelPaint(new Color(0,50,160));
        domainAxis.setLabelPaint(new Color(0,50,160));
        domainAxis.setLabelFont(new Font("Verdana", 1, 12));
        domainAxis.setTickLabelFont(new Font("Verdana", 1, 12));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
        rangeAxis.setTickLabelPaint(new Color(0,255,0));
        rangeAxis.setLabelPaint(new Color(0,255,0));
        rangeAxis.setLabelFont(new Font("Verdana", 1, 12));
        rangeAxis.setTickLabelFont(new Font("Verdana", 1, 12));



        categoryPlot.setBackgroundPaint(new Color(20,20,20));
        
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();

        Color clr3 = new Color(0,50,160);
        renderer.setSeriesPaint(0, clr3);
        renderer.setDefaultLegendTextPaint(new Color(255,0,0));
        
        
        ChartPanel barpChartPanel = new ChartPanel(chart);
        p.removeAll();
        p.add(barpChartPanel, BorderLayout.CENTER);
        p.validate();
        
               
        
    
    }
    
    
    
    public static void showWorkDoneChart(JPanel p
//              ,HashMap<String, Double> monthWorkMap
            ){
      
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(50, "", "january");
        dataset.setValue(30, "", "february");
        dataset.setValue(35, "", "march");
        dataset.setValue(38, "", "april");
        dataset.setValue(41, "", "may");
        dataset.setValue(38, "", "june");
        dataset.setValue(26, "", "july");
        dataset.setValue(33, "", "august");
        dataset.setValue(29, "", "september");
        dataset.setValue(32, "", "october");
        dataset.setValue(46, "", "november");
        dataset.setValue(44, "", "december");
        
//        monthWorkMap.forEach((month,work)->{
//            dataset.setValue(work, "", month);
//        });
        
        
        JFreeChart chart = ChartFactory.createBarChart("","","Faktury",
                dataset, PlotOrientation.VERTICAL, false,true,false);
        chart.setBackgroundPaint(new Color(20,20,20));
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.getDomainAxis().setLabelPaint(new Color(255,0,0) );
        categoryPlot.getRangeAxis().setTickLabelPaint(new Color(255,0,0));


        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setTickLabelPaint(new Color(0,50,160));
        domainAxis.setLabelPaint(new Color(0,50,160));
        domainAxis.setLabelFont(new Font("Verdana", 1, 12));
        domainAxis.setTickLabelFont(new Font("Verdana", 1, 12));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
        rangeAxis.setTickLabelPaint(new Color(0,255,0));
        rangeAxis.setLabelPaint(new Color(0,255,0));
        rangeAxis.setLabelFont(new Font("Verdana", 1, 12));
        rangeAxis.setTickLabelFont(new Font("Verdana", 1, 12));



        categoryPlot.setBackgroundPaint(new Color(20,20,20));
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();

        Color clr3 = new Color(0,50,160);
        renderer.setSeriesPaint(0, clr3);
        renderer.setDefaultLegendTextPaint(new Color(255,0,0));
        ChartPanel barpChartPanel = new ChartPanel(chart);
        p.removeAll();
        p.add(barpChartPanel, BorderLayout.CENTER);
        p.validate();
      
        
        
    }
    
}
