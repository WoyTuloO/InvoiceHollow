package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.charts.ChartsGenerator;
import com.woytuloo.accountingapp.component.RoundedInfoPanel;
import com.woytuloo.accountingapp.main.AppFrame;
import com.woytuloo.accountingapp.MainApp;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashBoardHandler {
    private RoundedInfoPanel dashBoardChartDisplayPanel;
    private JProgressBar progressBar;
    private JLabel incomeThisMonthLabel;
    private JLabel thisMonthsTargetLabel;
    private JLabel thisMonthsInvoiceCountLabel;
    private JPanel dashBoardPanelCard;

    private ConfigStorage config;


    public DashBoardHandler(RoundedInfoPanel dashBoardChartDisplayPanel, JProgressBar progressBar, JLabel incomeThisMonthLabel, JLabel thisMonthsTargetLabel, JLabel thisMonthsInvoiceCountLabel, JPanel dashBoardPanelCard, ConfigStorage configStorage) {
        this.dashBoardChartDisplayPanel = dashBoardChartDisplayPanel;
        this.progressBar = progressBar;
        this.incomeThisMonthLabel = incomeThisMonthLabel;
        this.thisMonthsTargetLabel = thisMonthsTargetLabel;
        this.thisMonthsInvoiceCountLabel = thisMonthsInvoiceCountLabel;
        this.dashBoardPanelCard = dashBoardPanelCard;
        this.config = configStorage;


        this.dashBoardPanelCard.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                reloadDashBoard();
            }
        });
    }



    public void reloadDashBoard(){
        System.out.println("Reloading DashBoard");
        this.incomeThisMonthLabel.setText(config.getThisMonthsEarnings()+" zł");
        this.thisMonthsTargetLabel.setText(String.format("%.2f zł", config.getThisMonthsTarget()));
        this.thisMonthsInvoiceCountLabel.setText(config.getThisMonthsInvoiceCount()+"");
        this.progressBar.setMaximum(config.getYearlyTarget());
        this.progressBar.setValue(config.getCurrentlyEarned());
        this.progressBar.setString(config.getCurrentlyEarned() + " PLN" );
        this.progressBar.setBackground(new java.awt.Color(35, 35, 35));

        if(config.getCurrentlyEarned() > (config.getYearlyTarget() * 3 / 4))
            this.progressBar.setForeground(new java.awt.Color(250, 205, 0));

        if(config.getCurrentlyEarned() > (config.getYearlyTarget() * 9 / 10))
            this.progressBar.setForeground(new java.awt.Color(255, 0, 0));


        reloadCharts();


    }

    private void reloadCharts() {
        Map<String, Double> incomeMap = config.getMonthIncomeMap();
        Map<String, Integer> workDoneMap = config.getMonthAmmountMap();

        ChartsGenerator.showIncomeChart(dashBoardChartDisplayPanel.getIncomeChart(), incomeMap);
        ChartsGenerator.showWorkDoneChart(dashBoardChartDisplayPanel.getWorkDoneChart(), workDoneMap);

    }


}




