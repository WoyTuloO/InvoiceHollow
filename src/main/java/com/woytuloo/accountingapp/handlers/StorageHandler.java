package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import com.woytuloo.accountingapp.config.ConfigStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageHandler {

    ConfigStorage configStorage;
    private ConcurrentMap<Integer, ArchivedInvoice> invoices;


    public StorageHandler(ConfigStorage configStorage) {
        invoices = new ConcurrentHashMap<>();
        this.configStorage = configStorage;
    }


    public void saveInvoices(){

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "FormsData.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Folder FormsData został utworzony.");
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
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "FormsData.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Folder FormsData został utworzony.");
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



}
