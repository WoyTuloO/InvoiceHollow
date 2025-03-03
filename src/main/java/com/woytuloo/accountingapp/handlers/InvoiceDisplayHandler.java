package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;

import com.woytuloo.accountingapp.InvoiceManagement.InvoiceGenerator;
import com.woytuloo.accountingapp.component.InvoiceButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class InvoiceDisplayHandler {
    private JPanel background;
    private Map<String, Invoice> collection;
    private JPanel displayPanel;
    private InvoiceGenerator invoicegenerator;
    private CardLayout cardLayout;

    public InvoiceDisplayHandler(JPanel background, JPanel displayPanel, Map<String,Invoice> collection, InvoiceGenerator invoiceGenerator, CardLayout cardLayout){
        this.background = background;
        this.collection = collection;
        this.displayPanel = displayPanel;
        this.invoicegenerator = invoiceGenerator;
        this.cardLayout = cardLayout;
    }

    public void addInvoiceToDisplay(Invoice invoice){
        System.out.println(invoice.toString());
        InvoiceButtonPanel invoiceButtonPanel = new InvoiceButtonPanel(invoice);

        invoiceButtonPanel.addActionListener(e -> {
            invoicegenerator.setupFields(invoice);
            cardLayout.show(background, "fillInvoiceDataCard");
        });
        displayPanel.add(invoiceButtonPanel);
    }

    public void reloadInvoices(){
        displayPanel.removeAll();

        for(Invoice invoice : collection.values()){
            addInvoiceToDisplay(invoice);
        }

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void removeInvoice(){


    }


}