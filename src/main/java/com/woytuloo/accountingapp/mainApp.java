package com.woytuloo.accountingapp;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;
import com.woytuloo.accountingapp.component.InvoiceBlueprintPanel;
import com.woytuloo.accountingapp.handlers.*;
import com.woytuloo.accountingapp.main.AppFrame;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

public class mainApp {

    private static org.apache.logging.log4j.Logger logger ;


    public static void main(String[] args) {
try{
    String userHome = System.getProperty("user.home");

    String logPath = userHome + "/.logs/InvoiceHollow";

    System.setProperty("log.path", logPath);

    File dir = new File(logPath);
    if (!dir.exists()) {
        dir.mkdirs();
    }

    logger = org.apache.logging.log4j.LogManager.getLogger(mainApp.class);


    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("Nieobsłużony wyjątek w wątku " + thread.getName(), throwable);
        });


        AppFrame frame = new AppFrame();

        ConfigStorage configStorage = new ConfigStorage(frame.getExitButton());
        StorageHandler storageHandler = new StorageHandler(configStorage, frame.getLastInvoiceRenderPanel(), frame.getCardLayout(), frame.getBackgroundPanel(),frame.showLastInvoicesButtonPanel(), frame.getSearchButton(), frame.getSearchTextField());
        HashMap<String, Invoice> collection = new HashMap<>();

        InvoiceBlueprintAdder invoiceBlueprintAdder = new InvoiceBlueprintAdder(frame.getInvoiceNameField(), frame.getChoseFileButton(), frame.getProceedButton(), frame.getParamCellCombo(), frame.getSaveFormButton(), frame.getCardLayout(), frame.getBackgroundPanel(), collection);
        invoiceBlueprintAdder.loadInvoiceFromFile();


        DashBoardHandler dashBoardHandler = new DashBoardHandler(frame.getDashBoardChartDisplayPanel(), frame.getYearlyIncomeProgressBar(), frame.getIncomeThisMonthLabel(), frame.getThisMonthsTargetLabel(), frame.getThisMonthInvoiceCountLabel(), frame.getDashBoardPanelCard(), configStorage);

        AutoCompleteHandler autoCompleteHandler = new AutoCompleteHandler(frame.getRememberedJPanel(), frame.getParametersCombo(), frame.getSuggestionsTextArea(), frame.getSaveButton(), frame.getDeleteButton());

        InvoiceBlueprintHandler invoiceBlueprintHandler = new InvoiceBlueprintHandler(frame.getSavedBlueprintsCard(), frame.getSavedBlueprintsDisplayPanel(), collection, invoiceBlueprintAdder);

        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(frame.getGenerateInvoiceButton(), frame.getInvoiceDataRenderPanel(), configStorage, storageHandler, autoCompleteHandler, frame.getCardLayout(), frame.getBackgroundPanel());
        storageHandler.setInvoiceGenerator(invoiceGenerator);

        InvoiceDisplayHandler invoiceDisplayHandler = new InvoiceDisplayHandler(frame.getBackgroundPanel(), frame.getChoseInvoiceDisplayPanel(), collection, invoiceGenerator, frame.getCardLayout());

        MenuHandler menuHandler = new MenuHandler(frame.getMenu(), frame.getBackgroundPanel(), frame.getCardLayout(), invoiceDisplayHandler, storageHandler);

        frame.setVisible(true);

} catch (Exception e) {
    try {
        PrintWriter pw = new PrintWriter("fatal_error.log");
        e.printStackTrace(pw);
        pw.close();
    } catch (Exception ignored) {}
    throw new RuntimeException("Fatal error", e);
}


    }


}
