package com.woytuloo.accountingapp.InvoiceManagement;

import java.util.HashMap;
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

    public String getName(){
        return invoiceName;
    }

    public ArchivedInvoice(String initStr){
        propertyDataMap = new HashMap<>();

        String[] split = initStr.split(",");

        number = Integer.parseInt(split[0]);
        invoiceName = split[1];

        for(int i = 2; i < split.length; i++){
            String[] split2 = split[i].split(":");
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
            sb.append(k).append(":").append(v).append(",");
        });

        return number + "," + invoiceName + "," + sb;
    }

    public Map<String, String> getPropertyDataMap() {
        return propertyDataMap;
    }
}
