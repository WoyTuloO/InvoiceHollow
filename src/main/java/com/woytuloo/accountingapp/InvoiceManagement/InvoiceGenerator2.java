package com.woytuloo.accountingapp.InvoiceManagement;

import com.woytuloo.accountingapp.component.InvoiceDataTile;
import com.woytuloo.accountingapp.handlers.NumberToWordsConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.nio.channels.WritePendingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InvoiceGenerator2 {

    private final JPanel invoiceDataRenderPanel;
    private ArrayList<InvoiceDataTile> tiles;
    private GridBagConstraints gridBagConstraints;
    private Invoice invoice;
    private Map<String, Integer> automationTextfieldMap;

    public InvoiceGenerator2(JPanel invoiceDataRenderPanel) {
        this.invoiceDataRenderPanel = invoiceDataRenderPanel;
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

    public void scrapData(){
        for(InvoiceDataTile tile : tiles){
            String paramName = tile.getParameterName();
            String val = tile.getTextFieldValue();




        }


    }

}



