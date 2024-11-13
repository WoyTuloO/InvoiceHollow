/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author wojte
 */
public class ConfigStorage {

    /**
     * @return the currentInvoiceNum
     */
    public int getCurrentInvoiceNum() {
        return currentInvoiceNum;
    }

    /**
     * @param currentInvoiceNum the currentInvoiceNum to set
     */
    public void setCurrentInvoiceNum(int currentInvoiceNum) {
        this.currentInvoiceNum = currentInvoiceNum;
    }

    /**
     * @return the cal
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the invoiceTreePath
     */
    public String getInvoiceTreePath() {
        return invoiceTreePath;
    }

    /**
     * @param invoiceTreePath the invoiceTreePath to set
     */
    public void setInvoiceTreePath(String invoiceTreePath) {
        this.invoiceTreePath = invoiceTreePath;
        setupTree();
    }
    
    
    public void setupTree(){
        Path mainInvoiceFolder = Paths.get(this.invoiceTreePath, "InvoiceHollow");
        if (Files.notExists(mainInvoiceFolder)) {
            try {
                Files.createDirectory(mainInvoiceFolder);
            } catch (IOException ex1) {
                Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex1);
            }
            System.out.println("Folder InvoiceHollow został utworzony.");            
        }
        
        
        String[] months = {
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
        };
        
        Path monthInvoicePath = Paths.get(mainInvoiceFolder.toString(), months[LocalDate.now().getMonthValue() - 1]);
        
        if (Files.notExists(monthInvoicePath)) {
            try {
                Files.createDirectory(monthInvoicePath);
            } catch (IOException ex1) {
                Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex1);
            }
            System.out.println("Folder InvoiceHollow został utworzony.");
        }
        
        Path dayInvoicePath = Paths.get(monthInvoicePath.toString(), "" + LocalDateTime.now().getDayOfMonth());
        
        if (Files.notExists(dayInvoicePath)) {
            try {
                Files.createDirectory(dayInvoicePath);
            } catch (IOException ex1) {
                Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex1);
            }
            System.out.println("Folder InvoiceHollow został utworzony.");
        }
    }

    /**
     * @return the invoiceCountandMoney
     */
    public Map<String, Integer[]> getInvoiceCountandMoney() {
        return invoiceCountandMoney;
    }
 
    private int currentInvoiceNum;
    private String date;
    private String invoiceTreePath;
    private Map<String, Integer[]> invoiceCountandMoney;   // <nazwa , {ilość wydanych, dochód całkowity} >
    private Map<String, Double> monthIncomeMap;
    private Map<String, Integer> monthAmmountMap;
    
    
    public ConfigStorage(){
        this.date = setDate();
        this.invoiceCountandMoney = new HashMap<>();
        this.monthIncomeMap = new HashMap<>();
        this.monthAmmountMap = new HashMap<>();
        if(invoiceTreePath != null)
            setupTree();
    }
    
    public void setDefault(){
        currentInvoiceNum = 0;
        invoiceTreePath = System.getProperty("user.home") + File.separator + "Documents";
    }
    
    public String setDate(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return now.format(formatter);
    }
    
    public void loadConfigFile(){                 
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path invooFolderPath = Paths.get(userDocuments, "InvoiceHollow");
        Path configDirPath = Paths.get(invooFolderPath.toString() , "Config" );
        Path configDataPath = Paths.get(configDirPath.toString(),"appConfig.csv");
        
        
        BufferedReader reader;
        String data;
        try {
            reader = new BufferedReader(new FileReader(configDataPath.toString()));
            data = reader.readLine();
            String[] configDataTab = data.split(",");
            this.setCurrentInvoiceNum(Integer.parseInt(configDataTab[0]));              // pierwsza linia zawiera numer ostatniej faktury i sciezke do ich zapisu
            this.setInvoiceTreePath(configDataTab[1]);
            
           
            String[] monthAmmount = reader.readLine().split(",");                       // druga - ile opinii wydane w miesiacu 
            String[] monthIncome = reader.readLine().split(",");                        // trzecia - ile dochodu w miesiacu
            
            
            for(int i = 0; i < 12 ; i++){
                String[] monthAmmountUnit = monthAmmount[i].split(":");
                String[] monthIncomeUnit = monthIncome[i].split(":");
                
                monthAmmountMap.put(monthAmmountUnit[0], Integer.parseInt(monthAmmountUnit[1]));
                monthIncomeMap.put(monthIncomeUnit[0], Double.parseDouble(monthIncomeUnit[1]));
            }
            
            
            while((data = reader.readLine()) != null){                              // kazda kolejna to : nazwaFaktury, ilośćWydanychFaktur, dochódCałkowity
                String[] invoiceData = data.split(",");
                Integer[] invoiceNumbers = {Integer.parseInt(invoiceData[1]), Integer.parseInt(invoiceData[2])};
                getInvoiceCountandMoney().put(invoiceData[0], invoiceNumbers);
            }
            
            setupTree();
            
            
        } catch (FileNotFoundException ex) {
          
            JOptionPane.showMessageDialog(null, "Proszę wybrać lokalizację folderu na faktury.", "Wybór folderu", JOptionPane.INFORMATION_MESSAGE);
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz folder w którym będą zapisywane faktury");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = fc.showOpenDialog(null) ;
            if( res == JFileChooser.APPROVE_OPTION )
                setInvoiceTreePath(fc.getSelectedFile().toString());
            
            if (Files.notExists(invooFolderPath)) {
                try {
                    Files.createDirectory(invooFolderPath);
                } catch (IOException ex1) {
                    Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.out.println("Folder InvoiceHollow został utworzony.");
            }
            
            if(Files.notExists(Paths.get(invooFolderPath.toString(), "Config"))){
                try {
                    Files.createDirectory(Paths.get(invooFolderPath.toString(), "Config"));
                } catch (IOException ex1) {
                    Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.out.println("Folder Config został utworzony.");
            }
            
            if(Files.notExists(Paths.get(configDataPath.toString()))){
                
                try {
                    FileWriter fw = new FileWriter(configDataPath.toString(), true);
                    fw.append("0," + invoiceTreePath +"\n");
                    fw.flush();
                    
                    fw.append("styczeń:0,luty:0,marzec:0,kwiecień:0,maj:0,czerwiec:0,lipiec:0,sierpień:0,wrzesień:0,październik:0,listopad:0,grudzień:0\n");  //month : ammount
                    fw.flush();
                    
                    fw.append("styczeń:0,luty:0,marzec:0,kwiecień:0,maj:0,czerwiec:0,lipiec:0,sierpień:0,wrzesień:0,październik:0,listopad:0,grudzień:0\n");  //month : income
                    fw.flush();
                    
                    fw.close();
                } catch (IOException ex1) {
                    Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
  
        } catch (Exception ex) {
            Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
    
    public void saveConfigToFile(){
        
        
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow" , "Config" );
        Path configFilePath = Paths.get(configDirPath.toString(), "configFile.csv");
        
        try {
            FileWriter pw = new FileWriter(new File(configFilePath.toString()));
            
            
            String in = getCurrentInvoiceNum() + "," +  getInvoiceTreePath().toString() + "\n";
            pw.append(in);
            pw.flush();
            
            
            final StringBuilder sb1 = new StringBuilder();
            monthAmmountMap.forEach((k,v)->{
                    sb1.append(k).append(":").append(v).append(",\n");
            });
            
            pw.append(sb1.toString());
            pw.flush();
            
            
            final StringBuilder sb2 = new StringBuilder();
            monthIncomeMap.forEach((k,v)->{
                    sb2.append(k).append(":").append(v).append(",\n");
            });
            
            pw.append(sb2.toString());
            pw.flush();
            
            getInvoiceCountandMoney().forEach((k, v)->{
                try {
                    pw.append(k+","+ v[0] + "," + v[1] + "\n");
                    pw.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            pw.close();
  
        } catch (IOException ex) {
            Logger.getLogger(ConfigStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
