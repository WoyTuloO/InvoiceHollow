package com.woytuloo.accountingapp.InvoiceManagement;

import com.woytuloo.accountingapp.component.InvoiceComboDataTile;
import com.woytuloo.accountingapp.component.InvoiceDataTile;
import com.woytuloo.accountingapp.handlers.AutoCompleteHandler;
import com.woytuloo.accountingapp.handlers.ConfigStorage;
import com.woytuloo.accountingapp.handlers.NumberToWordsConvertionHandler;
import com.woytuloo.accountingapp.handlers.StorageHandler;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InvoiceGenerator {

    private final JPanel invoiceDataRenderPanel;
    private ArrayList<InvoiceComboDataTile> tiles;
    private GridBagConstraints gridBagConstraints;
    private Invoice invoice;
    private Map<String, Integer> automationTextfieldMap;
    private ConfigStorage configStorage;
    private StorageHandler storageHandler;
    private AutoCompleteHandler autoCompleteHandler;
    private HashMap<String, Integer> autofillTileMap;
    private boolean  workingOnArchived = false;

    String[] months = {
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
    };


    public InvoiceGenerator(JButton generateButton ,JPanel invoiceDataRenderPanel, ConfigStorage configStorage, StorageHandler storageHandler, AutoCompleteHandler autoCompleteHandler) {
        this.invoiceDataRenderPanel = invoiceDataRenderPanel;
        this.configStorage = configStorage;
        this.autoCompleteHandler = autoCompleteHandler;
        this.storageHandler = storageHandler;
        this.autofillTileMap = new HashMap<>();
        automationTextfieldMap = new HashMap<>();

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 0, 0, 89);


        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!workingOnArchived)
                    generateInvoice();
                else
                    updateInvoice();
            }
        });

    }

    public void setupFields(Invoice invoice){
        this.workingOnArchived = false;
        invoiceDataRenderPanel.removeAll();
        this.invoice = invoice;
        this.autofillTileMap = new HashMap<>();
        this.automationTextfieldMap = new HashMap<>();


        String[] configStr = invoice.getConfigurationDataString().split(",");

        loadPriceAutomation(configStr);

        tiles = new ArrayList<>();
        for(int i = 0; i < configStr.length; i++){

            InvoiceComboDataTile tile;
            String[] split = configStr[i].split(":");
            if(split[4].equals("."))
                tile = new InvoiceComboDataTile(configStr[i].split(":")[0], i);
            else
                tile = new InvoiceComboDataTile(configStr[i].split(":")[0], getAutomationValue(configStr[i].split(":")[4]), i);

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

    public void setupArchivedFields(ArchivedInvoice ai){


        this.autofillTileMap = new HashMap<>();
        this.automationTextfieldMap = new HashMap<>();

        this.workingOnArchived = true;
        this.invoice = InvoiceBlueprintAdder.getInvoiceBlueprint(ai.getName());
        if(invoice == null){
            JOptionPane.showMessageDialog(null, "Nie udało się wczytać szablonu faktury");
            return;
        }
        invoiceDataRenderPanel.removeAll();
        Map<String, String> data = new HashMap<>(ai.getPropertyDataMap());

        tiles = new ArrayList<>();

        int i = 0;
        for(String key : data.keySet()){
            InvoiceComboDataTile tile = new InvoiceComboDataTile(key, data.get(key), i);
            i++;
            tiles.add(tile);
            invoiceDataRenderPanel.add(tile, gridBagConstraints);
            gridBagConstraints.gridy++;
        }

        GridBagConstraints tempConstr = new GridBagConstraints();
        tempConstr.gridx = 0;
        tempConstr.gridy = gridBagConstraints.gridy;
        tempConstr.weighty = 1;
        tempConstr.anchor = GridBagConstraints.NORTH;
        invoiceDataRenderPanel.add(new JLabel(), tempConstr);
    }


    public void loadAutomation(){

        autofillTileMap.forEach((k,v)->{

            JComboBox<String> comboBox = tiles.get(autofillTileMap.get(k)).getComboBox();
            JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    String text = textField.getText();
                    if ("Autouzupełnianie".equals(text)) {
                        comboBox.removeAllItems();
                        textField.setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    String text = textField.getText();
                    if (text.isEmpty()) {
                        comboBox.addItem("Autouzupełnianie");
                        textField.setText("Autouzupełnianie");
                    }
                }
            });

            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        String text = textField.getText();
                        HashSet<String> suggestions = autoCompleteHandler.getSuggestions(tiles.get(autofillTileMap.get(k)).getParameterName(), text);
                        comboBox.removeAllItems();
                        comboBox.addItem(text);
                        for (String suggestion : suggestions) {
                            comboBox.addItem(suggestion);
                        }
                    }
                }
            });

        });




        automationTextfieldMap.forEach((key, value) -> {
            switch (key){
                case "U" -> {
                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("U")).getComboBox();
                    JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

                    textField.addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            String text = textField.getText();
                            if ("Podaj cenę produktu".equals(text)) {
                                comboBox.removeAllItems();
                                textField.setText("");
                            }
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            String text = textField.getText();
                            if (text.isEmpty()) {
                                comboBox.removeAllItems();
                                comboBox.addItem("Podaj cenę produktu");
                                textField.setText("Podaj cenę produktu");
                            }
                        }
                    });

                    comboBox.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                            ((JComboBox<?>) e.getSource()).hidePopup();
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {}
                    });
                }

                case "Q" -> {
                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("Q")).getComboBox();
                    JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

                    textField.addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            String text = textField.getText();
                            if ("Podaj Ilość produktów".equals(text)) {
                                comboBox.removeAllItems();
                                textField.setText("");
                            }
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            String text = textField.getText();
                            if (text.isEmpty()) {
                                comboBox.removeAllItems();
                                comboBox.addItem("Podaj Ilość produktów");
                                textField.setText("Podaj Ilość produktów");
                            }
                        }
                    });
                    comboBox.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                            ((JComboBox<?>) e.getSource()).hidePopup();
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {}
                    });
                }

                case "T" -> {
                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("T")).getComboBox();
                    JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

                    textField.addFocusListener(new FocusListener() {

                        @Override
                        public void focusGained(FocusEvent e) {
                            String text = textField.getText();
                            if ("Naciśnij by uzyskać kwotę".equals(text) || "Błędne formatowanie wartości".equals(text)) {
                                comboBox.removeAllItems();
                                textField.setText("");
                            }
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            String text = textField.getText();
                            if (text.isEmpty()) {
                                try {
                                    int qValue = Integer.parseInt(tiles.get(automationTextfieldMap.get("Q")).getComboBoxValue());
                                    int uValue = Integer.parseInt(tiles.get(automationTextfieldMap.get("U")).getComboBoxValue());
                                    textField.setText(String.valueOf(qValue * uValue));
                                } catch (NumberFormatException ex) {
                                    textField.setText("Błędne formatowanie wartości");
                                }
                            }
                        }
                    });
                    comboBox.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                            ((JComboBox<?>) e.getSource()).hidePopup();
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {}
                    });
                }

                case "S" -> {
                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("S")).getComboBox();
                    JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

                    textField.addFocusListener(new FocusListener() {

                        @Override
                        public void focusGained(FocusEvent e) {
                            String text = textField.getText();
                            if ("Nacisnij by uzyskać kwotę słownie".equals(text) || "Błędny format liczby, popraw kwotę całkowitą i spróbuj ponownie".equals(text)) {
                                comboBox.removeAllItems();
                                textField.setText("");
                            }
                        }

                        @Override
                        public void focusLost(FocusEvent e) {

                            String text = textField.getText();
                            if (text.isEmpty()) {
                                String priceToWord;
                                try {
                                    String comboVal = ((JTextField) tiles.get(automationTextfieldMap.get("T")).getComboBox().getEditor().getEditorComponent()).getText();
                                    priceToWord = NumberToWordsConvertionHandler.numberToWords(Integer.parseInt(comboVal));
                                } catch (NumberFormatException ex) {
                                    priceToWord = "Błędny format liczby, popraw kwotę całkowitą i spróbuj ponownie";
                                }
                                textField.setText(priceToWord);
                            }
                        }
                    });
                    comboBox.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                            ((JComboBox<?>) e.getSource()).hidePopup();
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {}
                    });
                }

            }
        });
    }

    public String getAutomationValue(String auto){
        return switch (auto){
            case "D" -> LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            case "N" -> configStorage.getCurrentInvoiceNum() + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
            case "T" -> "Naciśnij by uzyskać kwotę";
            case "Q" -> "Podaj Ilość produktów";
            case "U" -> "Podaj cenę produktu";
            case "S" -> "Nacisnij by uzyskać kwotę słownie";
            case "F" -> "Autouzupełnianie";
            default -> "Nieznana wartość automatyzacji";
        };

    }

    public void loadPriceAutomation(String[] configStr){
        for(int i = 0; i < configStr.length; i++){
            String[] dataSplit = configStr[i].split(":");
            String auto = dataSplit[4];

            if("F".equals(auto)){
                autofillTileMap.put(dataSplit[0], i);
            }else if(!auto.equals("."))
                automationTextfieldMap.put(auto, i);
        }

    }

    public ReadyInvoice scrapData(){

        ReadyInvoice readyInvoice = new ReadyInvoice(invoice);

        for(InvoiceComboDataTile tile : tiles){
            String paramName = tile.getParameterName();
            String val = ((JTextField) tile.getComboBox().getEditor().getEditorComponent()).getText();
            System.out.println(paramName + " : " + val);
            readyInvoice.addProperty(paramName, val);
        }

        return readyInvoice;
    }

    public void setNumberForInvoice(ReadyInvoice readyInvoice){
        int nr = configStorage.getCurrentInvoiceNum();

        try {
            nr = Integer.parseInt(((JTextField) tiles.get(automationTextfieldMap.get("N")).getComboBox().getEditor().getEditorComponent()).getText().split("/")[0]);
        } catch (NumberFormatException ex) {
            System.out.println("Nie udało się pobrać numeru faktury - ustawianie domyślnego numeru");
        }

        readyInvoice.setNumber(nr);
    }

    private void getAutoFillData() {

        autofillTileMap.forEach((k,v)->{
            JComboBox<String> comboBox = tiles.get(autofillTileMap.get(k)).getComboBox();
            JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
            autoCompleteHandler.fillSuggestions(tiles.get(autofillTileMap.get(k)).getParameterName(), textField.getText());
        });
    }

    public void generateInvoice(){

        ReadyInvoice readyInvoice = scrapData();
        setNumberForInvoice(readyInvoice);
        getAutoFillData();


        Path filePath = Paths.get(this.configStorage.getInvoiceTreePath(), "InvoiceHollow", months[LocalDate.now().getMonthValue() - 1], "" + LocalDateTime.now().getDayOfMonth(), invoice.getName() + readyInvoice.getNumber() + "." + invoice.getExtension());

        fillInvoice(readyInvoice, filePath);

        storageHandler.archiveInvoice(new ArchivedInvoice(readyInvoice));
        storageHandler.saveCurrentInvoice(readyInvoice);
        configStorage.incrementInvoiceNum();

        JOptionPane optionPane = new JOptionPane("Sukces",
                JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog(null, "");

        Timer timer = new Timer(700, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);


    }

    public void updateInvoice(){

        ReadyInvoice readyInvoice = scrapData();
        readyInvoice.setupParameterCellMap(invoice.getConfigurationDataString());
        Path filePath = Paths.get(ConfigStorage.getInvoiceTreePath(), "InvoiceHollow", months[LocalDate.now().getMonthValue() - 1], "" + LocalDateTime.now().getDayOfMonth(), invoice.getName() + readyInvoice.getNumber() + "." + invoice.getExtension());

        fillInvoice(readyInvoice, filePath);

        storageHandler.updateArchive(readyInvoice);
        storageHandler.saveCurrentInvoice(readyInvoice);

        JOptionPane optionPane = new JOptionPane("Sukces",
                JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog(null, "Sukces");

        Timer timer = new Timer(700, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }



    public void fillInvoice(ReadyInvoice readyInvoice, Path filePath){
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        String fileName = invoice.getFile().getName();
        Path srcFilePath = Paths.get(userDocuments, "InvoiceHollow", "Forms", fileName);

        try {
            Files.copy(srcFilePath, filePath);
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(null,"Czy na pewno chcesz nadpisać plik?", "Plik już istnieje", JOptionPane.YES_NO_OPTION);
        }

        File outputFile = new File(filePath.toString());
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(outputFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Błąd odczytu pliku");
            return;
        }

        Workbook workbook = null;
        if (invoice.getExtension().equals("xls")) {
            try {
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                System.out.println("Błąd wyboru pliku");
            }
        }
        if (invoice.getExtension().equals("xlsx")) {
            try {
                workbook = new XSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                System.out.println("Błąd wyboru pliku");
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
            int columnIndex = cellPosition.toUpperCase().replaceAll("[^A-Z]", "").charAt(0) - 'A';

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
            System.out.println("Błąd zapisu pliku");
        }
    }

}



