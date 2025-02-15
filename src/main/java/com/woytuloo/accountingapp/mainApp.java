package com.woytuloo.accountingapp;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import com.woytuloo.accountingapp.InvoiceManagement.WorkingInvoice;
import com.woytuloo.accountingapp.config.ConfigStorage;
import com.woytuloo.accountingapp.handlers.DashBoardHandler;
import com.woytuloo.accountingapp.main.AppFrame;

import java.util.HashMap;
import java.util.Map;

public class mainApp {

    public static void main(String[] args) {
        AppFrame frame = new AppFrame();

        ConfigStorage configStorage = new ConfigStorage();
        configStorage.loadConfigFile();

        HashMap<String, Invoice> collection = new HashMap<>();

        InvoiceBlueprintAdder invoiceBlueprintAdder = new InvoiceBlueprintAdder(frame.getInvoiceNameField(), frame.getChoseFileButton(), frame.getProceedButton(), frame.getParamCellCombo(), frame.getSaveFormButton(), frame.getCardLayout(), frame.getBackgroundPanel(), collection);
        DashBoardHandler dashBoardHandler = new DashBoardHandler(frame.getDashBoardChartDisplayPanel(), frame.getYearlyIncomeProgressBar(), frame.getIncomeThisMonthLabel(), frame.getThisMonthsTargetLabel(), frame.getThisMonthInvoiceCountLabel(), frame.getDashBoardPanelCard());






        frame.setVisible(true);


    }


}
