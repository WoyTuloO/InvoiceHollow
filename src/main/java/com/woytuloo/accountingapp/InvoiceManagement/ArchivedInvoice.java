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



    public ArchivedInvoice(String initStr){
        propertyDataMap = new HashMap<>();

        String[] split = initStr.split(",");



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

        return number + "," + invoiceName + "," + sb;
    }
}
