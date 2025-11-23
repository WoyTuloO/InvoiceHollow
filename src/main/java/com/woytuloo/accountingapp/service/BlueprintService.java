package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;

public class BlueprintService {
    private final BlueprintRepoPort repo;

    public BlueprintService(BlueprintRepoPort repo) {
        this.repo = repo;
    }

    public Invoice getByName(String name) {
        return repo.getByName(name);
    }
}

interface BlueprintRepoPort {
    Invoice getByName(String name);
}
