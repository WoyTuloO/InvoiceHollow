package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class BlueprintServiceTest {

    private BlueprintService service;
    private TestBlueprintRepoPort repo;

    @BeforeEach
    void setup() {
        repo = new TestBlueprintRepoPort();
        service = new BlueprintService(repo);
    }

    @Test
    void getByName_returns_blueprint() throws IOException {
        Invoice blueprint = createInvoice("Blueprint1", "Param;A1;val;C;.");
        repo.register("Blueprint1", blueprint);

        Invoice result = service.getByName("Blueprint1");

        assertNotNull(result);
        assertEquals("Blueprint1", result.getName());
    }

    @Test
    void getByName_returns_null_for_missing() {
        Invoice result = service.getByName("NonExistent");

        assertNull(result);
    }

    @Test
    void getByName_returns_correct_blueprint_among_many() throws IOException {
        repo.register("BP1", createInvoice("BP1", "Param1;A1;val1;C;."));
        repo.register("BP2", createInvoice("BP2", "Param2;A2;val2;C;."));
        repo.register("BP3", createInvoice("BP3", "Param3;A3;val3;C;."));

        Invoice result = service.getByName("BP2");

        assertNotNull(result);
        assertEquals("BP2", result.getName());
    }

    // ============ Helper Classes ============

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

    private static Invoice createInvoice(String name, String configCsv) throws IOException {
        File tmp = File.createTempFile("tmpl-", ".xlsx");
        Invoice inv = new Invoice(name, tmp);
        inv.setConfigurationDataString(configCsv);
        return inv;
    }
}
