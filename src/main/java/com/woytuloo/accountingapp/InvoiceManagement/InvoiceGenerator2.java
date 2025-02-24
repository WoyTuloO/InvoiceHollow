package com.woytuloo.accountingapp.InvoiceManagement;

import com.woytuloo.accountingapp.component.InvoiceDataTile;
import com.woytuloo.accountingapp.handlers.ConfigStorage;
import com.woytuloo.accountingapp.handlers.NumberToWordsConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvoiceGenerator2 {

    private final JPanel invoiceDataRenderPanel;
    private ArrayList<InvoiceDataTile> tiles;
    private GridBagConstraints gridBagConstraints;
    private Invoice invoice;
    private Map<String, Integer> automationTextfieldMap;
    private ConfigStorage configStorage;

    public InvoiceGenerator2(JPanel invoiceDataRenderPanel, ConfigStorage configStorage) {
        this.invoiceDataRenderPanel = invoiceDataRenderPanel;
        this.configStorage = configStorage;

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 0, 0, 89);
    }

    public void setupFields(Invoice invoice){
        invoiceDataRenderPanel.removeAll();
        this.invoice = invoice;

        String[] configStr = invoice.getConfigurationDataString().split(",");

        automationTextfieldMap = new HashMap<>();
        loadPriceAutomation(configStr);

        tiles = new ArrayList<>();
        for(int i = 0; i < configStr.length; i++){

            InvoiceDataTile tile;
            if(configStr[i].split(":")[4].isBlank())
                tile = new InvoiceDataTile(configStr[i].split(":")[0], i);
            else
                tile = new InvoiceDataTile(configStr[i].split(":")[0], getAutomationValue(configStr[i].split(":")[4]), i);


            tiles.add(tile);
            invoiceDataRenderPanel.add(tile, gridBagConstraints);
            gridBagConstraints.gridy++;
        }

        loadAutomation();

        GridBagConstraints tempConstr = new GridBagConstraints();
        tempConstr.gridx = 0;
        tempConstr.gridy = gridBagConstraints.gridy;
        tempConstr.weighty = 1;
        tempConstr.anchor = GridBagConstraints.NORTH;
        invoiceDataRenderPanel.add(new JLabel(), tempConstr);

    }

    public void loadAutomation(){
        automationTextfieldMap.forEach((key, value) -> {
            switch (key){
                case "T" -> {tiles.get(automationTextfieldMap.get("T")).getTextField().addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            super.focusLost(e);
                            JTextField textField = (JTextField) e.getSource();
                            String text = textField.getText();
                            if(text.isEmpty())
                                try {
                                    textField.setText((Integer.parseInt(tiles.get(automationTextfieldMap.get("Q")).getTextFieldValue()) * Integer.parseInt(tiles.get(automationTextfieldMap.get("U")).getTextFieldValue())) + "");
                                } catch (NumberFormatException ex) {
                                    textField.setText("0");
                                    JOptionPane.showMessageDialog(null,
                                            "Błędne formatowanie wartości!",
                                            "Błąd",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                        }
                    });

                }
                case "Q" -> {tiles.get(automationTextfieldMap.get("Q")).getTextField().addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (tiles.get(automationTextfieldMap.get("Q")).getTextFieldValue().equals("Podaj Ilość produktów"))
                            tiles.get(automationTextfieldMap.get("Q")).getTextField().setText("");
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        JTextField textField = (JTextField) e.getSource();
                        String text = textField.getText();
                        if (text.isEmpty()) textField.setText("Podaj Ilość produktów");

                    }
                });

                }
                case "U" -> {tiles.get(automationTextfieldMap.get("U")).getTextField().addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (tiles.get(automationTextfieldMap.get("U")).getTextFieldValue().equals("Podaj cenę produktu"))
                            tiles.get(automationTextfieldMap.get("U")).getTextField().setText("");
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        JTextField textField = (JTextField) e.getSource();
                        String text = textField.getText();
                        if (text.isEmpty()) textField.setText("Podaj cenę produktu");
                    }
                                                                                                        }
                );

                }
                case "S" -> {tiles.get(automationTextfieldMap.get("S")).getTextField().addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (tiles.get(automationTextfieldMap.get("S")).getTextFieldValue().equals("Nacisnij by uzyskać kwotę słownie")) {
                            String priceToWord;
                            try{
                                priceToWord = NumberToWordsConverter.numberToWords(Integer.parseInt(tiles.get(automationTextfieldMap.get("T")).getTextFieldValue()));
                            }catch (NumberFormatException ex){
                                priceToWord = NumberToWordsConverter.numberToWords(0);

                                JOptionPane.showMessageDialog(null,
                                        "Błędne formatowanie wartości!",
                                        "Błąd",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            tiles.get(automationTextfieldMap.get("S")).getTextField().setText(priceToWord);
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        JTextField textField = (JTextField) e.getSource();
                        String text = textField.getText();
                        if(text.isEmpty()){
                            String priceToWord;
                            try{
                                priceToWord = NumberToWordsConverter.numberToWords(Integer.parseInt(tiles.get(automationTextfieldMap.get("T")).getTextFieldValue()));
                            }catch (NumberFormatException ex){
                                priceToWord = NumberToWordsConverter.numberToWords(0);


                                JOptionPane.showMessageDialog(null,
                                        "Błędne formatowanie wartości!",
                                        "Błąd",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            tiles.get(automationTextfieldMap.get("S")).getTextField().setText(priceToWord);
                        }
                    }                 }
                );

                }

            }
        });
    }

    public String getAutomationValue(String auto){
        return switch (auto){
            case "D" -> LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            case "N" -> "0";
            case "T" -> "Naciśnij by uzyskać kwotę";
            case "Q" -> "Podaj Ilość produktów";
            case "U" -> "Podaj cenę produktu";
            case "S" -> "Nacisnij by uzyskać kwotę słownie";
            default -> "Nieznana wartość automatyzacji";
        };

    }

    public void loadPriceAutomation(String[] configStr){
        for(int i = 0; i < configStr.length; i++){
            String[] dataSplit = configStr[i].split(":");
            String auto = dataSplit[4];

            if(!auto.isEmpty())
                automationTextfieldMap.put(auto, i);
        }

    }

    public ReadyInvoice scrapData(){

        ReadyInvoice readyInvoice = new ReadyInvoice(invoice);

        for(InvoiceDataTile tile : tiles){
            String paramName = tile.getParameterName();
            String val = tile.getTextFieldValue();
            readyInvoice.addProperty(paramName, val);
        }

        return readyInvoice;

    }



    public void generateInvoice(){

        ReadyInvoice readyInvoice = scrapData();
        readyInvoice.setNumber(configStorage.getCurrentInvoiceNum());

        String[] months = {
                "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
                "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
        };

        Path filePath = Paths.get(this.configStorage.getInvoiceTreePath(), "InvoiceHollow", months[LocalDate.now().getMonthValue() - 1], "" + LocalDateTime.now().getDayOfMonth(), invoice.getName() + configStorage.getCurrentInvoiceNum() + "." + invoice.getExtension());

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        String fileName = invoice.getFile().getName();
        Path srcFilePath = Paths.get(userDocuments, "InvoiceHollow", "Forms", fileName);

        try {
            Files.copy(srcFilePath, filePath);
        } catch (IOException ex) {
            Logger.getLogger(InvoiceGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        File outputFile = new File(filePath.toString());

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(outputFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
        }

        Workbook workbook = null;

        if (invoice.getExtension().equals("xls")) {
            try {
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (invoice.getExtension().equals("xlsx")) {
            try {
                workbook = new XSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        assert workbook != null;

        Sheet sheet = workbook.getSheetAt(0);


        String[] confStr = invoice.getConfigurationDataString().split(",");
        for(String data : confStr){
            String[] dataSplit = data.split(":");

            String paramName = dataSplit[0];
            String cellPosition = dataSplit[1];
            String placeholder = dataSplit[2];
            String alignment = dataSplit[3];

            int rowIndex = Integer.parseInt(cellPosition.replaceAll("[^0-9]", "")) - 1;
            int columnIndex = cellPosition.replaceAll("[^A-Z]", "").charAt(0) - 'A';

            Row row = sheet.getRow(rowIndex);
            if (row == null)
                row = sheet.createRow(rowIndex);


            Cell cell = row.getCell(columnIndex);

            if (cell == null)
                cell = row.createCell(columnIndex);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            switch (alignment) {
                case "L" -> cellStyle.setAlignment(HorizontalAlignment.LEFT);
                case "C" -> cellStyle.setAlignment(HorizontalAlignment.CENTER);
                case "R" -> cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                default -> {}
            }

            cell.setCellStyle(cellStyle);

            String val = readyInvoice.getPropertyDataMap().get(paramName);

            if(!placeholder.isBlank())
                val = placeholder.replace("@", readyInvoice.getPropertyDataMap().get(paramName));

            cell.setCellValue(val);
        }


        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            workbook.write(fileOutputStream);
        } catch (IOException ex) {
            Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
        }



        configStorage.incrementInvoiceNum();


    }





}



