/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.component;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    
    
}
