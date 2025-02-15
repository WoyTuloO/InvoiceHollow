package com.woytuloo.accountingapp.InvoiceManagement;

import com.woytuloo.accountingapp.component.InvoiceDataTile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InvoiceGenerator2 {

    private final JPanel invoiceDataRenderPanel;
    private ArrayList<InvoiceDataTile> tiles;
    private java.awt.GridBagConstraints gridBagConstraints;

    public InvoiceGenerator2(JPanel invoiceDataRenderPanel) {
        this.invoiceDataRenderPanel = invoiceDataRenderPanel;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 89);
    }


    public void setupFields(Invoice invoice){
        invoiceDataRenderPanel.removeAll();
        String[] configStr = invoice.getConfigurationDataString().split(",");
        tiles = new ArrayList<>();
        for(int i = 1; i < configStr.length; i++){
            InvoiceDataTile tile = new InvoiceDataTile(configStr[i].split(":")[0], i);
            tiles.add(tile);
            invoiceDataRenderPanel.add(tile, gridBagConstraints);
            gridBagConstraints.gridy++;
        }

        GridBagConstraints tempConstr = new GridBagConstraints();

        tempConstr.gridx = 0;
        tempConstr.gridy = gridBagConstraints.gridy;
        tempConstr.weighty = 1;
        tempConstr.anchor = GridBagConstraints.NORTH;
        invoiceDataRenderPanel.add(new JLabel(), tempConstr);

    }
}
