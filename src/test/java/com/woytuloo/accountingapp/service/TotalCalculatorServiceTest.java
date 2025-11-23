package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TotalCalculatorServiceTest {

    private TotalCalculatorService service;
    private TestBlueprintRepoPort repo;

    @BeforeEach
    void setup() {
        repo = new TestBlueprintRepoPort();
        service = new TotalCalculatorService(repo);
    }

    @Test
    void computeTotal_single_total_field() throws IOException {
        Invoice inv = createInvoice("Invoice1", "Total;B2;500;R;T");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Total", "500");

        double result = service.computeTotal("Invoice1", props);

        assertEquals(500.0, result);
    }

    @Test
    void computeTotal_multiple_total_fields_same_value() throws IOException {
        Invoice inv = createInvoice("Invoice1",
                "Total1;B2;300;R;T",
                "Total2;B3;300;R;T");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Total1", "300");
        props.put("Total2", "300");

        double result = service.computeTotal("Invoice1", props);

        assertEquals(300.0, result);
    }

    @Test
    void computeTotal_missing_blueprint_returns_minus_one() {
        Map<String, String> props = new HashMap<>();
        props.put("Total", "500");

        double result = service.computeTotal("NonExistent", props);

        assertEquals(-1.0, result);
    }

    @Test
    void computeTotal_no_total_fields_returns_minus_one() throws IOException {
        Invoice inv = createInvoice("Invoice1", "Param;A1;val;C;.");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Param", "value");

        double result = service.computeTotal("Invoice1", props);

        assertEquals(-1.0, result);
    }

    @Test
    void computeTotal_missing_property_skips_field() throws IOException {
        Invoice inv = createInvoice("Invoice1",
                "Total1;B2;100;R;T",
                "Total2;B3;200;R;T");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Total1", "100");
        // Total2 missing

        double result = service.computeTotal("Invoice1", props);

        assertEquals(100.0, result);
    }

    @Test
    void computeTotal_blank_property_skips_field() throws IOException {
        Invoice inv = createInvoice("Invoice1",
                "Total1;B2;100;R;T",
                "Total2;B3;200;R;T");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Total1", "100");
        props.put("Total2", "");

        double result = service.computeTotal("Invoice1", props);

        assertEquals(100.0, result);
    }

    @Test
    void computeTotal_invalid_number_returns_minus_one() throws IOException {
        Invoice inv = createInvoice("Invoice1", "Total;B2;500;R;T");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Total", "NOT_A_NUMBER");

        double result = service.computeTotal("Invoice1", props);

        assertEquals(-1.0, result);
    }

    @Test
    void computeTotal_decimal_values() throws IOException {
        Invoice inv = createInvoice("Invoice1", "Total;B2;99.99;R;T");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Total", "99.99");

        double result = service.computeTotal("Invoice1", props);

        assertEquals(99.99, result, 0.001);
    }

    @Test
    void computeTotal_zero_value() throws IOException {
        Invoice inv = createInvoice("Invoice1", "Total;B2;0;R;T");
        repo.register("Invoice1", inv);

        Map<String, String> props = new HashMap<>();
        props.put("Total", "0");

        double result = service.computeTotal("Invoice1", props);

        assertEquals(0.0, result);
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

    private static Invoice createInvoice(String name, String... configLines) throws IOException {
        File tmp = File.createTempFile("tmpl-", ".xlsx");
        Invoice inv = new Invoice(name, tmp);
        inv.setConfigurationDataString(String.join(",", configLines));
        return inv;
    }
}
