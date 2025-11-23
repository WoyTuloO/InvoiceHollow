package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import com.woytuloo.accountingapp.handlers.AutoCompleteHandler;
import com.woytuloo.accountingapp.handlers.ConfigStorage;
import com.woytuloo.accountingapp.handlers.NumberToWordsConvertionHandler;
import com.woytuloo.accountingapp.handlers.StorageHandler;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Adapters {

    public static class ConfigStorageAdapter implements ConfigPort {
        private final ConfigStorage configStorage;
        public ConfigStorageAdapter(ConfigStorage configStorage) { this.configStorage = configStorage; }
        @Override public int getCurrentInvoiceNum() { return ConfigStorage.getCurrentInvoiceNum(); }
        @Override public String getInvoiceTreePath() { return ConfigStorage.getInvoiceTreePath(); }
        @Override public void incrementEarningsAndInvoiceCount(int amount) { configStorage.incrementEarningsAndInvoiceCount(amount); }
        @Override public void updateTotalBy(double delta) { configStorage.updateTotalBy(delta); }
    }

    public static class StorageHandlerArchiveAdapter implements ArchivePort {
    private final StorageHandler storageHandler;
    public StorageHandlerArchiveAdapter(StorageHandler storageHandler) { this.storageHandler = storageHandler; }
    @Override public ArchivedInvoice getByNumber(int number) { return storageHandler.getInvoice(number); }
    @Override public void saveNew(ReadyInvoice readyInvoice) { storageHandler.archiveInvoice(new ArchivedInvoice(readyInvoice)); storageHandler.saveCurrentInvoice(readyInvoice); }
    @Override public void updateExisting(ReadyInvoice readyInvoice) { storageHandler.updateArchive(readyInvoice); storageHandler.saveCurrentInvoice(readyInvoice); }
    }
    
    // Archive repo adapter for querying/filtering
    public static class StorageHandlerArchiveRepoAdapter implements ArchiveRepoPort {
    private final StorageHandler storageHandler;
    public StorageHandlerArchiveRepoAdapter(StorageHandler storageHandler) { this.storageHandler = storageHandler; }
    @Override public java.util.Map<Integer, ArchivedInvoice> all() { return storageHandler.getAllInvoicesView(); }
    @Override public ArchivedInvoice getByNumber(int number) { return storageHandler.getInvoice(number); }
    }

    public static class AutoCompleteSuggestionsAdapter implements SuggestionsPort {
        private final AutoCompleteHandler autoCompleteHandler;
        public AutoCompleteSuggestionsAdapter(AutoCompleteHandler autoCompleteHandler) { this.autoCompleteHandler = autoCompleteHandler; }
        @Override public void rememberSuggestion(String paramName, String value) { autoCompleteHandler.fillSuggestions(paramName, value); }
    }

    public static class PoiDocumentFillerAdapter implements DocumentFillerPort {
        @Override
        public void fill(Invoice invoice, ReadyInvoice readyInvoice, Path filePath) throws IOException {
            String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
            String fileName = invoice.getFile().getName();
            Path srcFilePath = Paths.get(userDocuments, "InvoiceHollow", "Forms", fileName);
            Files.createDirectories(filePath.getParent());
            try { Files.copy(srcFilePath, filePath); } catch (IOException ignored) {}

            File outputFile = filePath.toFile();
            try (FileInputStream fis = new FileInputStream(outputFile)) {
                Workbook workbook;
                if (invoice.getExtension().equals("xls")) {
                    workbook = new HSSFWorkbook(fis);
                } else if (invoice.getExtension().equals("xlsx")) {
                    workbook = new XSSFWorkbook(fis);
                } else {
                    throw new IllegalArgumentException("Nieobsługiwane rozszerzenie: " + invoice.getExtension());
                }

                Sheet sheet = workbook.getSheetAt(0);
                String[] confStr = invoice.getConfigurationDataString().split(",");
                for (String data : confStr) {
                    String[] dataSplit = data.split(";");
                    String paramName = dataSplit[0];
                    String cellPosition = dataSplit[1];
                    String placeholder = dataSplit[2];
                    String alignment = dataSplit[3];
                    String auto = dataSplit.length > 4 ? dataSplit[4] : ".";
                    int rowIndex = Integer.parseInt(cellPosition.replaceAll("[^0-9]", "")) - 1;
                    int columnIndex = cellPosition.toUpperCase().replaceAll("[^A-Z]", "").charAt(0) - 'A';

                    String val = readyInvoice.getPropertyDataMap().get(paramName);
                    if (placeholder.contains("@")) {
                        String replacement = readyInvoice.getPropertyDataMap().get(paramName).isBlank() ? "250" : readyInvoice.getPropertyDataMap().get(paramName);
                        val = placeholder.replace("@", replacement);
                    }

                    Row row = sheet.getRow(rowIndex);
                    if (row == null) row = sheet.createRow(rowIndex);

                    int nl = val.length() / 42;
                    if (nl != 0 && placeholder.length() < 15) {
                        row.setHeightInPoints(row.getHeightInPoints() * (nl + 1));
                        StringBuilder sb = new StringBuilder(val);
                        for (int i = 1; i <= nl; i++) sb.insert(i * 42, "\n");
                        val = sb.toString();
                    }

                    Cell cell = row.getCell(columnIndex);
                    if (cell == null) cell = row.createCell(columnIndex);

                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);

                    Font font = workbook.createFont();
                    font.setFontName("Arial");
                    font.setFontHeightInPoints((short) 10);
                    if ("N".equals(auto)) {
                        font.setFontHeightInPoints((short) 18);
                        font.setBold(true);
                    }
                    cellStyle.setFont(font);

                    switch (alignment) {
                        case "L" -> cellStyle.setAlignment(HorizontalAlignment.LEFT);
                        case "C" -> cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        case "R" -> cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                    }

                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(val);
                }

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    workbook.write(fos);
                }
            }
        }
    }

    public static class SystemDateTimeProvider implements DateTimeProvider {
        @Override public LocalDate today() { return LocalDate.now(); }
        @Override public LocalDateTime now() { return LocalDateTime.now(); }
    }

    public static class PolishNumberToWordsAdapter implements NumberToWordsPort {
        @Override public String numberToWords(int number) { return NumberToWordsConvertionHandler.numberToWords(number); }
    }

    public static class InvoiceBlueprintRepoAdapter implements BlueprintRepoPort {
        @Override public com.woytuloo.accountingapp.InvoiceManagement.Invoice getByName(String name) {
            return com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder.getInvoiceBlueprint(name);
        }
    }
}
