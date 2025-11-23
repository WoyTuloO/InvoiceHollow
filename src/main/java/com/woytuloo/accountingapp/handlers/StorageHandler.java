package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import com.woytuloo.accountingapp.component.ButtonPanel;
import com.woytuloo.accountingapp.component.InvoiceButtonPanel;
import com.woytuloo.accountingapp.component.InvoiceComboDataTile;

import javax.swing.*;
import com.woytuloo.accountingapp.service.ArchiveService;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StorageHandler {

    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(StorageHandler.class);

    // Mostek do wywołań statycznych (z zachowaniem kompatybilności)
    private static volatile StorageHandler lastInstance;

    ConfigStorage configStorage;
    JPanel lastInvoiceRenderPanel;
    InvoiceGenerator invoiceGenerator;
    JPanel background;
    CardLayout cardLayout;
    private final ConcurrentMap<Integer, ArchivedInvoice> invoices;
    private int workingInvoiceNum = 0;
    private JTextField searchField;

    public ArchivedInvoice getInvoice(int num){
        return invoices.get(num);
    }

    // Expose read-only view for archive querying service
    public java.util.Map<Integer, ArchivedInvoice> getAllInvoicesView(){
        return java.util.Collections.unmodifiableMap(invoices);
    }

    public StorageHandler(ConfigStorage configStorage, JPanel lastInvoiceRenderPanel, CardLayout cardLayout, JPanel background, ButtonPanel showLastInvoicesButtonPanel, JButton searchButton, JTextField searchField){
        this.invoices = new ConcurrentHashMap<>();
        this.configStorage = configStorage;
        this.lastInvoiceRenderPanel = lastInvoiceRenderPanel;
        this.cardLayout = cardLayout;
        this.background = background;
        this.searchField = searchField;
        lastInstance = this;

        // Rejestracja akcji UI bez IO – import należy uruchomić jawnie przez init()
        showLastInvoicesButtonPanel.addActionListener(e -> {
            displayInvoices();
            cardLayout.show(this.background, "2 1");
        });

        searchButton.addActionListener(e -> {
            filterInvoices(searchField.getText());
        });

        searchField.addActionListener(e -> {
            filterInvoices(searchField.getText());
        });

        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if("Wyszukaj fakturę  (numer, data, adres)".equals(searchField.getText())){
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(searchField.getText().isBlank()){
                    searchField.setText("Wyszukaj fakturę  (numer, data, adres)");
                }
            }
        });

    }

    // Jawna inicjalizacja danych archiwum (IO poza konstruktorem)
    public void init() {
        importInvoices();
    }

    public void setInvoiceGenerator(InvoiceGenerator invoiceGenerator){
        this.invoiceGenerator = invoiceGenerator;
    }

    public void archiveInvoice(ArchivedInvoice invoice){
        invoices.put(ConfigStorage.getCurrentInvoiceNum(), invoice);
    }

    public void updateArchive(ReadyInvoice rdyInvoice) {
        invoices.put(workingInvoiceNum, new ArchivedInvoice(rdyInvoice));
    }


    public void displayInvoices(){
        lastInvoiceRenderPanel.removeAll();

        ArchiveService archiveService = new ArchiveService(new com.woytuloo.accountingapp.service.Adapters.StorageHandlerArchiveRepoAdapter(this));
        java.util.List<Integer> numbers = archiveService.listAllNumbers();
        for (Integer k : numbers) {
            ArchivedInvoice v = archiveService.getByNumber(k);
            InvoiceButtonPanel invoiceButtonPanel = new InvoiceButtonPanel(String.valueOf(k));
            invoiceButtonPanel.addActionListener(e -> {
                this.invoiceGenerator.setupArchivedFields(v);
                this.workingInvoiceNum = k;
                cardLayout.show(this.background, "fillInvoiceDataCard");
            });
            lastInvoiceRenderPanel.add(invoiceButtonPanel);
        }

        lastInvoiceRenderPanel.revalidate();
        lastInvoiceRenderPanel.repaint();
    }

    public void filterInvoices(String filter){
        lastInvoiceRenderPanel.removeAll();

        if(!filter.isBlank()){
            ArchiveService archiveService = new ArchiveService(new com.woytuloo.accountingapp.service.Adapters.StorageHandlerArchiveRepoAdapter(this));
            java.util.List<Integer> numbers = archiveService.filterNumbers(filter);
            for (Integer k : numbers) {
                ArchivedInvoice v = archiveService.getByNumber(k);
                InvoiceButtonPanel invoiceButtonPanel = new InvoiceButtonPanel(String.valueOf(k));
                invoiceButtonPanel.addActionListener(e -> {
                    this.invoiceGenerator.setupArchivedFields(v);
                    this.workingInvoiceNum = k;
                    cardLayout.show(this.background, "fillInvoiceDataCard");
                });
                lastInvoiceRenderPanel.add(invoiceButtonPanel);
            }
        } else {
            displayInvoices();
        }

        lastInvoiceRenderPanel.revalidate();
        lastInvoiceRenderPanel.repaint();
    }




    public void saveCurrentInvoice(ReadyInvoice rdyInvoice){

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "InvoiceArchive2.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Plik InvoiceArchive został utworzony.");
            }

            // read existing (encrypted or plaintext), append, write encrypted
            java.util.List<String> lines = Files.readAllLines(formsDataPath);
            lines.add(rdyInvoice.toString());
            Files.write(formsDataPath, lines);

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            logger.error("Bład :" + e.getMessage());

            e.printStackTrace();
        }
    }


    public static void saveInvoices(){
        StorageHandler inst = lastInstance;
        if (inst == null) {
            // Brak instancji – nic do zapisania, zachowujemy się bezpiecznie
            return;
        }
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "InvoiceArchive1.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Plik InvoiceArchive został utworzony.");
            }

            java.util.List<String> lines = new java.util.ArrayList<>();
            inst.invoices.forEach((k, v) -> lines.add(v.toString()));
            Files.write(formsDataPath, lines);

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            logger.error("Bład :" + e.getMessage());

            e.printStackTrace();
        }
    }


    public void importInvoices(){
        applyFileLimits();
        checkForFileErrors();

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "InvoiceArchive1.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Plik InvoiceArchive został utworzony.");
            }

            java.util.List<String> lines = Files.readAllLines(formsDataPath);
            for (String line : lines) {
                String[] data = line.split("\\|");
                ArchivedInvoice invoice = new ArchivedInvoice(line);
                invoices.put(Integer.parseInt(data[0]), invoice);
            }

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            logger.error("Bład :" + e.getMessage());

            e.printStackTrace();
        }
    }

    private void applyFileLimits() {
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "InvoiceArchive1.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Plik InvoiceArchive został utworzony.");
            }

            Set<String> archived = new HashSet<>(Files.readAllLines(formsDataPath));
            if(archived.size() > 1000){
                Set<String> newArchived = new HashSet<>();
                for(int i = archived.size() - 1000; i < archived.size(); i++){
                    newArchived.add(archived.toArray()[i].toString());
                }
                Files.write(formsDataPath, new java.util.ArrayList<>(newArchived));
            }

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            logger.error("Bład :" + e.getMessage());

            e.printStackTrace();
        }


    }

    private void checkForFileErrors() {

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "InvoiceArchive1.csv");
        Path formsDataPath2 = Paths.get(configDirPath.toString(), "InvoiceArchive2.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Plik InvoiceArchive1 został utworzony.");
            }
            if(Files.notExists(formsDataPath2)){
                Files.createFile(formsDataPath2);
                System.out.println("Plik InvoiceArchive2 został utworzony.");
            }

            Set<String> archived = new HashSet<>(Files.readAllLines(formsDataPath));
            Set<String> incomingLines = new HashSet<>(Files.readAllLines(formsDataPath2)); // mergingFile

            for(String line : incomingLines){
                if(!archived.contains(line)){
                    mergeFiles(archived, incomingLines, formsDataPath);
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            logger.error("Bład :" + e.getMessage());

            e.printStackTrace();
        }

        clearCacheFile(formsDataPath2);
    }

    private void clearCacheFile(Path formsDataPath2) {
        try {
            Files.write(formsDataPath2, new ArrayList<>());
        } catch (IOException e) {
            System.err.println("Wystąpił błąd: podczas czyszczenia pliku cache.");
            logger.error("Bład :" + e.getMessage());
        }
    }

    private void mergeFiles(Set<String> archived, Set<String> incomingLines, Path archiveFile) {
        archived.addAll(incomingLines);
        try {
            Files.write(archiveFile, new java.util.ArrayList<>(archived));
        } catch (IOException e) {
            System.err.println("Wystąpił błąd: podczas mergowania archuwum.");
            logger.error("Bład :" + e.getMessage());

        }
    }


    public void reloadArchiveMenu() {
        this.searchField.setText("Wyszukaj fakturę  (numer, data, adres)");
        displayInvoices();
    }
}
