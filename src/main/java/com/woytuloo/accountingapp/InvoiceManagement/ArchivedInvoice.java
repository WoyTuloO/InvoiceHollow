package com.woytuloo.accountingapp.InvoiceManagement;

import com.woytuloo.accountingapp.handlers.InvoiceBlueprintHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchivedInvoice {
    private int number;
    private String invoiceName;
    private Map<String, String> propertyDataMap;

//
// jak polaczyc archovedInvoice z Invoice??
// czytac z formsData.csv wszystkie szablony i wrzucac do mapy
// pozniej laczyc po nazwie z Invoice przy edycji
//

    public double getTotal(){
        Invoice invoice = InvoiceBlueprintHandler.getInvoiceByName(invoiceName);
        List<String> totalFields = Arrays.stream(invoice.getConfigurationDataString().split(",")).filter(par -> par.split(";")[4].equals("T")).toList();
        double total = 0;
        for(String s : totalFields){
            String[] split = s.split(";");
            String paramName = split[0];
            String data = propertyDataMap.get(paramName);
            if(data == null)
                continue;
            double totalV = Double.parseDouble(data);
            if(total == 0)
                total = totalV;
        }

        return total;
    }


    public String getName(){
        return invoiceName;
    }

    public ArchivedInvoice(String initStr){
        propertyDataMap = new HashMap<>();

        String[] split = initStr.split("\\|");

        number = Integer.parseInt(split[0]);
        invoiceName = split[1];

        for(int i = 2; i < split.length; i++){
            String[] split2 = split[i].split(";");
            if(split2.length < 2)
                split2 = new String[]{split2[0].isBlank() ? "" : split2[0], ""};
            propertyDataMap.put(split2[0], split2[1]);
        }
    }

    public ArchivedInvoice(ReadyInvoice ri){
        this.number = ri.getNumber();
        this.invoiceName = ri.getName();
        this.propertyDataMap = ri.getPropertyDataMap();

    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        propertyDataMap.forEach((k, v) -> {
            sb.append(k).append(";").append(v).append("|");
        });

        return number + "|" + invoiceName + "|" + sb;
    }

    public Map<String, String> getPropertyDataMap() {
        return propertyDataMap;
    }
}
