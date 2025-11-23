package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;

import java.util.*;

public class ArchiveService {
    private final ArchiveRepoPort repo;

    public ArchiveService(ArchiveRepoPort repo) {
        this.repo = repo;
    }

    public List<Integer> listAllNumbers() {
        Map<Integer, ArchivedInvoice> all = repo.all();
        List<Integer> ids = new ArrayList<>(all.keySet());
        Collections.sort(ids);
        return ids;
    }

    public List<Integer> filterNumbers(String term) {
        if (term == null || term.isBlank()) {
            return listAllNumbers();
        }
        String lower = term.toLowerCase(Locale.ROOT);
        Map<Integer, ArchivedInvoice> all = repo.all();
        List<Integer> res = new ArrayList<>();
        for (Map.Entry<Integer, ArchivedInvoice> e : all.entrySet()) {
            Map<String, String> data = e.getValue().getPropertyDataMap();
            boolean matches = data.values().stream().anyMatch(v -> v != null && v.toLowerCase(Locale.ROOT).contains(lower));
            if (matches) res.add(e.getKey());
        }
        Collections.sort(res);
        return res;
    }

    public ArchivedInvoice getByNumber(int number) {
        return repo.getByNumber(number);
    }
}

interface ArchiveRepoPort {
    Map<Integer, ArchivedInvoice> all();
    ArchivedInvoice getByNumber(int number);
}
