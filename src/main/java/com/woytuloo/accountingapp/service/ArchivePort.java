package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;

public interface ArchivePort {
    ArchivedInvoice getByNumber(int number);
    void saveNew(ReadyInvoice readyInvoice);
    void updateExisting(ReadyInvoice readyInvoice);
}
