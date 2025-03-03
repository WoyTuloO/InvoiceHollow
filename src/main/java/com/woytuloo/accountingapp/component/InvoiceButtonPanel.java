/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.component;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Objects;
import javax.swing.*;
import javax.swing.text.Style;

/**
 *
 * @author wojte
 */
public class InvoiceButtonPanel extends JButton {
    Dimension size = new Dimension(210,340);
    Invoice invoice;

    public InvoiceButtonPanel(){
        setFocusPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        ImageIcon iconInvoice = new ImageIcon (getClass().getResource("/Images/InvoiceIcon.png"));
        Image imgInvoice = iconInvoice.getImage().getScaledInstance(92, 92,  java.awt.Image.SCALE_SMOOTH);
        this.setIcon(new ImageIcon(imgInvoice));
        setText("Nazwaa");
        setFont(new Font("Segoe UI", 2, 14));
        setToolTipText("");
        setHideActionText(true);
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        
    }

    public InvoiceButtonPanel(Invoice invoice){
        this.invoice = invoice;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        ImageIcon iconInvoice = new ImageIcon (Objects.requireNonNull(getClass().getResource("/Images/InvoiceIcon.png")));
        Image imgInvoice = iconInvoice.getImage().getScaledInstance(92, 92,  java.awt.Image.SCALE_SMOOTH);
        this.setIcon(new ImageIcon(imgInvoice));
        setText(this.invoice.getName());
        setFont(new Font("Segoe UI", 2, 14));
        setToolTipText("");
        setHideActionText(true);
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    }

    public InvoiceButtonPanel(String name){
        setFocusPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        ImageIcon iconInvoice = new ImageIcon (Objects.requireNonNull(getClass().getResource("/Images/InvoiceIcon.png")));
        Image imgInvoice = iconInvoice.getImage().getScaledInstance(92, 92,  java.awt.Image.SCALE_SMOOTH);
        this.setIcon(new ImageIcon(imgInvoice));
        setText(name);
        setFont(new Font("Segoe UI", 2, 14));
        setToolTipText("");
        setHideActionText(true);
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);


    }

    
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color (15,15,15));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
    }

}
