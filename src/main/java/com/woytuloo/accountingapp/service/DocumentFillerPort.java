package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentFillerPort {
    void fill(Invoice invoice, ReadyInvoice readyInvoice, Path outputPath) throws IOException;
}
