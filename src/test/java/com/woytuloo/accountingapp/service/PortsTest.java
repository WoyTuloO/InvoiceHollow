package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class PortsTest {

    // ============ ConfigPort Tests ============

    @Test
    void configPort_getCurrentInvoiceNum_returns_valid_number() {
        ConfigPort port = new TestConfigPort(42);
        assertEquals(42, port.getCurrentInvoiceNum());
    }

    @Test
    void configPort_getInvoiceTreePath_returns_valid_path() {
        String expectedPath = "/home/user/invoices";
        ConfigPort port = new TestConfigPort(1, expectedPath);
        assertEquals(expectedPath, port.getInvoiceTreePath());
    }

    @Test
    void configPort_incrementEarningsAndInvoiceCount_updates_state() {
        TestConfigPort port = new TestConfigPort(1);
        port.incrementEarningsAndInvoiceCount(500);
        assertEquals(500, port.getEarningsAdded());
        assertEquals(1, port.getInvoiceCountAdded());
    }

    @Test
    void configPort_updateTotalBy_accumulates_delta() {
        TestConfigPort port = new TestConfigPort(1);
        port.updateTotalBy(100.5);
        port.updateTotalBy(-50.25);
        assertEquals(50.25, port.getTotalDelta(), 0.001);
    }

    // ============ ArchivePort Tests ============

    @Test
    void archivePort_saveNew_persists_invoice() throws IOException {
        ArchivePort port = new TestArchivePort();
        Invoice inv = createTestInvoice("Test", "xlsx", "Param;A1;val;C;.");
        ReadyInvoice ready = new ReadyInvoice(inv);
        ready.addProperty("Param", "value");
        ready.setNumber(1);

        port.saveNew(ready);

        ArchivedInvoice retrieved = port.getByNumber(1);
        assertNotNull(retrieved);
        assertEquals("Test", retrieved.getName());
    }

    @Test
    void archivePort_updateExisting_modifies_invoice() throws IOException {
        ArchivePort port = new TestArchivePort();
        Invoice inv = createTestInvoice("Test", "xlsx", "Param;A1;val;C;.");
        ReadyInvoice ready = new ReadyInvoice(inv);
        ready.addProperty("Param", "original");
        ready.setNumber(5);
        port.saveNew(ready);

        ReadyInvoice updated = new ReadyInvoice(inv);
        updated.addProperty("Param", "modified");
        updated.setNumber(5);
        port.updateExisting(updated);

        ArchivedInvoice retrieved = port.getByNumber(5);
        assertEquals("modified", retrieved.getPropertyDataMap().get("Param"));
    }

    @Test
    void archivePort_getByNumber_returns_null_for_missing() {
        ArchivePort port = new TestArchivePort();
        assertNull(port.getByNumber(999));
    }

    // ============ SuggestionsPort Tests ============

    @Test
    void suggestionsPort_rememberSuggestion_stores_value() {
        SuggestionsPort port = new TestSuggestionsPort();
        port.rememberSuggestion("Client", "ACME Corp");
        port.rememberSuggestion("Client", "Beta Inc");
        port.rememberSuggestion("Address", "123 Main St");

        TestSuggestionsPort testPort = (TestSuggestionsPort) port;
        assertTrue(testPort.getSuggestions("Client").contains("ACME Corp"));
        assertTrue(testPort.getSuggestions("Client").contains("Beta Inc"));
        assertTrue(testPort.getSuggestions("Address").contains("123 Main St"));
    }

    @Test
    void suggestionsPort_rememberSuggestion_ignores_duplicates() {
        SuggestionsPort port = new TestSuggestionsPort();
        port.rememberSuggestion("Client", "ACME");
        port.rememberSuggestion("Client", "ACME");
        port.rememberSuggestion("Client", "ACME");

        TestSuggestionsPort testPort = (TestSuggestionsPort) port;
        assertEquals(1, testPort.getSuggestions("Client").size());
    }

    // ============ DateTimeProvider Tests ============

    @Test
    void dateTimeProvider_today_returns_date() {
        LocalDate expected = LocalDate.of(2024, 5, 15);
        DateTimeProvider provider = new TestDateTimeProvider(expected, LocalDateTime.of(2024, 5, 15, 10, 30));
        assertEquals(expected, provider.today());
    }

    @Test
    void dateTimeProvider_now_returns_datetime() {
        LocalDateTime expected = LocalDateTime.of(2024, 5, 15, 14, 45, 30);
        DateTimeProvider provider = new TestDateTimeProvider(LocalDate.of(2024, 5, 15), expected);
        assertEquals(expected, provider.now());
    }

    // ============ NumberToWordsPort Tests ============

    @Test
    void numberToWordsPort_converts_single_digit() {
        NumberToWordsPort port = new TestNumberToWordsPort();
        String result = port.numberToWords(5);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void numberToWordsPort_converts_large_number() {
        NumberToWordsPort port = new TestNumberToWordsPort();
        String result = port.numberToWords(12345);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void numberToWordsPort_converts_zero() {
        NumberToWordsPort port = new TestNumberToWordsPort();
        String result = port.numberToWords(0);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // ============ DocumentFillerPort Tests ============

    @Test
    void documentFillerPort_fill_called_with_valid_params() throws IOException {
        DocumentFillerPort port = new TestDocumentFillerPort();
        Invoice inv = createTestInvoice("Test", "xlsx", "Param;A1;val;C;.");
        ReadyInvoice ready = new ReadyInvoice(inv);
        ready.addProperty("Param", "value");
        Path outputPath = Paths.get(System.getProperty("java.io.tmpdir"), "test.xlsx");

        port.fill(inv, ready, outputPath);

        TestDocumentFillerPort testPort = (TestDocumentFillerPort) port;
        assertTrue(testPort.wasCalled());
    }

    // ============ Helper Classes ============

    private static class TestConfigPort implements ConfigPort {
        private final int currentNum;
        private final String treePath;
        private int earningsAdded = 0;
        private int invoiceCountAdded = 0;
        private double totalDelta = 0;

        TestConfigPort(int currentNum) {
            this(currentNum, System.getProperty("java.io.tmpdir"));
        }

        TestConfigPort(int currentNum, String treePath) {
            this.currentNum = currentNum;
            this.treePath = treePath;
        }

        @Override
        public int getCurrentInvoiceNum() {
            return currentNum;
        }

        @Override
        public String getInvoiceTreePath() {
            return treePath;
        }

        @Override
        public void incrementEarningsAndInvoiceCount(int amount) {
            earningsAdded += amount;
            invoiceCountAdded++;
        }

        @Override
        public void updateTotalBy(double delta) {
            totalDelta += delta;
        }

        int getEarningsAdded() {
            return earningsAdded;
        }

        int getInvoiceCountAdded() {
            return invoiceCountAdded;
        }

        double getTotalDelta() {
            return totalDelta;
        }
    }

    private static class TestArchivePort implements ArchivePort {
        private final Map<Integer, ArchivedInvoice> db = new HashMap<>();

        @Override
        public ArchivedInvoice getByNumber(int number) {
            return db.get(number);
        }

        @Override
        public void saveNew(ReadyInvoice readyInvoice) {
            db.put(readyInvoice.getNumber(), new ArchivedInvoice(readyInvoice));
        }

        @Override
        public void updateExisting(ReadyInvoice readyInvoice) {
            db.put(readyInvoice.getNumber(), new ArchivedInvoice(readyInvoice));
        }
    }

    private static class TestSuggestionsPort implements SuggestionsPort {
        private final Map<String, Set<String>> suggestions = new HashMap<>();

        @Override
        public void rememberSuggestion(String paramName, String value) {
            suggestions.computeIfAbsent(paramName, k -> new HashSet<>()).add(value);
        }

        Set<String> getSuggestions(String paramName) {
            return suggestions.getOrDefault(paramName, Collections.emptySet());
        }
    }

    private static class TestDateTimeProvider implements DateTimeProvider {
        private final LocalDate date;
        private final LocalDateTime dateTime;

        TestDateTimeProvider(LocalDate date, LocalDateTime dateTime) {
            this.date = date;
            this.dateTime = dateTime;
        }

        @Override
        public LocalDate today() {
            return date;
        }

        @Override
        public LocalDateTime now() {
            return dateTime;
        }
    }

    private static class TestNumberToWordsPort implements NumberToWordsPort {
        @Override
        public String numberToWords(int number) {
            return "NUM_" + number;
        }
    }

    private static class TestDocumentFillerPort implements DocumentFillerPort {
        private boolean called = false;

        @Override
        public void fill(Invoice invoice, ReadyInvoice readyInvoice, Path outputPath) throws IOException {
            called = true;
        }

        boolean wasCalled() {
            return called;
        }
    }

    private static Invoice createTestInvoice(String name, String extension, String configCsv) throws IOException {
        File tmp = File.createTempFile("tmpl-", "." + extension);
        Invoice inv = new Invoice(name, tmp);
        inv.setConfigurationDataString(configCsv);
        return inv;
    }
}
