package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import com.woytuloo.accountingapp.component.ButtonPanel;
import com.woytuloo.accountingapp.component.InvoiceButtonPanel;
import com.woytuloo.accountingapp.component.InvoiceComboDataTile;

import javax.smartcardio.Card;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StorageHandler {

    ConfigStorage configStorage;
    JPanel lastInvoiceRenderPanel;
    InvoiceGenerator invoiceGenerator;
    JPanel background;
    CardLayout cardLayout;
    private static ConcurrentMap<Integer, ArchivedInvoice> invoices;

    public StorageHandler(ConfigStorage configStorage, JPanel lastInvoiceRenderPanel, CardLayout cardLayout, JPanel background, ButtonPanel showLastInvoicesButtonPanel) {
        invoices = new ConcurrentHashMap<>();
        this.configStorage = configStorage;
        this.lastInvoiceRenderPanel = lastInvoiceRenderPanel;
        this.cardLayout = cardLayout;
        this.background = background;
        importInvoices();

        showLastInvoicesButtonPanel.addActionListener(e -> {
            displayInvoices();
            cardLayout.show(this.background, "2 1");
        });

    }

    public void setInvoiceGenerator(InvoiceGenerator invoiceGenerator){
        this.invoiceGenerator = invoiceGenerator;
    }

    public void archiveInvoice(ArchivedInvoice invoice){
        invoices.put(ConfigStorage.getCurrentInvoiceNum(), invoice);
    }

    public void updateArchive(ReadyInvoice rdyInvoice) {
        invoices.put(rdyInvoice.getNumber(), new ArchivedInvoice(rdyInvoice));
    }


    public void displayInvoices(){
        lastInvoiceRenderPanel.removeAll();
        invoices.forEach((k, v) -> {

            InvoiceButtonPanel invoiceButtonPanel = new InvoiceButtonPanel(v.getName());

            invoiceButtonPanel.addActionListener(e -> {
                this.invoiceGenerator.setupArchivedFields(v);
                cardLayout.show(this.background, "fillInvoiceDataCard");
            });

            lastInvoiceRenderPanel.add(invoiceButtonPanel);
        });

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
                    e.printStackTrace();
                }
            });

            writer.close();

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
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
            e.printStackTrace();
        }

        clearCacheFile(formsDataPath2);
    }

    private void clearCacheFile(Path formsDataPath2) {
        try (FileWriter writer = new FileWriter(formsDataPath2.toFile(), false)){
            writer.write("");
        } catch (IOException e) {
            System.err.println("Wystąpił błąd: podczas czyszczenia pliku cache.");
        }
    }

    private void mergeFiles(Set<String> archived, Set<String> incomingLines, Path archiveFile) {
        archived.addAll(incomingLines);
        try {
            Files.write(archiveFile, archived, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Wystąpił błąd: podczas mergowania archuwum.");
        }
    }


}
