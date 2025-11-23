package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class ArchiveServiceTest {

    private ArchiveService service;
    private TestArchiveRepoPort repo;

    @BeforeEach
    void setup() {
        repo = new TestArchiveRepoPort();
        service = new ArchiveService(repo);
    }

    @Test
    void listAllNumbers_returns_sorted_list() {
        ArchivedInvoice inv1 = createArchivedInvoice(5, "Invoice1", "Client", "ACME");
        ArchivedInvoice inv2 = createArchivedInvoice(2, "Invoice1", "Client", "Beta");
        ArchivedInvoice inv3 = createArchivedInvoice(10, "Invoice1", "Client", "Gamma");

        repo.add(5, inv1);
        repo.add(2, inv2);
        repo.add(10, inv3);

        List<Integer> result = service.listAllNumbers();

        assertEquals(Arrays.asList(2, 5, 10), result);
    }

    @Test
    void listAllNumbers_returns_empty_for_empty_repo() {
        List<Integer> result = service.listAllNumbers();

        assertTrue(result.isEmpty());
    }

    @Test
    void filterNumbers_returns_all_when_term_null() {
        repo.add(1, createArchivedInvoice(1, "Invoice1", "Client", "ACME"));
        repo.add(2, createArchivedInvoice(2, "Invoice1", "Client", "Beta"));

        List<Integer> result = service.filterNumbers(null);

        assertEquals(2, result.size());
    }

    @Test
    void filterNumbers_returns_all_when_term_blank() {
        repo.add(1, createArchivedInvoice(1, "Invoice1", "Client", "ACME"));
        repo.add(2, createArchivedInvoice(2, "Invoice1", "Client", "Beta"));

        List<Integer> result = service.filterNumbers("   ");

        assertEquals(2, result.size());
    }

    @Test
    void filterNumbers_case_insensitive_match() {
        repo.add(1, createArchivedInvoice(1, "Invoice1", "Client", "ACME Corp"));
        repo.add(2, createArchivedInvoice(2, "Invoice1", "Client", "Beta Inc"));

        List<Integer> result = service.filterNumbers("acme");

        assertEquals(1, result.size());
        assertTrue(result.contains(1));
    }

    @Test
    void filterNumbers_partial_match() {
        repo.add(1, createArchivedInvoice(1, "Invoice1", "Client", "ACME Corporation"));
        repo.add(2, createArchivedInvoice(2, "Invoice1", "Client", "Beta Inc"));

        List<Integer> result = service.filterNumbers("Corp");

        assertEquals(1, result.size());
        assertTrue(result.contains(1));
    }

    @Test
    void filterNumbers_matches_multiple_fields() {
        repo.add(1, createArchivedInvoice(1, "Invoice1", "Client", "ACME", "Address", "123 Main St"));
        repo.add(2, createArchivedInvoice(2, "Invoice1", "Client", "Beta", "Address", "456 Oak Ave"));

        List<Integer> result = service.filterNumbers("Main");

        assertEquals(1, result.size());
        assertTrue(result.contains(1));
    }

    @Test
    void filterNumbers_returns_sorted_results() {
        repo.add(10, createArchivedInvoice(10, "Invoice1", "Client", "ACME"));
        repo.add(2, createArchivedInvoice(2, "Invoice1", "Client", "ACME"));
        repo.add(5, createArchivedInvoice(5, "Invoice1", "Client", "ACME"));

        List<Integer> result = service.filterNumbers("ACME");

        assertEquals(Arrays.asList(2, 5, 10), result);
    }

    @Test
    void filterNumbers_no_match_returns_empty() {
        repo.add(1, createArchivedInvoice(1, "Invoice1", "Client", "ACME"));
        repo.add(2, createArchivedInvoice(2, "Invoice1", "Client", "Beta"));

        List<Integer> result = service.filterNumbers("Gamma");

        assertTrue(result.isEmpty());
    }

    @Test
    void getByNumber_returns_invoice() {
        ArchivedInvoice inv = createArchivedInvoice(7, "Invoice1", "Client", "ACME");
        repo.add(7, inv);

        ArchivedInvoice result = service.getByNumber(7);

        assertEquals(inv, result);
    }

    @Test
    void getByNumber_returns_null_for_missing() {
        ArchivedInvoice result = service.getByNumber(999);

        assertNull(result);
    }

    // ============ Helper Classes ============

    private static class TestArchiveRepoPort implements ArchiveRepoPort {
        private final Map<Integer, ArchivedInvoice> data = new HashMap<>();

        void add(int number, ArchivedInvoice invoice) {
            data.put(number, invoice);
        }

        @Override
        public Map<Integer, ArchivedInvoice> all() {
            return new HashMap<>(data);
        }

        @Override
        public ArchivedInvoice getByNumber(int number) {
            return data.get(number);
        }
    }

    private static ArchivedInvoice createArchivedInvoice(int number, String invoiceName, String... keyValues) {
        Map<String, String> props = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            props.put(keyValues[i], keyValues[i + 1]);
        }
        return new ArchivedInvoice(number + "|" + invoiceName + "|" +
                String.join("|", props.entrySet().stream()
                        .map(e -> e.getKey() + ";" + e.getValue())
                        .toArray(String[]::new)));
    }
}
