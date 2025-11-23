package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.charts.ChartsGenerator;

import javax.swing.*;
import java.util.Map;

public class ChartsGeneratorGateway implements ChartsGateway {
    @Override
    public void showIncomeChart(JPanel target, Map<String, Double> incomeMap) {
        ChartsGenerator.showIncomeChart(target, incomeMap);
    }

    @Override
    public void showWorkDoneChart(JPanel target, Map<String, Integer> workDoneMap) {
        ChartsGenerator.showWorkDoneChart(target, workDoneMap);
    }
}
