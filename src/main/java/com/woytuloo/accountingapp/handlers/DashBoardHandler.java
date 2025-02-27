package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.charts.ChartsGenerator;
import com.woytuloo.accountingapp.component.RoundedInfoPanel;

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
        this.incomeThisMonthLabel.setText(config.getThisMonthsEarnings()+"");
        this.thisMonthsTargetLabel.setText(config.getThisMonthsTarget()+"");
        this.thisMonthsInvoiceCountLabel.setText(config.getThisMonthsInvoiceCount()+"");
        this.progressBar.setMaximum(config.getYearlyTarget());
        this.progressBar.setValue(config.getCurrentlyEarned());
        this.progressBar.setString(config.getCurrentlyEarned() + " PLN" );

        System.out.println("Currently earned: " + config.getCurrentlyEarned());
        System.out.println("Yearly target: " + config.getYearlyTarget());

        if(config.getCurrentlyEarned() > (config.getYearlyTarget() * 3 / 4))
            this.progressBar.setForeground(new java.awt.Color(250, 205, 0));

        if(config.getCurrentlyEarned() > (config.getYearlyTarget() * 9 / 10))
            this.progressBar.setForeground(new java.awt.Color(255, 0, 0));


        reloadCharts();


    }

    private void reloadCharts() {
        Map<String, Integer[]> countAndMoneyMap =  config.getInvoiceCountandMoney();

        Map<String, Integer> incomeMap = new LinkedHashMap<>();
        Map<String, Integer> workDoneMap = new LinkedHashMap<>();



        for (Map.Entry<String, Integer[]> entry : countAndMoneyMap.entrySet()) {
            String month = entry.getKey();
            Integer[] countAndMoney = entry.getValue();

            if (countAndMoney == null || countAndMoney.length < 2) {
                System.err.println("Niepoprawne dane dla miesiąca: " + month);
                continue;
            }

            int workDone = countAndMoney[0];
            int money = countAndMoney[1];

            System.out.println(month + " ilość: " + workDone + " zarobek: " + money);


            workDoneMap.put(month, workDone);
            incomeMap.put(month, money);
        }

        ChartsGenerator.showIncomeChart(dashBoardChartDisplayPanel.getIncomeChart(), incomeMap);
        ChartsGenerator.showWorkDoneChart(dashBoardChartDisplayPanel.getWorkDoneChart(), workDoneMap);

    }


}




