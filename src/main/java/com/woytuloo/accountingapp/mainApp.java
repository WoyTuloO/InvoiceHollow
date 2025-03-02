package com.woytuloo.accountingapp;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;
import com.woytuloo.accountingapp.handlers.*;
import com.woytuloo.accountingapp.main.AppFrame;

import java.util.HashMap;

public class mainApp {

    public static void main(String[] args) {
        AppFrame frame = new AppFrame();

        ConfigStorage configStorage = new ConfigStorage(frame.getExitButton());
        StorageHandler storageHandler = new StorageHandler(configStorage, frame.getLastInvoiceRenderPanel(), frame.getCardLayout(), frame.getBackgroundPanel(),frame.showLastInvoicesButtonPanel(), frame.getSearchButton(), frame.getSearchTextField());
        HashMap<String, Invoice> collection = new HashMap<>();

        InvoiceBlueprintAdder invoiceBlueprintAdder = new InvoiceBlueprintAdder(frame.getInvoiceNameField(), frame.getChoseFileButton(), frame.getProceedButton(), frame.getParamCellCombo(), frame.getSaveFormButton(), frame.getCardLayout(), frame.getBackgroundPanel(), collection);
        invoiceBlueprintAdder.loadInvoiceFromFile();


        DashBoardHandler dashBoardHandler = new DashBoardHandler(frame.getDashBoardChartDisplayPanel(), frame.getYearlyIncomeProgressBar(), frame.getIncomeThisMonthLabel(), frame.getThisMonthsTargetLabel(), frame.getThisMonthInvoiceCountLabel(), frame.getDashBoardPanelCard(), configStorage);

        AutoCompleteHandler autoCompleteHandler = new AutoCompleteHandler(frame.getRememberedJPanel(), frame.getParametersCombo(), frame.getSuggestionsTextArea(), frame.getSaveButton(), frame.getDeleteButton());

        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(frame.getGenerateInvoiceButton(), frame.getInvoiceDataRenderPanel(), configStorage, storageHandler, autoCompleteHandler, frame.getCardLayout(), frame.getBackgroundPanel());
        storageHandler.setInvoiceGenerator(invoiceGenerator);

        InvoiceDisplayHandler invoiceDisplayHandler = new InvoiceDisplayHandler(frame.getBackgroundPanel(), frame.getChoseInvoiceDisplayPanel(), collection, invoiceGenerator, frame.getCardLayout());

        MenuHandler menuHandler = new MenuHandler(frame.getMenu(), frame.getBackgroundPanel(), frame.getCardLayout(), invoiceDisplayHandler, storageHandler);

        frame.setVisible(true);


    }


}
