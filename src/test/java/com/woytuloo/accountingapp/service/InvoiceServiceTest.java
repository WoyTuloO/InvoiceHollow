package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceServiceTest {

    private FakeConfigPort config;
    private FakeArchivePort archive;
    private FakeSuggestionsPort suggestions;
    private FakeDocumentFillerPort filler;
    private FakeDateTimeProvider time;
    private FakeNumberToWordsPort n2w;
    private InvoiceService service;

    @BeforeEach
    void setup() {
        config = new FakeConfigPort();
        archive = new FakeArchivePort();
        suggestions = new FakeSuggestionsPort();
        filler = new FakeDocumentFillerPort();
        time = new FakeDateTimeProvider(LocalDate.of(2024, 5, 12), LocalDateTime.of(2024, 5, 12, 14, 33, 0));
        n2w = new FakeNumberToWordsPort();
        service = new InvoiceService(config, archive, suggestions, filler, time, n2w);
    }

    private Invoice buildInvoice(String name, String extension, String configCsv) throws IOException {
        File tmp = File.createTempFile("tmpl-", "." + extension);
        return new Invoice(name, tmp) {{ setConfigurationDataString(configCsv); }};
    }

    @Test
    void buildReadyInvoice_fallbacks_and_autoS_words() throws Exception {
        // Config: N (number), T (total placeholder 123), S (words placeholder "@"), F (client auto-fill), . (note)
        String cfg = String.join(",",
                "Number;A1;1/2024;C;N",
                "Total;B2;123;R;T",
                "Words;C3;@;L;S",
                "Client;D4;Autouzupełnianie;L;F",
                "Note;E5;Default note;L;."
        );
        Invoice inv = buildInvoice("TestInvoice", "xlsx", cfg);

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("Client", "ACME"); // non-generic -> should be suggested
        inputs.put("Note", ""); // blank -> fallback to placeholder
        // Words left blank -> placeholder "@" triggers words from T=123

        ReadyInvoice ready = service.buildReadyInvoice(inv, inputs);

        assertEquals(1, ready.getNumber(), "Default number should be 1 from FakeConfigPort");
        assertEquals("ACME", ready.getPropertyDataMap().get("Client"));
        assertEquals("Default note", ready.getPropertyDataMap().get("Note"));
        // Words should be generated via NumberToWordsPort for 123
        assertEquals("ONE HUNDRED TWENTY THREE", ready.getPropertyDataMap().get("Words"));

        // Suggestions should not be recorded yet (only after generateNewInvoice), but words are not suggestions anyway
        assertTrue(suggestions.remembered.isEmpty());
    }

    @Test
    void computeOutputPath_uses_date_and_tree_path() throws Exception {
        String cfg = "Number;A1;7/2024;C;N";
        Invoice inv = buildInvoice("Name", "xlsx", cfg);
        Path p = service.computeOutputPath(inv, 7);
        String s = p.toString().replace('\\', '/');
        assertTrue(s.contains("/InvoiceHollow/maj/12/"), "Path should include month/day based on FakeDateTimeProvider");
        assertTrue(s.endsWith("Name7.xlsx"));
    }


    // Helpers
    private static void registerBlueprint(String name, Invoice inv) throws Exception {
        Class<?> cls = Class.forName("com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder");
        Field f = cls.getDeclaredField("collection");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Invoice> map = (Map<String, Invoice>) f.get(null);
        if (map == null) {
            map = new HashMap<>();
            f.set(null, map);
        }
        map.put(name, inv);
    }

    // Fake implementations
    private static class FakeConfigPort implements ConfigPort {
        int current = 1;
        String treePath = System.getProperty("java.io.tmpdir");
        int earningsAdded = 0;
        int invoiceCountAdded = 0;
        double totalDelta = 0;
        @Override public int getCurrentInvoiceNum() { return current; }
        @Override public String getInvoiceTreePath() { return treePath; }
        @Override public void incrementEarningsAndInvoiceCount(int amount) { earningsAdded += amount; invoiceCountAdded++; }
        @Override public void updateTotalBy(double delta) { totalDelta += delta; }
    }

    private static class FakeArchivePort implements ArchivePort {
        ArchivedInvoice lastSaved;
        final Map<Integer, ArchivedInvoice> db = new HashMap<>();
        @Override public ArchivedInvoice getByNumber(int number) { return db.get(number); }
        @Override public void saveNew(ReadyInvoice readyInvoice) { lastSaved = new ArchivedInvoice(readyInvoice); db.put(readyInvoice.getNumber(), lastSaved); }
        @Override public void updateExisting(ReadyInvoice readyInvoice) { db.put(readyInvoice.getNumber(), new ArchivedInvoice(readyInvoice)); }
    }

    private static class FakeSuggestionsPort implements SuggestionsPort {
        final Map<String, Set<String>> remembered = new HashMap<>();
        @Override public void rememberSuggestion(String paramName, String value) {
            remembered.computeIfAbsent(paramName, k -> new HashSet<>()).add(value);
        }
    }

    private static class FakeDocumentFillerPort implements DocumentFillerPort {
        boolean fillCalled = false;
        Path lastOutputPath;
        @Override public void fill(Invoice invoice, ReadyInvoice readyInvoice, Path outputPath) throws IOException {
            fillCalled = true;
            lastOutputPath = outputPath;
        }
    }

    private static class FakeDateTimeProvider implements DateTimeProvider {
        private final LocalDate d; private final LocalDateTime dt;
        FakeDateTimeProvider(LocalDate d, LocalDateTime dt) { this.d = d; this.dt = dt; }
        @Override public LocalDate today() { return d; }
        @Override public LocalDateTime now() { return dt; }
    }

    private static class FakeNumberToWordsPort implements NumberToWordsPort {
        @Override public String numberToWords(int number) {
            if (number == 123) return "ONE HUNDRED TWENTY THREE";
            return "NUM " + number;
        }
    }
}
