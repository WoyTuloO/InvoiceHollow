/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
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
        
//        monthMoneyMap.forEach((month,money)->{
//            dataset.setValue(money, "", month);
//        });
        
        
        JFreeChart chart = ChartFactory.createBarChart("Dochód","","razem [zł]", 
                dataset, PlotOrientation.VERTICAL, false,true,false);
        chart.setBackgroundPaint(new Color(30,30,30));
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.getDomainAxis().setLabelPaint(new Color(255,0,0) );
        categoryPlot.getRangeAxis().setTickLabelPaint(new Color(255,0,0));
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);
        
        categoryPlot.setBackgroundPaint(new Color(30,30,30));
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
