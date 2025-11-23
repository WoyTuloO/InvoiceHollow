package com.woytuloo.accountingapp.InvoiceManagement;

import com.formdev.flatlaf.ui.FlatComboBoxUI;
import com.woytuloo.accountingapp.component.InvoiceComboDataTile;
import com.woytuloo.accountingapp.handlers.*;
import com.woytuloo.accountingapp.service.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
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
import java.util.List;

public class InvoiceGenerator {

    private static final Logger logger = LogManager.getLogger(InvoiceGenerator.class);
    private final JPanel invoiceDataRenderPanel;
    private ArrayList<InvoiceComboDataTile> tiles;
    private GridBagConstraints gridBagConstraints;
    private Invoice invoice;
    private Map<String, Integer> automationTextfieldMap;
    private ConfigStorage configStorage;
    private AutoCompleteHandler autoCompleteHandler;
    private HashMap<String, Integer> autofillTileMap;
    private HashMap<String, Integer> totalPriceMap;
    private boolean  workingOnArchived = false;
    private InvoiceService invoiceService;
    private Adapters.PolishNumberToWordsAdapter numberToWords;

    String[] months = {
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
    };

    List<String> genericPhrases = new ArrayList<>(List.of("Naciśnij by uzyskać kwotę",
            "Błędne formatowanie wartości",
            "Podaj Ilość produktów",
            "Podaj cenę produktu",
            "Nacisnij by uzyskać kwotę słownie",
            "Autouzupełnianie"));

    public InvoiceGenerator(JButton generateButton , JPanel invoiceDataRenderPanel, ConfigStorage configStorage, StorageHandler storageHandler, AutoCompleteHandler autoCompleteHandler, CardLayout cardLayout, JPanel background, InvoiceService invoiceService, Adapters.PolishNumberToWordsAdapter numberToWords) {
        this.invoiceDataRenderPanel = invoiceDataRenderPanel;
        this.configStorage = configStorage;
        this.autoCompleteHandler = autoCompleteHandler;
        this.autofillTileMap = new HashMap<>();
        this.automationTextfieldMap = new HashMap<>();
        this.totalPriceMap = new HashMap<>();

        // Initialize injected application service and number-to-words port
        this.invoiceService = invoiceService;
        this.numberToWords = numberToWords;

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 0, 0, 89);


        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                if(!workingOnArchived){
                    generateInvoice();
                    cardLayout.show(background,"1 1");
                }
                else {
                    updateInvoice();
                    storageHandler.displayInvoices();
                    cardLayout.show(background,"2 1");
                }}
                catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Błąd generowania faktury.");
                    logger.error("Bład :" + ex.getMessage());
                    cardLayout.show(background,"0 0");
                }
            }
        });
    }

    public void setupFields(Invoice invoice){
        this.workingOnArchived = false;
        invoiceDataRenderPanel.removeAll();
        this.invoice = invoice;
        this.autofillTileMap = new HashMap<>();
        this.automationTextfieldMap = new HashMap<>();
        this.totalPriceMap = new HashMap<>();


        String[] configStr = invoice.getConfigurationDataString().split(",");

        loadPriceAutomation(configStr);

        tiles = new ArrayList<>();
        for(int i = 0; i < configStr.length; i++){

            InvoiceComboDataTile tile;
            String[] split = configStr[i].split(";");
            if(split[4].equals("."))
                tile = new InvoiceComboDataTile(configStr[i].split(";")[0], i);
            else if(split[4].equals("L")){
                tile = new InvoiceComboDataTile(configStr[i].split(";")[0], i, true);
            }else
                tile = new InvoiceComboDataTile(configStr[i].split(";")[0], getAutomationValue(configStr[i].split(";")[4]), i);

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
        BlueprintService blueprintService = new BlueprintService(new Adapters.InvoiceBlueprintRepoAdapter());
        this.invoice = blueprintService.getByName(ai.getName());
        if(invoice == null){
            JOptionPane.showMessageDialog(null, "Nie udało się wczytać szablonu faktury");
            MenuHandler.goToArchiveCard();
        }
        invoiceDataRenderPanel.removeAll();
        Map<String, String> data = new HashMap<>(ai.getPropertyDataMap());

        tiles = new ArrayList<>();
        List<String> l = Arrays.stream(invoice.getConfigurationDataString().split(","))
                .filter(prop -> prop.split(";")[4].equals("L"))
                .map(param -> param.split(";")[0]).toList();
        int i = 0;
        for(String key : data.keySet()){
            InvoiceComboDataTile tile;
            if(l.contains(key))
                tile = new InvoiceComboDataTile(key, i, true, data.get(key));
            else {
                tile = new InvoiceComboDataTile(key, data.get(key), i);
                customizeComboBox(tile.getComboBox());
            }
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
                        comboBox.addItem("");
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
                        Set<String> suggestions = autoCompleteHandler.getSuggestions(tiles.get(autofillTileMap.get(k)).getParameterName(), text);
                        comboBox.removeAllItems();
                        if(!text.isBlank())
                            comboBox.addItem(text);

                        for (String suggestion : suggestions) {
                            if(!text.equals(suggestion))
                                comboBox.addItem(suggestion);
                        }
                    }
                }
            });
        });


        totalPriceMap.forEach((k, v)->{
            JComboBox<String> comboBox = tiles.get(totalPriceMap.get(k)).getComboBox();

            customizeComboBox(comboBox);

            JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

            textField.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent e) {
                    String text = textField.getText();
                    if ("Naciśnij by uzyskać kwotę".equals(text) || "Błędne formatowanie wartości".equals(text)) {
                        comboBox.removeAllItems();
                        try {
                            int qValue = Integer.parseInt(tiles.get(automationTextfieldMap.get("Q")).getComboBoxValue());
                            int uValue = Integer.parseInt(tiles.get(automationTextfieldMap.get("U")).getComboBoxValue());
                            textField.setText(String.valueOf(qValue * uValue));

                        } catch (NumberFormatException ex) {
                            textField.setText("Błędne formatowanie wartości");
                            logger.error("Bład :" + ex.getMessage());

                        }
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
                }}
            });

        });






        automationTextfieldMap.forEach((key, value) -> {
            switch (key){
                case "N" -> {
                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("N")).getComboBox();
                    customizeComboBox(comboBox);
                }

                case "U" -> {
                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("U")).getComboBox();

                    customizeComboBox(comboBox);

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
                }

                case "Q" -> {
                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("Q")).getComboBox();

                    customizeComboBox(comboBox);
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
                }

//                case "T" -> {
//                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("T")).getComboBox();
//
//                    customizeComboBox(comboBox);
//
//                    JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
//
//                    textField.addFocusListener(new FocusListener() {
//
//                        @Override
//                        public void focusGained(FocusEvent e) {
//                            String text = textField.getText();
//                            if ("Naciśnij by uzyskać kwotę".equals(text) || "Błędne formatowanie wartości".equals(text)) {
//                                comboBox.removeAllItems();
//                                textField.setText("");
//                            }
//                        }
//
//                        @Override
//                        public void focusLost(FocusEvent e) {
//                            String text = textField.getText();
//                            if (text.isEmpty()) {
//                                try {
//                                    int qValue = Integer.parseInt(tiles.get(automationTextfieldMap.get("Q")).getComboBoxValue());
//                                    int uValue = Integer.parseInt(tiles.get(automationTextfieldMap.get("U")).getComboBoxValue());
//                                    textField.setText(String.valueOf(qValue * uValue));
//                                } catch (NumberFormatException ex) {
//                                    textField.setText("Błędne formatowanie wartości");
//                                }
//                            }
//                        }
//                    });
//                }

                case "S" -> {


                    JComboBox<String> comboBox = tiles.get(automationTextfieldMap.get("S")).getComboBox();

                    customizeComboBox(comboBox);

                    JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

                    textField.addFocusListener(new FocusListener() {

                        @Override
                        public void focusGained(FocusEvent e) {
                            String text = textField.getText();
                            if ("Nacisnij by uzyskać kwotę słownie".equals(text) || "Błędny format liczby, popraw kwotę całkowitą i spróbuj ponownie".equals(text)) {
                                comboBox.removeAllItems();
                                String priceToWord;
                                try {
                                    int num = totalPriceMap.values().stream().findFirst().orElse(0);
                                    String comboVal = ((JTextField) tiles.get(num).getComboBox().getEditor().getEditorComponent()).getText();
                                    priceToWord = numberToWords.numberToWords(Integer.parseInt(comboVal));
                                } catch (NumberFormatException ex) {
                                    priceToWord = "Błędny format liczby, popraw kwotę całkowitą i spróbuj ponownie";
                                }
                                textField.setText(priceToWord);
                            }
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            String text = textField.getText();
                            if (text.isEmpty()) {
                                String priceToWord;
                                try {
                                    int num = totalPriceMap.values().stream().findFirst().orElse(0);
                                    String comboVal = ((JTextField) tiles.get(num).getComboBox().getEditor().getEditorComponent()).getText();
                                    priceToWord = numberToWords.numberToWords(Integer.parseInt(comboVal));
                                } catch (NumberFormatException ex) {
                                    priceToWord = "Błędny format liczby, popraw kwotę całkowitą i spróbuj ponownie";
                                }
                                textField.setText(priceToWord);
                            }
                        }
                    });
                }
            }
        });
    }

    public void customizeComboBox(JComboBox<String> comboBox){


        comboBox.setUI(new FlatComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    public int getWidth() {
                        return 100;
                    }
                };
            }

            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        size.height = Math.max(size.height, 10);
                        return size;
                    }
                };
            }
        });

        comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                }
            }
        });

        Component arrowButton = comboBox.getComponent(0);
        if (arrowButton instanceof JButton) {
            arrowButton.setEnabled(false);
            for (MouseListener ml : arrowButton.getMouseListeners()) {
                arrowButton.removeMouseListener(ml);
            }
        }
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                comboBox.setPopupVisible(false);
                e.consume();
            }
        });

        Component[] components = comboBox.getComponents();
        for (Component c : components) {
            if (c instanceof JButton) {
                c.setVisible(false);
                c.setBackground(comboBox.getBackground());
            }
        }

        comboBox.revalidate();
        comboBox.repaint();

    }

    public String getAutomationValue(String auto){
        return switch (auto){
            case "D" -> LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            case "N" -> ConfigStorage.getCurrentInvoiceNum() + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
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
            String[] dataSplit = configStr[i].split(";");
            String auto = dataSplit[4];

            if("F".equals(auto)){
                autofillTileMap.put(dataSplit[0], i);
            }else if("T".equals(auto)){
                totalPriceMap.put(dataSplit[0], i);
            }else if("L".equals(auto)){
                //to skip
            }else if(!".".equals(auto))
                automationTextfieldMap.put(auto, i);
        }

    }

    public ReadyInvoice scrapData() {

        ReadyInvoice readyInvoice = new ReadyInvoice(invoice);

        for (InvoiceComboDataTile tile : tiles) {
            String paramName = tile.getParameterName();
            String val;
            if (tile.big)
                val = tile.getComboBoxValue();
            else
                val = ((JTextField) tile.getComboBox().getEditor().getEditorComponent()).getText();

            if (val.isBlank() || genericPhrases.contains(val)) {
                Optional<String> param = Arrays.stream(invoice.getConfigurationDataString().split(","))
                        .filter(par -> par.split(";")[0].equals(paramName))
                        .findFirst();
                if (param.isPresent())
                    val = param.get().split(";")[2];
                String auto;
                if (param.isPresent())
                    auto = param.get().split(";")[4];
                else {
                    auto = "";
                }


                if (val.contains("@")) {
                    Optional<String> def;
                    if (!"S".equals(auto)) {
                        def = Arrays.stream(invoice.getConfigurationDataString().split(","))
                                .filter(par -> par.split(";")[4].equals(auto) && !par.split(";")[2].contains("@"))
                                .findFirst();
                    } else {
                        def = Arrays.stream(invoice.getConfigurationDataString().split(","))
                                .filter(par -> par.split(";")[4].equals("T") && !par.split(";")[2].contains("@"))
                                .findFirst();
                    }
                    if (def.isPresent())
                        if ("S".equals(auto))
                            val = NumberToWordsConvertionHandler.numberToWords(Integer.parseInt(def.get().split(";")[2]));
                        else
                            val = def.get().split(";")[2];

                    else
                        val = "Nieznana wartość!!";
                }
            }


            System.out.println(paramName + " : " + val);
            readyInvoice.addProperty(paramName, val);
        }

        return readyInvoice;
    }
    public void setNumberForInvoice(ReadyInvoice readyInvoice){
        int nr = ConfigStorage.getCurrentInvoiceNum();

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

    private Map<String, String> collectInputs(){
        Map<String, String> inputs = new LinkedHashMap<>();
        for(InvoiceComboDataTile tile : tiles){
            String paramName = tile.getParameterName();
            String val;
            if(tile.big)
                val = tile.getComboBoxValue();
            else
                val = ((JTextField) tile.getComboBox().getEditor().getEditorComponent()).getText();
            inputs.put(paramName, val);
        }
        return inputs;
    }

    public void generateInvoice() throws Exception{
        try {
            Map<String, String> inputs = collectInputs();
            getAutoFillData();
            invoiceService.generateNewInvoice(invoice, inputs);
        } catch (IllegalArgumentException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() != null ? ex.getMessage() : "Błąd generowania faktury.");
            logger.error("Bład :" + ex.getMessage());
            throw ex;
        }

        JOptionPane optionPane = new JOptionPane("Sukces",
                JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog(null, "Sukces");

        Timer timer = new Timer(700, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    public boolean comparePricing(ReadyInvoice readyInvoice, ArchivedInvoice oldInvoice){
        if(oldInvoice != null){
            double oldPrice = oldInvoice.getTotal();
            double newPrice = readyInvoice.getTotal();
            if(oldPrice == -1 || newPrice == -1){
                JOptionPane.showMessageDialog(null, "Niepoprawna suma faktury. Sprawdź dane w formularzu.");
                return false ;
            }

            if(oldPrice != newPrice){
                configStorage.updateTotalBy(newPrice - oldPrice);
            }
        }

        return true;
    }


    public void updateInvoice(){
        try {
            Map<String, String> inputs = collectInputs();
            invoiceService.updateExistingInvoice(invoice, inputs);
        } catch (IllegalArgumentException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() != null ? ex.getMessage() : "Błąd generowania faktury.");
            logger.error("Bład :" + ex.getMessage());
            return;
        }

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
            logger.error("Bład :" + ex.getMessage());
            throw new RuntimeException();
        }

        Workbook workbook = null;
        if (invoice.getExtension().equals("xls")) {
            try {
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                System.out.println("Błąd wyboru pliku");
                logger.error("Bład :" + ex.getMessage());
                throw new RuntimeException();

            }
        }
        if (invoice.getExtension().equals("xlsx")) {
            try {
                workbook = new XSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                System.out.println("Błąd wyboru pliku");
                logger.error("Bład :" + ex.getMessage());
                throw new RuntimeException();

            }
        }

        assert workbook != null;

        Sheet sheet = workbook.getSheetAt(0);

        String[] confStr = invoice.getConfigurationDataString().split(",");
        for(String data : confStr){
            String[] dataSplit = data.split(";");

            String paramName = dataSplit[0];
            String cellPosition = dataSplit[1];
            String placeholder = dataSplit[2];
            String alignment = dataSplit[3];
            String auto = dataSplit[4];
            int rowIndex = Integer.parseInt(cellPosition.replaceAll("[^0-9]", "")) - 1;
            int columnIndex = cellPosition.toUpperCase().replaceAll("[^A-Z]", "").charAt(0) - 'A';

            String val = readyInvoice.getPropertyDataMap().get(paramName);

            if(placeholder.contains("@"))
                val = placeholder.replace("@", readyInvoice.getPropertyDataMap().get(paramName).isBlank() ?
                        "250" : readyInvoice.getPropertyDataMap().get(paramName));




            Row row = sheet.getRow(rowIndex);
            if (row == null)
                row = sheet.createRow(rowIndex);

            int nl = val.length() / 42;
            if(nl != 0 && placeholder.length() < 15){
                row.setHeightInPoints(row.getHeightInPoints() * (nl + 1));

                StringBuilder sb = new StringBuilder(val);
                for(int i = 1; i <= nl; i++){
                    sb.insert(i*42, "\n");
                }

                val = sb.toString() ;
            }

            Cell cell = row.getCell(columnIndex);
            if (cell == null)
                cell = row.createCell(columnIndex);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            var font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 10);

            if("N".equals(auto)){
                font.setFontHeightInPoints((short) 18);
                font.setBold(true);
            }
            cellStyle.setFont(font);

            switch (alignment) {
                case "L" -> cellStyle.setAlignment(HorizontalAlignment.LEFT);
                case "C" -> cellStyle.setAlignment(HorizontalAlignment.CENTER);
                case "R" -> cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                default -> {}
            }

            cell.setCellStyle(cellStyle);
            cell.setCellValue(val);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            workbook.write(fileOutputStream);
        } catch (IOException ex) {
            System.out.println("Błąd zapisu pliku");
            logger.error("Bład :" + ex.getMessage());
            throw new RuntimeException();


        }
    }

}



