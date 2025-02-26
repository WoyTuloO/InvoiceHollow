package com.woytuloo.accountingapp.InvoiceManagement;

import java.util.HashMap;
import java.util.Map;


public class ReadyInvoice {
    private int number;
    private Map<String, String> propertyDataMap;
    private Invoice invoice;

    public int getNumber(){
        return number;
    }

    public String getName(){
        return invoice.getName();
    }

    public ReadyInvoice(Invoice invoice){
        this.invoice = invoice;
        propertyDataMap = new HashMap<>();
    }

    public void addProperty(String property, String data){
        propertyDataMap.put(property, data);
    }
    public Map<String, String> getPropertyDataMap(){
        return propertyDataMap;
    }
    public void setNumber(int number){
        this.number = number;
    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        propertyDataMap.forEach((k, v) -> {
            sb.append(k).append(":").append(v).append(",");
        });

        return number + "," + invoice.getName() + "," + sb;
    }
}
