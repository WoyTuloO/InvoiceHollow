package com.woytuloo.accountingapp.service;

import javax.swing.*;
import java.util.Map;

public interface ChartsGateway {
    void showIncomeChart(JPanel target, Map<String, Double> incomeMap);
    void showWorkDoneChart(JPanel target, Map<String, Integer> workDoneMap);
}
