/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.component;

import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author wojte
 */
public class ControllJButton extends JButton {
    private ImageIcon icon;
    
    public ControllJButton(){
        init();
    }
    
    private void init(){
        setText("");
        icon = new ImageIcon (getClass().getResource("/Images/closeIcon.png"));
        Image img = icon.getImage().getScaledInstance(24, 24,  java.awt.Image.SCALE_SMOOTH);
        this.setFocusable(false);
        this.setBackground(Color.red);
        this.setIcon( new ImageIcon(img));
        this.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(e != null){
                    closeApp();
                }
            }
        });
    }
    
    
    private void closeApp(){
        
        //TODO: dodac zachowanie przy zamknieciu
        
        System.exit(0);  
    }
    
    
    private void saveData(){
        
        
        BufferedWriter writer = null;
                
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path invooFolderPath = Paths.get(userDocuments, "InvoiceHollow");
        
        
        Path configDirPath = Paths.get(invooFolderPath.toString(), "Config" );
        Path configDataPath = Paths.get(configDirPath.toString(),"appConfig.csv");
        
        try {
            if (Files.notExists(invooFolderPath)) {
                Files.createDirectory(invooFolderPath);
                System.out.println("Folder InvoiceHollow został utworzony.");
            }
            if(Files.notExists(Paths.get(invooFolderPath.toString(), "Config"))){
                    Files.createDirectory(configDirPath);
                    System.out.println("Folder Config został utworzony.");
                }
            if (!Files.exists(configDataPath)) {
                Files.createFile(configDataPath);
            } 
            
            writer = new BufferedWriter(new FileWriter(configDataPath.toString()));
            StringBuilder sb = new StringBuilder();
            
            sb.append("");      // CurrentInvoiceNumber
            sb.append(",");
            
        } catch (IOException e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
            e.printStackTrace();
        }
        }
        
        
    }
    
    

