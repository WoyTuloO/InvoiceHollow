package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.charts.ChartsGenerator;
import org.apache.commons.collections4.list.TreeList;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class AutoCompleteHandler {

    private static Map<String, HashSet<String>> paramSuggestionsMap;

    public AutoCompleteHandler(JPanel rememberedJPanel,JComboBox parametersCombo, JTextArea suggestionsTextArea, JButton save, JButton delete) {
        paramSuggestionsMap = new HashMap<>();

        rememberedJPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                parametersCombo.removeAllItems();
                for(String paramName : paramSuggestionsMap.keySet()){
                    parametersCombo.addItem(paramName);
                }
            }
        });

        parametersCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String selectedParam = (String) parametersCombo.getSelectedItem();
                if(selectedParam != null){
                    suggestionsTextArea.setText("");
                    for(String suggestion : paramSuggestionsMap.get(selectedParam)){
                        suggestionsTextArea.append(suggestion + "\n");
                    }
                }
            }
        });

        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String selectedParam = (String) parametersCombo.getSelectedItem();
                if(selectedParam != null){
                    String[] suggestions = suggestionsTextArea.getText().split("\n");
                    paramSuggestionsMap.put(selectedParam, new HashSet<>(Arrays.asList(suggestions)));
                }
            }
        });

        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String selectedParam = (String) parametersCombo.getSelectedItem();
                if(selectedParam != null){
                    paramSuggestionsMap.remove(selectedParam);
                    parametersCombo.removeItem(selectedParam);
                }
            }
        });



        loadSuggestionsFromFile();
    }



    public HashSet<String> getSuggestions(String paramName, String text) {
        HashSet<String> result = new HashSet<>();
        HashSet<String> suggestions = paramSuggestionsMap.getOrDefault(paramName, new HashSet<>());

        for(String word : suggestions){
            if(word.startsWith(text)){
                result.add(word);
            }
        }
        return result;
    }

    public void fillSuggestions(String paramName, String value){
        if(!paramSuggestionsMap.containsKey(paramName))
            paramSuggestionsMap.put(paramName, new HashSet<>());
        HashSet<String> set = paramSuggestionsMap.get(paramName);
        set.add(value);
    }

    public static void saveSuggestionsToFile() {

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path configFilePath = Paths.get(configDirPath.toString(), "autoCompleteSuggestions.csv");

        try {
            FileWriter pw = new FileWriter(new File(configFilePath.toString()));

            StringBuilder sb = new StringBuilder();
            for (String paramName : paramSuggestionsMap.keySet()) {
                sb.append(paramName).append(":");
                for (String suggestion : paramSuggestionsMap.get(paramName)) {
                    sb.append(suggestion).append("$");
                }
                pw.write(sb.substring(0, sb.length() - 1) + "\n");
                sb = new StringBuilder();
            }
            pw.flush();
            pw.close();

        }catch (IOException ex) {
            System.out.println("Bład :" + ex.getMessage());
        }
    }

    public void loadSuggestionsFromFile(){

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path invooFolderPath = Paths.get(userDocuments, "InvoiceHollow");
        Path configDirPath = Paths.get(invooFolderPath.toString(), "Config");
        Path configDataPath = Paths.get(configDirPath.toString(), "autoCompleteSuggestions.csv");


        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(configDataPath.toString()));
            String line;

            while((line = reader.readLine()) != null){
                String[] parts = line.split(":");
                String paramName = parts[0];
                String[] suggestions = parts[1].split("\\$");
                for(String suggestion : suggestions){
                    fillSuggestions(paramName, suggestion);
                }
            }

        } catch (Exception ex) {
            System.out.println("Bład :" + ex.getMessage());
        }


    }

}
