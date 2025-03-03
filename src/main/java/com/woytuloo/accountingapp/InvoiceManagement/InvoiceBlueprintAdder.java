/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.InvoiceManagement;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import javax.swing.*;

/**
 * @author wojte
 */
public class InvoiceBlueprintAdder {

    JLabel label;
    File srcFile;
    private static HashMap<String, Invoice> collection;

    private JTextField invoiceNameField;
    private JButton choseFileButton;
    private JButton proceedButton;
    private JComboBox paramCellCombo;
    private CardLayout cardLayout;
    private JPanel background;

    private Invoice currentInvoice;

    public static Invoice getInvoiceBlueprint(String name){
        return collection.get(name);
    }


    public InvoiceBlueprintAdder(JTextField invoiceNameField, JButton choseFileButton, JButton proceedButton, JComboBox paramCellCombo, JButton saveFormButton, CardLayout cardLayout, JPanel backgroundPanel, HashMap<String, Invoice> collection) {
        this.invoiceNameField = invoiceNameField;
        this.choseFileButton = choseFileButton;
        this.cardLayout = cardLayout;
        this.background = backgroundPanel;

        this.choseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                srcFile = setFile();
            }
        });

        this.proceedButton = proceedButton;
        this.proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!invoiceNameField.getText().isEmpty() && getFile() != null) {
                    setupInvoice(invoiceNameField.getText(), getFile());
                    cardLayout.show(backgroundPanel, "fillFormCard");
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Brakuje nazwy lub pliku wejściowego!",
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        this.paramCellCombo = paramCellCombo;

        saveFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    setInvoiceConfigurationString();
                    collection.put(currentInvoice.getName(), new Invoice(currentInvoice));
                    System.out.println(collection.get(currentInvoice.getName()).toString());
                    clearFileds();
                    saveInvoiceToFile();
            }
        });
        this.collection = collection;
    }

    public void setupInvoice(String name, File src) {
        this.currentInvoice = new Invoice(name, getFile());
    }
    public void setupInvoiceEdit(Invoice invoice){
        this.currentInvoice = invoice;
    }

    public void setInvoiceConfigurationString() {
        int comboSize = paramCellCombo.getItemCount();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < comboSize; i++)
            sb.append(",").append(paramCellCombo.getItemAt(i));

        this.currentInvoice.setConfigurationDataString(sb.toString());
    }

    public File setFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        return null;
    }

    public File getFile() {
        return this.srcFile;
    }

    public void clearFileds() {
        this.invoiceNameField.setText("Nazwa Szablonu");
        this.srcFile = null;
        this.paramCellCombo.removeAllItems();
    }

    public String getType(File src) {
        return src.getName().split("\\.")[1];
    }

    public void loadInvoiceFromFile() {

        BufferedReader reader = null;
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "FormsData.csv");

        try {
            if (Files.notExists(configDirPath))
                return;

            if (Files.notExists(formsDataPath))
                return;

            reader = new BufferedReader(new FileReader(formsDataPath.toString()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    return;
                String[] data = line.split(",");
                String name = data[0];
                String path = data[1];
                Invoice inv = new Invoice(name, new File(path));

                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < data.length; i++) {
                    if(!data[i].isBlank())
                        sb.append(",").append(data[i]);
                }

                inv.setConfigurationDataString(sb.substring(1));

                if (!collection.containsKey(name))
                    collection.put(name, inv);

            }
        } catch (IOException ex) {
            System.err.println("Wystąpił błąd: " + ex.getMessage());
        }
    }


    public void saveInvoiceToFile() {

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path formsDataPath = Paths.get(configDirPath.toString(), "FormsData.csv");

        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder InvoiceHollow został utworzony.");
            }

            if (Files.notExists(formsDataPath)) {
                Files.createFile(formsDataPath);
                System.out.println("Plik FormsData został utworzony.");
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(formsDataPath.toFile()));

            for(Invoice inv : collection.values()){
                writer.write(inv.toString());
                writer.newLine();
            }

            writer.close();

        } catch (IOException ex) {
            System.err.println("Wystąpił błąd: " + ex.getMessage());
        }
    }

    public void setupEdit(Invoice invoice) {
        this.paramCellCombo.removeAllItems();
        String[] comboItems = invoice.getConfigurationDataString().split(",");

        for (String item : comboItems) {
            this.paramCellCombo.addItem(item);
        }
        setupInvoiceEdit(invoice);
        this.cardLayout.show(background, "fillFormCard");

    }
}