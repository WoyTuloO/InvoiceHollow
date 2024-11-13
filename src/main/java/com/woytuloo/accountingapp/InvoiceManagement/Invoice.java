/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.InvoiceManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author wojte
 */
public class Invoice {
    
    private String filePath;
    private Map<String, String> propertyCellMap;
    private String name;
    private Map<String, String> paramAutoCellsMap;
    private Map<String, String> cellAlignmentMap;
    
    public Invoice(String nam, String fp, Map<String, String> properties, Map<String, String> paramAutoMap, Map<String, String> cellAlignMap){
        name = nam;         // name.type
        filePath = fp;
        propertyCellMap = properties;
        paramAutoCellsMap = paramAutoMap;
        cellAlignmentMap = cellAlignMap;
    }

 
    public void addProperty(String name, String CellData){
        propertyCellMap.put(name, CellData);
    }
    
    public void editProperty(String name, String CellData){
        propertyCellMap.remove(name);
        if(!CellData.equals(""))
            propertyCellMap.put(name, CellData);
    }
    
    public Map<String, String> getCellAlignmentMap(){
        return this.cellAlignmentMap;
    }
    
    
    
    public String formatToCsv(){
        StringBuilder sb = new StringBuilder();
                
        paramAutoCellsMap.forEach((k,v)-> {
            sb.append(",");
            sb.append(k);                               // nazwa parametru
            sb.append(":");
            String cellName = propertyCellMap.get(k);
            sb.append(cellName);                        // komorka
            sb.append(":");
            sb.append(v);                               // Automatyzacja
            sb.append(":");
            sb.append(cellAlignmentMap.get(cellName));  // Alignment
        });   
        
        
        return name + "," + filePath + sb.toString();
        
    }   
       
    public Map<String, String> getParamCellMap(){
        return this.propertyCellMap;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getName(int num){
        return num +"-"+ this.name;
    }
    
    public void saveToCsv(){
        
        
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow" , "Config" );
        Path formsDataPath = Paths.get(configDirPath.toString(),"FormsData.csv");
        try {
            if (Files.notExists(configDirPath)) {
                Files.createDirectory(configDirPath);
                System.out.println("Folder Config został utworzony.");
            }
            if(Files.notExists(formsDataPath)){
                Files.createFile(formsDataPath);
                System.out.println("Folder FormsData został utworzony.");
            }



            String in = this.formatToCsv() + "\n";
            
            BufferedReader reader = new BufferedReader( new FileReader(formsDataPath.toString()));
            String line;
            while((line = reader.readLine()) != null ) {
                if (line.equals(in)) {
                    return;
                }
            }

            FileWriter pw = new FileWriter(formsDataPath.toString(), true);
            pw.append(in);
            pw.flush();
            pw.close();

        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            e.printStackTrace();
        }

        
        
    }

    /**
     * @return the autoCellsMap
     */
    public Map<String, String> getAutoCellsMap() {
        return paramAutoCellsMap;
    }
    
}
