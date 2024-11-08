/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.InvoiceManagement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author wojte
 */
public class InvoiceBlueprintAdder {
    
    JLabel label;
    File srcFile;
    Map<String, Invoice> collection;
    
    public InvoiceBlueprintAdder(JLabel lab, Map<String, Invoice> coll){
        this.label = lab;
        this.collection = coll;     
    }

    public InvoiceBlueprintAdder(Map<String, Invoice> coll){
        this.collection = coll;
        readData();
    }
    
    public void addNewBlueprint(){
        if((srcFile = setFile()) == null)
            return;
        label.setText(srcFile.getName()); 
    }
    
    public File setFile(){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int res = fc.showOpenDialog(null) ;
        if( res == JFileChooser.APPROVE_OPTION )
            return fc.getSelectedFile();
        return null;
    }
    
    public File getFile(){
        return this.srcFile;
    }
    
    
    public void fillCollection(String name, Map<String, String> map){
        String nameWType = name + "." + getType(srcFile);
        Invoice inv = new Invoice(nameWType, copyBlueprintFile(srcFile,name), map);
        inv.saveToCsv();
        
        this.collection.put(name,inv);
    }
    
    public String getType(File src){
        return src.getName().split("\\.")[1];

    }
    
    public String copyBlueprintFile(File src, String name){
        String type = getType(src);
        
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        
        Path invooFolderPath = Paths.get(userDocuments, "InvoiceHollow");
        Path sourceFilePath = Paths.get(src.getAbsolutePath());
        Path targetFilePath = Paths.get(userDocuments + File.separator + "InvoiceHollow" + File.separator + "Forms", name + "." + type);
        

        try {
            if (Files.notExists(invooFolderPath)) {
                Files.createDirectory(invooFolderPath);
                System.out.println("Folder InvoiceHollow został utworzony.");
                if(Files.notExists(Paths.get(invooFolderPath.toString(), "Forms"))){
                    Files.createDirectory(Paths.get(userDocuments + File.separator + "InvoiceHollow", "Forms"));
                    System.out.println("Folder Forms został utworzony.");
                }
            }
            if (Files.exists(targetFilePath)) {
                System.out.println("Kopia już istnieje.");
            } else {

                Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Plik został skopiowany.");
            }
        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            e.printStackTrace();
        }
        return targetFilePath.toString();
    }  

    
    public void setData(File file, Map<String, String> cellDataMap){
        
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
        }
        Workbook workbook= null;
        try {
            workbook = new HSSFWorkbook(fileInputStream);
        } catch (IOException ex) {
            Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        Sheet sheet = workbook.getSheetAt(0);

        for (Map.Entry<String, String> entry : cellDataMap.entrySet()) {
            String cellPosition = entry.getKey();
            String value = entry.getValue();

            int rowIndex = Integer.parseInt(cellPosition.replaceAll("[^0-9]", "")) - 1;
            int columnIndex = cellPosition.replaceAll("[^A-Z]", "").charAt(0) - 'A';

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }

            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                cell = row.createCell(columnIndex);
            }

            cell.setCellValue(value);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream( new File("C:\\Users\\wojte\\Desktop\\Faktura 4.xls"))) {
            workbook.write(fileOutputStream);
        } catch (IOException ex) {
            Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        }

    public void initialFill(String name, String path, Map<String, String> map){
        Invoice inv = new Invoice(name, path, map);
        this.collection.put(name.split("\\.")[0],inv);
    }

         public void readData(){
             BufferedReader reader = null;

             String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
             Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow" , "Config" );
             Path formsDataPath = Paths.get(configDirPath.toString(),"FormsData.csv");

             try {
                if(Files.notExists(configDirPath))
                    return;

                if(Files.notExists(formsDataPath))
                    return;



                reader = new BufferedReader(new FileReader(formsDataPath.toString()));
                String line;
                while((line = reader.readLine()) != null){
                    String [] data = line.split(",");
                    String name = data[0];
                    String path = data[1];
                    Map<String, String> map = new HashMap<>();
                    for(int i = 2; i < data.length; i++){
                        String [] cellData = data[i].split(":");
                        map.put(cellData[0], cellData[1]);
                    }
                    initialFill(name, path, map);
                }
            } catch (IOException ex) {
                Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
            }


         }
    }
    
    
    

