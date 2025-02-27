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

    private Map<String, HashSet<String>> paramSuggestionsMap;

    public AutoCompleteHandler(){
        paramSuggestionsMap = new HashMap<>();
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

    public void saveSuggestionsToFile(){

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path configFilePath = Paths.get(configDirPath.toString(), "autoCompleteSuggestions.csv");

        try {
            FileWriter pw = new FileWriter(new File(configFilePath.toString()));

            StringBuilder sb = new StringBuilder();
            for(String paramName : paramSuggestionsMap.keySet()){
                sb.append(paramName).append(":");
                for(String suggestion : paramSuggestionsMap.get(paramName)){
                    sb.append(suggestion).append(",");
                }
                pw.write(sb.substring(0, sb.length()-1) + "\n");
            }


            pw.flush();
            pw.close();

        } catch (IOException ex) {
            System.out.println("Bład :" + ex.getMessage());
        }
    }

    public void loadSuggestionsFromFile(){

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path invooFolderPath = Paths.get(userDocuments, "InvoiceHollow");
        Path configDirPath = Paths.get(invooFolderPath.toString(), "Config");
        Path configDataPath = Paths.get(configDirPath.toString(), "appConfig.csv");


        BufferedReader reader;
        String data;
        try {
            reader = new BufferedReader(new FileReader(configDataPath.toString()));



        } catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(null, "Proszę wybrać lokalizację folderu na faktury.", "Wybór folderu", JOptionPane.INFORMATION_MESSAGE);
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz folder w którym będą zapisywane faktury");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = fc.showOpenDialog(null);
//            if (res == JFileChooser.APPROVE_OPTION)
//                setInvoiceTreePath(fc.getSelectedFile().toString());
//
//            this.currentInvoiceNum = Integer.parseInt(JOptionPane.showInputDialog(null, "Podaj numer faktury:", "Numer faktury", JOptionPane.QUESTION_MESSAGE));
//            this.yearlyTarget = Integer.parseInt(JOptionPane.showInputDialog(null, "Podaj roczny cel zarobków:", "Cel zarobków", JOptionPane.QUESTION_MESSAGE));
            if (Files.notExists(invooFolderPath)) {
                try {
                    Files.createDirectory(invooFolderPath);
                } catch (IOException ex1) {
                    System.out.println("Bład tworzenia folderu.");
                }
                System.out.println("Folder InvoiceHollow został utworzony.");
            }

            if (Files.notExists(Paths.get(invooFolderPath.toString(), "Config"))) {
                try {
                    Files.createDirectory(Paths.get(invooFolderPath.toString(), "Config"));
                } catch (IOException ex1) {
                    System.out.println("Bład tworzenia folderu.");
                }
                System.out.println("Folder Config został utworzony.");
            }




        } catch (Exception ex) {
            System.out.println("Bład :" + ex.getMessage());
        }


    }

}
