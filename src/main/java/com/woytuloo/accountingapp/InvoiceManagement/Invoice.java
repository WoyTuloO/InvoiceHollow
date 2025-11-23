package com.woytuloo.accountingapp.InvoiceManagement;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Invoice {

    private File file;
    private String name;
    private String configurationDataString;

    public String getName() {
        return name;
    }

    public String getFilePath(){
        return file.getAbsolutePath();
    }

    public String getExtension(){
        return file.getName().split("\\.")[1];
    }

    public File getFile(){
        return file;
    }

    public String getConfigurationDataString(){
        try {
            return configurationDataString;
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Faktura nie może być pusta.");

        }
        return "";
    }

    public Invoice(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public void setConfigurationDataString(String data){
        this.configurationDataString = data;
    }

    public Invoice(WorkingInvoice workingInvoice){
        this.name = workingInvoice.getName();
        this.file = new File(workingInvoice.getFilePath());
        this.configurationDataString = workingInvoice.formatToCsv();
    }

    public Invoice(Invoice invoice){
        this.name = invoice.getName();
        this.file = invoice.getFile();
        this.configurationDataString = invoice.getConfigurationDataString();
    }

    public String toString(){
        return name + "," + file.getAbsolutePath() + "," + configurationDataString;
    }
}
