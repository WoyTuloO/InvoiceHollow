package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import com.woytuloo.accountingapp.component.InvoiceBlueprintPanel;
import com.woytuloo.accountingapp.component.InvoiceButtonPanel;

import javax.swing.*;
import java.util.HashMap;

public class InvoiceBlueprintHandler {

    private JPanel savedBlueprintDisplayPanel;
    private static HashMap<String, Invoice> collection;
    private InvoiceBlueprintAdder invoiceBlueprintAdder;

    public static Invoice getInvoiceByName(String name){ return collection.get(name); }

    public InvoiceBlueprintHandler(JPanel savedBlueprintCard, JPanel savedBlueprintDisplayPanel, HashMap<String, Invoice> collection, InvoiceBlueprintAdder invoiceBlueprintAdder) {
        this.savedBlueprintDisplayPanel = savedBlueprintDisplayPanel;
        this.collection = collection;
        this.invoiceBlueprintAdder = invoiceBlueprintAdder;

        savedBlueprintCard.addComponentListener( new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                reloadBlueprints();
            }
        });

    }

    private void reloadBlueprints() {
        savedBlueprintDisplayPanel.removeAll();
        for(Invoice invoice : collection.values()){
            InvoiceBlueprintPanel invoiceBlueprintPanel = new InvoiceBlueprintPanel(invoice);

            invoiceBlueprintPanel.addActionListener(
                    e -> {
                        invoiceBlueprintAdder.setupEdit(invoice);
                    }
            );

            invoiceBlueprintPanel.getRemoveButton().addActionListener(
                    e -> {
                        removeInvoice(invoice);
                        reloadBlueprints();
                    }
            );

            savedBlueprintDisplayPanel.add(invoiceBlueprintPanel);
        }

        savedBlueprintDisplayPanel.revalidate();
        savedBlueprintDisplayPanel.repaint();
    }


    public void removeInvoice(Invoice invoice){
        int response = JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunąć szablon?", "Potwierdzenie usunięcia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            deleteInvoice(invoice.getName());
        } else {
            JOptionPane.showMessageDialog(null, "Usunięcie szablonu anulowane.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteInvoice(String invoiceName) {
        collection.remove(invoiceName);
    }



}
