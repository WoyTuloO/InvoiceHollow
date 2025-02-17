package com.woytuloo.accountingapp.InvoiceManagement;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadyInvoice {
    private Map<String, String> propertyDataMap;
    private Invoice invoice;

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

}
