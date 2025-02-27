/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.component;

import com.woytuloo.accountingapp.menu.MenuEvent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author wojte
 */
public class ButtonPanel extends JButton {

    private MenuEvent event;

    public ButtonPanel(){
        setFocusPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);

    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color (15,15,15));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
    }
    

    public MenuEvent getEvent() {
        return event;
    }

    public void setEvent(MenuEvent event){
        this.event = event;
    }
    
    
    
    
    
    
}
