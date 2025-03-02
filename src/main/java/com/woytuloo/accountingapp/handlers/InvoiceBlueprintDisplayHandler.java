package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;

import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class InvoiceBlueprintDisplayHandler {
    private JPanel background;
    private Map<String, Invoice> collection;
    private JPanel displayPanel;
    private InvoiceGenerator invoicegenerator;
    private CardLayout cardLayout;

    public InvoiceBlueprintDisplayHandler(JPanel background, JPanel displayPanel, Map<String,Invoice> collection, InvoiceGenerator invoiceGenerator, CardLayout cardLayout){
        this.background = background;
        this.collection = collection;
        this.displayPanel = displayPanel;
        this.invoicegenerator = invoiceGenerator;
        this.cardLayout = cardLayout;
    }

    public void addBlueprintToDisplay(Invoice invoice){
        displayPanel.add(new JButton(invoice.getName()));
    }


}
