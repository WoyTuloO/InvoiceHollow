package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.config.ConfigStorage;

import javax.swing.*;

public class DashBoardHandler {

    private JPanel dashBoardChartDisplayPanel;
    private JProgressBar progressBar;
    private JLabel incomeThisMonthLabel;
    private JLabel thisMonthsTargetLabel;
    private JLabel thisMonthsInvoiceCountLabel;

    private ConfigStorage config;


    public DashBoardHandler(JPanel dashBoardChartDisplayPanel, JProgressBar progressBar, JLabel incomeThisMonthLabel, JLabel thisMonthsTargetLabel, JLabel thisMonthsInvoiceCountLabel) {
        this.dashBoardChartDisplayPanel = dashBoardChartDisplayPanel;
        this.progressBar = progressBar;
        this.incomeThisMonthLabel = incomeThisMonthLabel;
        this.thisMonthsTargetLabel = thisMonthsTargetLabel;
        this.thisMonthsInvoiceCountLabel = thisMonthsInvoiceCountLabel;
    }


    public void reloadDashBoard(){



    }





}




