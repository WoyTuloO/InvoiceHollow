package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TotalCalculatorService {
    private final BlueprintRepoPort blueprintRepo;

    public TotalCalculatorService(BlueprintRepoPort blueprintRepo) {
        this.blueprintRepo = blueprintRepo;
    }

    public double computeTotal(String invoiceName, Map<String, String> propertyDataMap) {
        Invoice invoice = blueprintRepo.getByName(invoiceName);
        if (invoice == null) return -1;
        List<String> totalFields = Arrays.stream(invoice.getConfigurationDataString().split(","))
                .filter(par -> par.split(";")[4].equals("T")).toList();
        double total = -1;
        for (String s : totalFields) {
            String[] split = s.split(";");
            String paramName = split[0];
            String data = propertyDataMap.get(paramName);
            if (data == null || data.isBlank()) continue;
            double totalV;
            try { totalV = Double.parseDouble(data); } catch (NumberFormatException ex) { return -1; }
            if (total < 0) total = totalV;
        }
        return total;
    }
}
