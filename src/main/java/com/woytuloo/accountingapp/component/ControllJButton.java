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

    public ControllJButton() {
        init();
    }

    private void init() {
        setText("");
        icon = new ImageIcon(getClass().getResource("/Images/closeIcon.png"));
        Image img = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
        this.setFocusable(false);
        this.setBackground(Color.red);
        this.setIcon(new ImageIcon(img));
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e != null) {
                    closeApp();
                }
            }
        });
    }


    private void closeApp() {



        System.exit(0);
    }


}
    
    

