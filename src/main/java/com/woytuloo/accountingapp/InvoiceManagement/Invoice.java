/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.InvoiceManagement;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wojte
 */
public class Invoice {
    
    private String filePath;
    private Map<String, String> propertyCellMap;
    private String name;
    
    public Invoice(String nam, String fp, Map<String, String> properties){
        name = nam;
        filePath = fp;
        propertyCellMap = properties;
    }
 
    public void addProperty(String name, String CellData){
        propertyCellMap.put(name, CellData);
    }
    
    public void editProperty(String name, String CellData){
        propertyCellMap.remove(name);
        if(!CellData.equals(""))
            propertyCellMap.put(name, CellData);
    }
    
    public String formatToCsv(){
        StringBuilder sb = new StringBuilder();
        propertyCellMap.forEach((k,v)-> {
            sb.append(",");
            sb.append(k);
            sb.append(":");
            sb.append(v);
        });       
        return name + "," + filePath + sb.toString();
        
    }   
    
}
