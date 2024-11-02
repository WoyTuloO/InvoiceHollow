/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.InvoiceManagement;

import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author WoyTuloo G6X
 */
public class invoiceGenerator {
    
    
    private Map<String, String> paramterValueMap;
    private Invoice invoice;
    private JTable table;
    
    public invoiceGenerator(Invoice inv, JTable tab){
        this.invoice = inv;
        this.table = tab;
        invoice.getParamCellMap().forEach((k,v)->{
            paramterValueMap.put(k, "");        
        });
        fillTable();
    }
    
    public void fillTable(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nazwa parametru");
        model.addColumn("Wartość");
        invoiceCollection.get(invoiceChoiceCombo.getSelectedItem()).getParamCellMap().forEach((paramName, cell)->{
            model.addRow( new Object[]{paramName,""});
        });
        paramValueTable.setModel(model);   
    }
    
    
    
    
    
    
    
}
