package com.woytuloo.accountingapp.InvoiceManagement;

import java.util.HashMap;
import java.util.Map;


public class ReadyInvoice {
    private int number;
    private Map<String, String> propertyDataMap;
    private Invoice invoice;
    private Map<String, String> parameterCellMap;


    public int getNumber(){
        return number;
    }

    public String getName(){
        return invoice.getName();
    }

    public ReadyInvoice(Invoice invoice){
        this.invoice = invoice;
        propertyDataMap = new HashMap<>();
        parameterCellMap = new HashMap<>();
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

    public void setupParameterCellMap(String confStr){
        String[] split = confStr.split(",");
        for(String s : split){
            String[] split2 = s.split(";");
            parameterCellMap.put(split2[0], split2[1]);
            if("N".equals(split2[4]))
                setFileParameters(split2[0]);

        }
    }

    public void setFileParameters(String property){
        this.number = Integer.parseInt(propertyDataMap.get(property).split("/")[0]);
    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        propertyDataMap.forEach((k, v) -> {
            sb.append(k).append(";").append(v).append(",");
        });

        return number + "," + invoice.getName() + "," + sb;
    }
}
