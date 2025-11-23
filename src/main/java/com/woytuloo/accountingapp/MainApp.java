package com.woytuloo.accountingapp;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;
import com.woytuloo.accountingapp.handlers.*;
import com.woytuloo.accountingapp.main.AppFrame;
import com.woytuloo.accountingapp.service.ChartsGeneratorGateway;
import com.woytuloo.accountingapp.service.FileSuggestionsRepository;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

public class MainApp {

    private static org.apache.logging.log4j.Logger logger ;
    public static boolean settingUp = true;


    public static void main(String[] args) {
try {

    String userHome = System.getProperty("user.home");

    String logPath = userHome + "/.logs/InvoiceHollow";

    System.setProperty("log.path", logPath);

    File dir = new File(logPath);
    if (!dir.exists()) {
        dir.mkdirs();
    }

    logger = org.apache.logging.log4j.LogManager.getLogger(MainApp.class);

    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
        logger.error("Nieobsłużony wyjątek w wątku " + thread.getName(), throwable);
    });

    EventQueue.invokeLater(() -> {

        JWindow splash = new JWindow();
        splash.add(new JLabel("Ładowanie…", SwingConstants.CENTER));
        splash.setSize(300, 120);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);


        new SwingWorker<AppFrame, Void>() {
            @Override
            protected AppFrame doInBackground() {
                return setup();
            }

            @Override
            protected void done() {
                try {
                    AppFrame frame = get();
                    splash.dispose();
                    frame.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();

    });
} catch (Exception e) {
        try {
            PrintWriter pw = new PrintWriter("fatal_error.log");
            e.printStackTrace(pw);
            pw.close();
        } catch (Exception ignored) {}
        throw new RuntimeException("Fatal error", e);
    }}

public static AppFrame setup(){




        AppFrame frame = new AppFrame();
//    frame.getExitButton()
        ConfigStorage configStorage = new ConfigStorage();
        configStorage.init();
        StorageHandler storageHandler = new StorageHandler(configStorage, frame.getLastInvoiceRenderPanel(), frame.getCardLayout(), frame.getBackgroundPanel(),frame.showLastInvoicesButtonPanel(), frame.getSearchButton(), frame.getSearchTextField());
        storageHandler.init();
        HashMap<String, Invoice> collection = new HashMap<>();

        InvoiceBlueprintAdder invoiceBlueprintAdder = new InvoiceBlueprintAdder(frame.getInvoiceNameField(), frame.getChoseFileButton(), frame.getProceedButton(), frame.getParamCellCombo(), frame.getSaveFormButton(), frame.getCardLayout(), frame.getBackgroundPanel(), collection);
        invoiceBlueprintAdder.loadInvoiceFromFile();

        DashBoardHandler dashBoardHandler = new DashBoardHandler(
                frame.getDashBoardChartDisplayPanel(),
                frame.getYearlyIncomeProgressBar(),
                frame.getIncomeThisMonthLabel(),
                frame.getThisMonthsTargetLabel(),
                frame.getThisMonthInvoiceCountLabel(),
                frame.getDashBoardPanelCard(),
                configStorage,
                new ChartsGeneratorGateway()
        );

        AutoCompleteHandler autoCompleteHandler = new AutoCompleteHandler(
                frame.getRememberedJPanel(),
                frame.getParametersCombo(),
                frame.getSuggestionsTextArea(),
                frame.getSaveButton(),
                frame.getDeleteButton(),
                new FileSuggestionsRepository()
        );
        autoCompleteHandler.load();

        InvoiceBlueprintHandler invoiceBlueprintHandler = new InvoiceBlueprintHandler(frame.getSavedBlueprintsCard(), frame.getSavedBlueprintsDisplayPanel(), collection, invoiceBlueprintAdder);

        // Build new backend service and ports, inject into GUI
        var invoiceService = new com.woytuloo.accountingapp.service.InvoiceService(
                new com.woytuloo.accountingapp.service.Adapters.ConfigStorageAdapter(configStorage),
                new com.woytuloo.accountingapp.service.Adapters.StorageHandlerArchiveAdapter(storageHandler),
                new com.woytuloo.accountingapp.service.Adapters.AutoCompleteSuggestionsAdapter(autoCompleteHandler),
                new com.woytuloo.accountingapp.service.Adapters.PoiDocumentFillerAdapter(),
                new com.woytuloo.accountingapp.service.Adapters.SystemDateTimeProvider(),
                new com.woytuloo.accountingapp.service.Adapters.PolishNumberToWordsAdapter()
        );
        var numberToWordsPort = new com.woytuloo.accountingapp.service.Adapters.PolishNumberToWordsAdapter();
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(
                frame.getGenerateInvoiceButton(),
                frame.getInvoiceDataRenderPanel(),
                configStorage,
                storageHandler,
                autoCompleteHandler,
                frame.getCardLayout(),
                frame.getBackgroundPanel(),
                invoiceService,
                numberToWordsPort
        );
        storageHandler.setInvoiceGenerator(invoiceGenerator);

        InvoiceDisplayHandler invoiceDisplayHandler = new InvoiceDisplayHandler(frame.getBackgroundPanel(), frame.getChoseInvoiceDisplayPanel(), collection, invoiceGenerator, frame.getCardLayout());

        MenuHandler menuHandler = new MenuHandler(frame.getMenu(), frame.getBackgroundPanel(), frame.getCardLayout(), invoiceDisplayHandler, storageHandler);

        return frame;




    }


}
