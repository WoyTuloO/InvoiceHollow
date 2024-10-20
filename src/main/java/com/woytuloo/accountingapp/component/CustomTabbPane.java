/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.component;

import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 *
 * @author wojte
 */
public class CustomTabbPane extends JTabbedPane {
    public CustomTabbPane(){
        init();
    }
    
    
    private void init(){
    this.setBorder(BorderFactory.createEmptyBorder());
    this.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets = new Insets(0, 0, 0, 0);
            }
            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });


    
    }
    
}
