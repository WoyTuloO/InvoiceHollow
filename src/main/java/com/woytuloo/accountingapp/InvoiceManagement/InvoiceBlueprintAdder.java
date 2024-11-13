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
import javax.swing.JOptionPane;

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
    
    
    public void fillCollection(String name, Map<String, String> cellMap, Map<String, String> autoMap, Map<String, String> cellAliMap){
        if(getFile() == null){
                    JOptionPane.showMessageDialog(null,
                    "Brakuje pliku wejściowego!",
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE);
        }
            
        
        String nameWType = name + "." + getType(srcFile);
        Invoice inv = new Invoice(nameWType, copyBlueprintFile(srcFile,name), cellMap, autoMap, cellAliMap);
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
        Path targetFilePath = Paths.get(userDocuments, "InvoiceHollow" , "Forms", name + "." + type);
        

        try {
            if (Files.notExists(invooFolderPath)) {
                Files.createDirectory(invooFolderPath);
                System.out.println("Folder InvoiceHollow został utworzony.");
                
            }
            if(Files.notExists(Paths.get(invooFolderPath.toString(), "Forms"))){
                Files.createDirectory(Paths.get(invooFolderPath.toString(), "Forms"));
                System.out.println("Folder Forms został utworzony.");
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

    public void addInvoiceFromFile(String name, String path, Map<String, String> cellMap, Map<String, String> autoMap, Map<String, String> cellAlignment){
        Invoice inv = new Invoice(name, path, cellMap, autoMap, cellAlignment);
        this.collection.put(name.split("\\.")[0],inv);
    }
    
    //wczytuje zapisane invoice z pliku FormsData.csv
    
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
                if(line.equals(""))
                    return;
                String [] data = line.split(",");
                String name = data[0];
                String path = data[1];
                Map<String, String> cellMap = new HashMap<>();
                Map<String, String> paramAutoCellMap = new HashMap<>();
                Map<String, String> cellAlignMap = new HashMap<>();
                
                for(int i = 2; i < data.length; i++){
                    String [] cellData = data[i].split(":");
                    
                    cellMap.put(cellData[0], cellData[1]);
                    paramAutoCellMap.put(cellData[0], cellData[2]);
                    cellAlignMap.put(cellData[1], cellData[3]); 
                }
                
                addInvoiceFromFile(name, path, cellMap, paramAutoCellMap,cellAlignMap);
            }
        } catch (IOException ex) {
            Logger.getLogger(InvoiceBlueprintAdder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
}