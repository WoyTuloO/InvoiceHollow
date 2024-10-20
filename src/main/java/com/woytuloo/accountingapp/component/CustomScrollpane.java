/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.component;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

/**
 *
 * @author wojte
 */
public class CustomScrollpane extends JScrollPane {
    public CustomScrollpane(){
        init();
    }
    
    private void init(){
        this.getViewport().setOpaque(false);
        this.setBackground(new Color(30,30,30));
        this.setBorder(BorderFactory.createEmptyBorder());
        this.getVerticalScrollBar().setPreferredSize(new Dimension(5, Integer.MAX_VALUE));
    }
    
}
