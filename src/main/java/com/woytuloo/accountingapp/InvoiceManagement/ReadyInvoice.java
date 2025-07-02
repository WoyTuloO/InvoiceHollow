package com.woytuloo.accountingapp.InvoiceManagement;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    public double getTotal(){
        List<String> totalFields = Arrays.stream(invoice.getConfigurationDataString().split(",")).filter(par -> par.split(";")[4].equals("T")).toList();
        double total = -1;
        for(String s : totalFields){
            String[] split = s.split(";");
            String paramName = split[0];
            double totalV = Double.parseDouble(propertyDataMap.get(paramName));
            if(total == -1)
                total = totalV;
            else if(total != totalV)
                return -1;
        }

        return total;

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
            sb.append(k).append(";").append(v).append("|");
        });

        return number + "|" + invoice.getName() + "|" + sb;
    }
}
