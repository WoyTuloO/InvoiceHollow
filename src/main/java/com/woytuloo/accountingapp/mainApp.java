package com.woytuloo.accountingapp;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import com.woytuloo.accountingapp.config.ConfigStorage;
import com.woytuloo.accountingapp.handlers.DashBoardHandler;
import com.woytuloo.accountingapp.main.AppFrame;

import java.util.HashMap;
import java.util.Map;

public class mainApp {

    public static void main(String[] args) {
        AppFrame frame = new AppFrame();


        Map<String, Invoice> invoiceCollection = new HashMap<>();
        ConfigStorage configStorage = new ConfigStorage();

        configStorage.loadConfigFile();

        InvoiceBlueprintAdder invoiceAdder = new InvoiceBlueprintAdder(invoiceCollection);



        DashBoardHandler dashBoardHandler = new DashBoardHandler(frame.getDashBoardChartDisplayPanel(), frame.getYearlyIncomeProgressBar(), frame.getIncomeThisMonthLabel(), frame.getThisMonthsTargetLabel(), frame.getThisMonthInvoiceCountLabel());




        frame.setVisible(true);


    }


}
