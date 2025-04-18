/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.charts.ChartsGenerator;
import com.woytuloo.accountingapp.component.ControllJButton;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author wojte
 */
public class ConfigStorage {

private static final Logger logger = LogManager.getLogger(ConfigStorage.class);

    private static int currentInvoiceNum;
    private static String invoiceTreePath;
    private Map<String, Integer[]> invoiceCountandMoney = new LinkedHashMap<>();   // <nazwa , {ilość wydanych, dochód całkowity} >
    private static Map<String, Double> monthIncomeMap = new LinkedHashMap<>();
    private static Map<String, Integer> monthAmmountMap = new LinkedHashMap<>();
    private Map<String, String> monthMap = new LinkedHashMap<>();
    private static int yearlyTarget;
    private int currentlyEarned;

    public void setupTree() {
        Path mainInvoiceFolder = Paths.get(this.invoiceTreePath, "InvoiceHollow");
        if (Files.notExists(mainInvoiceFolder)) {
            try {
                Files.createDirectory(mainInvoiceFolder);
            } catch (IOException ex1) {
                System.out.println("Bład tworzenia folderu.");
                logger.error("Bład :" + ex1.getMessage());


            }
            System.out.println("Folder InvoiceHollow został utworzony.");
        }

        String[] months = {
                "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
                "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
        };

        Path monthInvoicePath = Paths.get(mainInvoiceFolder.toString(), months[LocalDate.now().getMonthValue() - 1]);

        if (Files.notExists(monthInvoicePath)) {
            try {
                Files.createDirectory(monthInvoicePath);
            } catch (IOException ex1) {
                System.out.println("Bład tworzenia folderu.");
                logger.error("Bład :" + ex1.getMessage());

            }
            System.out.println("Folder InvoiceHollow został utworzony.");
        }

        Path dayInvoicePath = Paths.get(monthInvoicePath.toString(), "" + LocalDateTime.now().getDayOfMonth());

        if (Files.notExists(dayInvoicePath)) {
            try {
                Files.createDirectory(dayInvoicePath);
            } catch (IOException ex1) {
                System.out.println("Bład tworzenia folderu.");
                logger.error("Bład :" + ex1.getMessage());

            }
            System.out.println("Folder InvoiceHollow został utworzony.");
        }
    }


    public static int getCurrentInvoiceNum() {
        return currentInvoiceNum;
    }

    public void setCurrentInvoiceNum(int currentInvoiceNum) {
        this.currentInvoiceNum = currentInvoiceNum;
    }

    public static String getInvoiceTreePath() {
        return invoiceTreePath;
    }

    public void setInvoiceTreePath(String invoiceTreePath) {
        this.invoiceTreePath = invoiceTreePath;
        setupTree();
    }

    public void increaseCurrentIncomeBy(int coin){
        this.currentlyEarned += coin;

        String month = monthMap.get(LocalDate.now().getMonth().toString());
        monthIncomeMap.put(month, monthIncomeMap.get(month) + coin);

    }

    public void increaseThisMonthsInvoiceCount(){
        String month = monthMap.get(LocalDate.now().getMonth().toString());
        monthAmmountMap.put(month, monthAmmountMap.get(month) + 1);
        currentInvoiceNum += 1;
    }

    public Map<String, Integer[]> getInvoiceCountandMoney() {
        return invoiceCountandMoney;
    }
    public Map<String, Integer> getMonthAmmountMap() {
        return monthAmmountMap;
    }

    public Map<String, Double> getMonthIncomeMap() {
        return monthIncomeMap;
    }

    public int getYearlyTarget() {
        return yearlyTarget;
    }

    public int getCurrentlyEarned() {
        return currentlyEarned;
    }

    public double getThisMonthsTarget() {
        return (double) (yearlyTarget - currentlyEarned) / 12;
    }

    public void setYearlyTarget(int target) {
        this.yearlyTarget = target;
    }

    public double getThisMonthsEarnings() {
        String month = monthMap.get(LocalDate.now().getMonth().toString());
        return monthIncomeMap.getOrDefault(month, 0.0);
    }

    public int getThisMonthsInvoiceCount() {
        return monthAmmountMap.getOrDefault(monthMap.get(LocalDate.now().getMonth().toString()), 0);
    }


    public ConfigStorage(ControllJButton exitButton) {
        monthMap.put("JANUARY", "styczeń");
        monthMap.put("FEBRUARY", "luty");
        monthMap.put("MARCH", "marzec");
        monthMap.put("APRIL", "kwiecień");
        monthMap.put("MAY", "maj");
        monthMap.put("JUNE", "czerwiec");
        monthMap.put("JULY", "lipiec");
        monthMap.put("AUGUST", "sierpień");
        monthMap.put("SEPTEMBER", "wrzesień");
        monthMap.put("OCTOBER", "październik");
        monthMap.put("NOVEMBER", "listopad");
        monthMap.put("DECEMBER", "grudzień");

        this.currentInvoiceNum = 0;
        this.yearlyTarget = 200000;


        setDefault();
        loadConfigFile();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM"));
        System.out.println(date);
        if (date.equals("01.01"))
            reloadForNewYear();

        if (invoiceTreePath != null)
            setupTree();

    }

    private void reloadForNewYear() {
        monthIncomeMap.forEach((k, v) -> {
            monthIncomeMap.put(k, 0.0);
        });

        monthAmmountMap.forEach((k, v) -> {
            monthAmmountMap.put(k, 0);
        });


    }

    public void setDefault() {
        currentInvoiceNum = 0;
        invoiceTreePath = System.getProperty("user.home") + File.separator + "Documents";
    }

    public void loadConfigFile() {
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path invooFolderPath = Paths.get(userDocuments, "InvoiceHollow");
        Path configDirPath = Paths.get(invooFolderPath.toString(), "Config");
        Path configDataPath = Paths.get(configDirPath.toString(), "appConfig.csv");


        BufferedReader reader;
        String data;
        try {
            reader = new BufferedReader(new FileReader(configDataPath.toString()));
            data = reader.readLine();
            String[] configDataTab = data.split(",");
            this.setCurrentInvoiceNum(Integer.parseInt(configDataTab[0]));              // pierwsza linia zawiera numer ostatniej faktury i sciezke do ich zapisu
            this.setInvoiceTreePath(configDataTab[1]);
            this.yearlyTarget = Integer.parseInt(configDataTab[2]);

            String[] monthAmount = reader.readLine().split(",");                       // druga - ile opinii wydane w miesiacu
            String[] monthIncome = reader.readLine().split(",");                        // trzecia - ile dochodu w miesiacu

            for (int i = 0; i < 12; i++) {
                String[] monthAmountUnit = monthAmount[i].split(":");
                String[] monthIncomeUnit = monthIncome[i].split(":");

                int amount = Integer.parseInt(monthAmountUnit[1]);
                double income = Double.parseDouble(monthIncomeUnit[1]);

                currentlyEarned += income;

                monthAmmountMap.put(monthAmountUnit[0], amount);
                monthIncomeMap.put(monthIncomeUnit[0], income);
                invoiceCountandMoney.put(monthIncomeUnit[0], new Integer[]{amount, (int)income});
            }

            setupTree();


        } catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(null, "Proszę wybrać lokalizację folderu na faktury.", "Wybór folderu", JOptionPane.INFORMATION_MESSAGE);
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz folder w którym będą zapisywane faktury");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = fc.showOpenDialog(null);
            if (res == JFileChooser.APPROVE_OPTION)
                setInvoiceTreePath(fc.getSelectedFile().toString());

            this.currentInvoiceNum = Integer.parseInt(JOptionPane.showInputDialog(null, "Podaj numer faktury:", "Numer faktury", JOptionPane.QUESTION_MESSAGE));
            this.yearlyTarget = Integer.parseInt(JOptionPane.showInputDialog(null, "Podaj roczny cel zarobków:", "Cel zarobków", JOptionPane.QUESTION_MESSAGE));
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
                    logger.error("Bład :" + ex1.getMessage());

                }
                System.out.println("Folder Config został utworzony.");
            }

            if (Files.notExists(Paths.get(configDataPath.toString()))) {

                try {
                    FileWriter fw = new FileWriter(configDataPath.toString(), true);
                    fw.append("0,").append(invoiceTreePath).append("\n");
                    fw.flush();

                    fw.append("styczeń:0,luty:0,marzec:0,kwiecień:0,maj:0,czerwiec:0,lipiec:0,sierpień:0,wrzesień:0,październik:0,listopad:0,grudzień:0\n");  //month : ammount
                    fw.flush();

                    fw.append("styczeń:0,luty:0,marzec:0,kwiecień:0,maj:0,czerwiec:0,lipiec:0,sierpień:0,wrzesień:0,październik:0,listopad:0,grudzień:0\n");  //month : income
                    fw.flush();

                    fw.close();

                    fillDefaultMonthMaps();

                } catch (IOException ex1) {
                    System.out.println("Bład :" + ex1.getMessage());
                    logger.error("Bład :" + ex1.getMessage());

                }
            }

        } catch (Exception ex) {
            System.out.println("Bład :" + ex.getMessage());
            logger.error("Bład :" + ex.getMessage());

        }

    }


    private void fillDefaultMonthMaps(){

        monthMap.forEach((k, monthPL) -> {
            monthAmmountMap.put(monthPL, 0);
            monthIncomeMap.put(monthPL, 0.0);
        });
    }

    public static void saveConfigToFile() {

        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        Path configFilePath = Paths.get(configDirPath.toString(), "appConfig.csv");

        try {
            FileWriter pw = new FileWriter(new File(configFilePath.toString()));


            String in = getCurrentInvoiceNum() + "," + getInvoiceTreePath() + "," + yearlyTarget+ "\n";
            pw.append(in);
            pw.flush();


            final StringBuilder sb1 = new StringBuilder();
            monthAmmountMap.forEach((k, v) -> {
                sb1.append(k).append(":").append(v).append(",");
            });

            pw.append(sb1.append("\n"));
            pw.flush();


            final StringBuilder sb2 = new StringBuilder();
            monthIncomeMap.forEach((k, v) -> {
                sb2.append(k).append(":").append(v).append(",");
            });

            pw.append(sb2.append("\n"));
            pw.flush();
            pw.close();

        } catch (IOException ex) {
            System.out.println("Bład :" + ex.getMessage());
            logger.error("Bład :" + ex.getMessage());

        }


    }


    public void incrementEarningsAndInvoiceCount(int i) {
        increaseCurrentIncomeBy(i);
        increaseThisMonthsInvoiceCount();
    }
}
