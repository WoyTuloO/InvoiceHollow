package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class IntegrationTests {

    @TempDir
    Path tempDir;

    private InvoiceService invoiceService;
    private ArchiveService archiveService;
    private TotalCalculatorService totalService;
    private BlueprintService blueprintService;

    private TestConfigPort configPort;
    private TestArchivePort archivePort;
    private TestSuggestionsPort suggestionsPort;
    private TestDocumentFillerPort fillerPort;
    private TestDateTimeProvider timeProvider;
    private TestNumberToWordsPort n2wPort;

    @BeforeEach
    void setup() {
        configPort = new TestConfigPort(1, tempDir.toString());
        archivePort = new TestArchivePort();
        suggestionsPort = new TestSuggestionsPort();
        fillerPort = new TestDocumentFillerPort();
        timeProvider = new TestDateTimeProvider(LocalDate.of(2024, 5, 15), LocalDateTime.of(2024, 5, 15, 10, 30));
        n2wPort = new TestNumberToWordsPort();

        invoiceService = new InvoiceService(configPort, archivePort, suggestionsPort, fillerPort, timeProvider, n2wPort);
        archiveService = new ArchiveService(new TestArchiveRepoPort(archivePort));
        totalService = new TotalCalculatorService(new TestBlueprintRepoPort());
        blueprintService = new BlueprintService(new TestBlueprintRepoPort());
    }

    // ============ Invoice Generation Workflow ============

    @Test
    void workflow_generate_new_invoice_end_to_end() throws Exception {
        // Setup blueprint
        Invoice blueprint = createInvoice("Invoice1", "xlsx",
                "Number;A1;1/2024;C;N",
                "Total;B2;500;R;T",
                "Client;C3;Autouzupełnianie;L;F");
        registerBlueprint("Invoice1", blueprint);

        // User inputs
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("Client", "ACME Corp");

        // Generate
        invoiceService.generateNewInvoice(blueprint, inputs);

        // Verify document filled
        assertTrue(fillerPort.wasCalled());

        // Verify archived
        ArchivedInvoice archived = archivePort.getByNumber(1);
        assertNotNull(archived);
        assertEquals("ACME Corp", archived.getPropertyDataMap().get("Client"));

        // Verify config updated
        assertEquals(500, configPort.getEarningsAdded());
        assertEquals(1, configPort.getInvoiceCountAdded());
    }


    @Test
    void workflow_update_existing_invoice() throws Exception {
        Invoice blueprint = createInvoice("Invoice1", "xlsx",
                "Number;A1;5/2024;C;N",
                "Total;B2;200;R;T",
                "Client;C3;Autouzupełnianie;L;F");
        registerBlueprint("Invoice1", blueprint);

        // Generate initial
        Map<String, String> inputs1 = new LinkedHashMap<>();
        inputs1.put("Client", "ACME");
        invoiceService.generateNewInvoice(blueprint, inputs1);

        int initialEarnings = configPort.getEarningsAdded();

        // Update with different total
        Map<String, String> inputs2 = new LinkedHashMap<>();
        inputs2.put("Client", "ACME Updated");
        invoiceService.updateExistingInvoice(blueprint, inputs2);

        // Verify archive updated
        ArchivedInvoice updated = archivePort.getByNumber(5);
        assertEquals("ACME Updated", updated.getPropertyDataMap().get("Client"));
    }

    // ============ Archive Service Integration ============

    @Test
    void integration_archive_service_with_multiple_invoices() throws Exception {
        // Create and archive multiple invoices
        for (int i = 1; i <= 5; i++) {
            Invoice inv = createInvoice("Invoice" + i, "xlsx", "Param;A1;val;C;.");
            ReadyInvoice ready = new ReadyInvoice(inv);
            ready.addProperty("Param", "value" + i);
            ready.setNumber(i);
            archivePort.saveNew(ready);
        }

        // List all
        List<Integer> all = archiveService.listAllNumbers();
        assertEquals(5, all.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), all);

        // Filter
        List<Integer> filtered = archiveService.filterNumbers("value3");
        assertEquals(1, filtered.size());
        assertTrue(filtered.contains(3));
    }

    // ============ Total Calculation Integration ============

    @Test
    void integration_total_calculation_with_blueprint() throws Exception {
        Invoice blueprint = createInvoice("Invoice1", "xlsx",
                "Total;B2;1000;R;T");
        TestBlueprintRepoPort blueprintRepo = new TestBlueprintRepoPort();
        blueprintRepo.register("Invoice1", blueprint);
        TotalCalculatorService totalCalc = new TotalCalculatorService(blueprintRepo);

        Map<String, String> props = new HashMap<>();
        props.put("Total", "1000");

        double total = totalCalc.computeTotal("Invoice1", props);
        assertEquals(1000.0, total);
    }

    // ============ Suggestions Integration ============

    @Test
    void integration_suggestions_persistence_workflow() throws Exception {
        // Fill suggestions
        suggestionsPort.rememberSuggestion("Client", "ACME");
        suggestionsPort.rememberSuggestion("Client", "Beta");
        suggestionsPort.rememberSuggestion("Address", "123 Main");

        // Verify stored
        TestSuggestionsPort testPort = (TestSuggestionsPort) suggestionsPort;
        assertEquals(2, testPort.getSuggestions("Client").size());
        assertEquals(1, testPort.getSuggestions("Address").size());
    }

    // ============ Config Port Integration ============

    @Test
    void integration_config_updates_across_operations() throws Exception {
        Invoice blueprint = createInvoice("Invoice1", "xlsx",
                "Number;A1;1/2024;C;N",
                "Total;B2;100;R;T");
        registerBlueprint("Invoice1", blueprint);

        // First invoice
        Map<String, String> inputs1 = new LinkedHashMap<>();
        invoiceService.generateNewInvoice(blueprint, inputs1);
        assertEquals(100, configPort.getEarningsAdded());
        assertEquals(1, configPort.getInvoiceCountAdded());

        // Second invoice
        Map<String, String> inputs2 = new LinkedHashMap<>();
        invoiceService.generateNewInvoice(blueprint, inputs2);
        assertEquals(200, configPort.getEarningsAdded());
        assertEquals(2, configPort.getInvoiceCountAdded());
    }

    // ============ Error Handling Integration ============

    @Test
    void integration_invalid_total_throws_exception() throws Exception {
        Invoice blueprint = createInvoice("Invoice1", "xlsx",
                "Number;A1;1/2024;C;N",
                "Total;B2;INVALID;R;T");
        registerBlueprint("Invoice1", blueprint);

        Map<String, String> inputs = new LinkedHashMap<>();

        assertThrows(IllegalArgumentException.class, () -> invoiceService.generateNewInvoice(blueprint, inputs));
    }

    @Test
    void integration_missing_blueprint_returns_null() {
        Invoice result = blueprintService.getByName("NonExistent");
        assertNull(result);
    }

    // ============ Helper Classes ============

    private static class TestConfigPort implements ConfigPort {
        private final int currentNum;
        private final String treePath;
        private int earningsAdded = 0;
        private int invoiceCountAdded = 0;
        private double totalDelta = 0;

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

    private static class TestArchiveRepoPort implements ArchiveRepoPort {
        private final TestArchivePort archivePort;

        TestArchiveRepoPort(TestArchivePort archivePort) {
            this.archivePort = archivePort;
        }

        @Override
        public Map<Integer, ArchivedInvoice> all() {
            return new HashMap<>(archivePort.db);
        }

        @Override
        public ArchivedInvoice getByNumber(int number) {
            return archivePort.getByNumber(number);
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

    private static class TestBlueprintRepoPort implements BlueprintRepoPort {
        private final Map<String, Invoice> blueprints = new HashMap<>();

        void register(String name, Invoice invoice) {
            blueprints.put(name, invoice);
        }

        @Override
        public Invoice getByName(String name) {
            return blueprints.get(name);
        }
    }

    private static Invoice createInvoice(String name, String extension, String... configLines) throws IOException {
        File tmp = File.createTempFile("tmpl-", "." + extension);
        Invoice inv = new Invoice(name, tmp);
        inv.setConfigurationDataString(String.join(",", configLines));
        return inv;
    }

    private static void registerBlueprint(String name, Invoice inv) throws Exception {
        Class<?> cls = Class.forName("com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder");
        java.lang.reflect.Field f = cls.getDeclaredField("collection");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Invoice> map = (Map<String, Invoice>) f.get(null);
        if (map == null) {
            map = new HashMap<>();
            f.set(null, map);
        }
        map.put(name, inv);
    }
}
