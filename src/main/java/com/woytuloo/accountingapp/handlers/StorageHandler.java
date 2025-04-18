package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import com.woytuloo.accountingapp.component.ButtonPanel;
import com.woytuloo.accountingapp.component.InvoiceButtonPanel;
import com.woytuloo.accountingapp.component.InvoiceComboDataTile;

import javax.swing.*;
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
    ConfigStorage configStorage;
    JPanel lastInvoiceRenderPanel;
    InvoiceGenerator invoiceGenerator;
    JPanel background;
    CardLayout cardLayout;
    private static ConcurrentMap<Integer, ArchivedInvoice> invoices;
    private int workingInvoiceNum = 0;
    private JTextField searchField;

    public StorageHandler(ConfigStorage configStorage, JPanel lastInvoiceRenderPanel, CardLayout cardLayout, JPanel background, ButtonPanel showLastInvoicesButtonPanel, JButton searchButton, JTextField searchField){
        invoices = new ConcurrentHashMap<>();
        this.configStorage = configStorage;
        this.lastInvoiceRenderPanel = lastInvoiceRenderPanel;
        this.cardLayout = cardLayout;
        this.background = background;
        this.searchField = searchField;
        importInvoices();

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

        invoices.forEach((k, v) -> {
            InvoiceButtonPanel invoiceButtonPanel = new InvoiceButtonPanel(String.valueOf(k));

            invoiceButtonPanel.addActionListener(e -> {
                this.invoiceGenerator.setupArchivedFields(v);
                this.workingInvoiceNum = k;
                cardLayout.show(this.background, "fillInvoiceDataCard");
            });

            lastInvoiceRenderPanel.add(invoiceButtonPanel);
        });

        lastInvoiceRenderPanel.revalidate();
        lastInvoiceRenderPanel.repaint();
    }

    public void filterInvoices(String filter){
        lastInvoiceRenderPanel.removeAll();

        if(!filter.isBlank())
            invoices.forEach((k, v) -> {
                Map<String, String > data = v.getPropertyDataMap();

                boolean matches = data.entrySet().stream()
                        .anyMatch(e -> e.getValue().toLowerCase().contains(filter.toLowerCase()));

                if(matches){
                    InvoiceButtonPanel invoiceButtonPanel = new InvoiceButtonPanel(String.valueOf(k));

                    invoiceButtonPanel.addActionListener(e -> {
                        this.invoiceGenerator.setupArchivedFields(v);
                        this.workingInvoiceNum = k;
                        cardLayout.show(this.background, "fillInvoiceDataCard");
                    });

                    lastInvoiceRenderPanel.add(invoiceButtonPanel);
                }
            });
        else
            displayInvoices();

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

            BufferedWriter writer = new BufferedWriter(new FileWriter(formsDataPath.toFile(), true));

            writer.write(rdyInvoice.toString());
            writer.newLine();

            writer.close();

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            logger.error("Bład :" + e.getMessage());

            e.printStackTrace();
        }
    }


    public static void saveInvoices(){

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

            BufferedWriter writer = new BufferedWriter(new FileWriter(formsDataPath.toFile()));

            invoices.forEach((k, v) -> {
                try {
                    writer.write(v.toString());
                    writer.newLine();
                } catch (IOException e) {
                    logger.error("Bład :" + e.getMessage());
                    e.printStackTrace();
                }
            });

            writer.close();

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

            BufferedReader reader = new BufferedReader(new FileReader(formsDataPath.toFile()));

            String line;
            while ((line = reader.readLine()) != null){
                String[] data = line.split(",");
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
                Files.write(formsDataPath, newArchived, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
        try (FileWriter writer = new FileWriter(formsDataPath2.toFile(), false)){
            writer.write("");
        } catch (IOException e) {
            System.err.println("Wystąpił błąd: podczas czyszczenia pliku cache.");
            logger.error("Bład :" + e.getMessage());

        }
    }

    private void mergeFiles(Set<String> archived, Set<String> incomingLines, Path archiveFile) {
        archived.addAll(incomingLines);
        try {
            Files.write(archiveFile, archived, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
