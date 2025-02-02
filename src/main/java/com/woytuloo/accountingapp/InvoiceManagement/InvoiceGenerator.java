/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.InvoiceManagement;

import com.woytuloo.accountingapp.config.ConfigStorage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author WoyTuloo G6X
 */
public class InvoiceGenerator {
    
    private Map<String, String> parameterCellMap = new HashMap<>();
    private Invoice invoice;
    private JTable table;
    private int invoiceNum;                                                             // to jest seed ktory nadpisuje pozniej parametrami ustawionymi z trybem auto
    private Map<String, String> paramAutoMap;
    private ConfigStorage configStorage;
    private InvoiceCalculator invCalc;
    private Map<String, Integer> autoRowMap;
    
    
    public InvoiceGenerator(Invoice inv, JTable tab, ConfigStorage cs){
        this.invoice = inv;
        if(invoice == null) return;
        this.table = tab;
        invoice.getParamCellMap().forEach((k,v)->{this.parameterCellMap.put(k, "");});
        paramAutoMap = invoice.getAutoCellsMap();
        this.configStorage = cs;
        this.invoiceNum = this.configStorage.getCurrentInvoiceNum();
        this.invCalc = new InvoiceCalculator();
        
    }
    
    public String constValSetter(String preVal, String auto){
        StringBuilder sb = new StringBuilder();
        return sb.append(preVal).append(auto).toString();
    }
    
    
    
    public void fillTable(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nazwa parametru");
        model.addColumn("Wartość");
        
        autoRowMap = new HashMap<>();
        
        this.parameterCellMap.forEach((paramName, cell)->{
            
            String automat = paramAutoMap.get(paramName);
            
            model.addRow( new Object[]{paramName,decideCellContent(automat)});
            
            if(automat != null)
                autoRowMap.put(automat, model.getRowCount() - 1);                       
        });
        
        model.addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e){
                int row = e.getFirstRow();
                String paramName = (String)model.getValueAt(row, 0);
                String automation = paramAutoMap.get(paramName);
                
                if((automation) !=null){
                    TableModelListener listener = this;
                    model.removeTableModelListener(listener);

                    try {
                        switch (automation) {
                            case "CJ" -> {
                                Double priceD;
                                try{
                                    priceD = Double.parseDouble((String) model.getValueAt(row, 1));
                                } catch(Exception ex){
                                    return;
                                }
                                
                                invCalc.setPrice(priceD);
                                model.setValueAt(invCalc.getTotalS(), autoRowMap.get("CC"), 1);
                                return;
                                }
                            case "CI" -> {
                                int ammountI;
                                try{
                                    ammountI =  Integer.parseInt((String) model.getValueAt(row, 1));
                                } catch(Exception ex){
                                    return;
                                }
                                
                                invCalc.setAmount(ammountI);
                                model.setValueAt(invCalc.getTotalS(), autoRowMap.get("CC"), 1);
                                return;
                                }
                            case "CC" -> {
                                Double totalD;
                                try{
                                    totalD = Double.parseDouble((String) model.getValueAt(row, 1));
                                } catch(Exception ex){
                                    return;
                                }
                                
                                invCalc.setTotal(totalD);
                                return;
                                }
                            default -> {
                                return;
                            }
                        }
                    } finally {
                        // Po zakończeniu modyfikacji ponownie dodajemy listenera
                        model.addTableModelListener(listener);
                    }
                }
                
            }
            
            
            
            
        });
        
        if(autoRowMap.get("CI") != null)
            model.setValueAt("0", autoRowMap.get("CI"), 1);
        if(autoRowMap.get("CJ") != null)
            model.setValueAt("0.0", autoRowMap.get("CJ"), 1);
        if(autoRowMap.get("CC") != null)
            model.setValueAt("0.0", autoRowMap.get("CC"), 1);
        this.table.setModel(model);
    } 
    
    
    
    public void generateInvoice(){
        setData(invoice.getName(invoiceNum), mapCellToData(invoice.getParamCellMap(), scrapTable()));
        
        
        
        configStorage.setCurrentInvoiceNum(configStorage.getCurrentInvoiceNum() + 1);
    }
    
    
    public String decideCellContent(String type){
        if(type == null) return "";
        
        return switch (type) {
            case "N" -> "" + invoiceNum;
            case "D" -> "" + configStorage.getDate();
            default -> "";
        };
    }
    
    
    
    public HashMap<String, String> scrapTable(){
        TableModel model = table.getModel();
        int rowC = model.getRowCount();
        HashMap<String, String> paramDataMap = new HashMap<>();
        
        for(int i = 0; i < rowC ; i++)
            paramDataMap.put((String) model.getValueAt(i, 0),(String) model.getValueAt(i, 1));
        
        return paramDataMap;
    }
    
    
    
    public Map<String, String> mapCellToData(Map<String, String> paramCell, Map<String, String> paramData){
        Map<String, String> cellDataMap = new HashMap<>();
        
        paramCell.forEach((parName,cell)->{
            cellDataMap.put(cell, paramData.get(parName));
        });
        
        return cellDataMap;
    }

    
    public void setData(String newFileName,Map<String, String> cellDataMap){
        String type = newFileName.split("\\.")[1];
        
        String[] months = {
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
        };
        Path filePath = Paths.get(this.configStorage.getInvoiceTreePath(), "InvoiceHollow", months[LocalDate.now().getMonthValue() - 1], "" + LocalDateTime.now().getDayOfMonth(),newFileName);
        
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path srcFilePath = Paths.get(userDocuments, "InvoiceHollow" , "Forms" , invoice.getName());
        
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
        
        Workbook workbook= null;
        
        if(type.equals("xls")){
            try {
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(type.equals("xlsx")){
            try {
                workbook = new XSSFWorkbook(fileInputStream);
            } catch (IOException ex) {
                Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
        
        
        
        
        Sheet sheet = workbook.getSheetAt(0);
        
        for (Map.Entry<String, String> entry : cellDataMap.entrySet()) {
            String cellPosition = entry.getKey();
            String value = entry.getValue();

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
            
            switch (invoice.getCellAlignmentMap().get(cellPosition)) {
                case "L" -> cellStyle.setAlignment(HorizontalAlignment.LEFT);
                case "C" -> cellStyle.setAlignment(HorizontalAlignment.CENTER);
                case "R" -> cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                default -> {
                }
            }

            cell.setCellStyle(cellStyle);
            cell.setCellValue(value);
        }
        
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            workbook.write(fileOutputStream);
        } catch (IOException ex) {
            Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
    }
    
}
