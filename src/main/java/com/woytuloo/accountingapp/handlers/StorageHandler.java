package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StorageHandler {

    ConfigStorage configStorage;
    private static ConcurrentMap<Integer, ArchivedInvoice> invoices;

    public StorageHandler(ConfigStorage configStorage) {
        invoices = new ConcurrentHashMap<>();
        this.configStorage = configStorage;

        importInvoices();
    }

    public void archiveInvoice(ArchivedInvoice invoice){
        invoices.put(configStorage.getCurrentInvoiceNum(), invoice);
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
