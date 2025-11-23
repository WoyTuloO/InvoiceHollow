package com.woytuloo.accountingapp.service;

public interface ConfigPort {
    int getCurrentInvoiceNum();
    String getInvoiceTreePath();
    void incrementEarningsAndInvoiceCount(int amount);
    void updateTotalBy(double delta);
}
