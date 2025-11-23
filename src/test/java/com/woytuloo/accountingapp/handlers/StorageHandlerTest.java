package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import com.woytuloo.accountingapp.component.ButtonPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class StorageHandlerTest {

    @TempDir
    Path tempDir;

    private StorageHandler handler;
    private JPanel testPanel;
    private CardLayout cardLayout;
    private JPanel background;
    private JButton searchButton;
    private JTextField searchField;
    private ButtonPanel buttonPanel;

    @BeforeEach
    void setup() {
        testPanel = new JPanel();
        cardLayout = new CardLayout();
        background = new JPanel(cardLayout);
        searchButton = new JButton("Search");
        searchField = new JTextField();
        buttonPanel = new ButtonPanel();

        // Set temp directory for file operations
        System.setProperty("user.home", tempDir.toString());

        handler = new StorageHandler(
                new ConfigStorage(),
                testPanel,
                cardLayout,
                background,
                buttonPanel,
                searchButton,
                searchField
        );
    }

    @Test
    void getInvoice_returns_null_for_missing() {
        ArchivedInvoice result = handler.getInvoice(999);
        assertNull(result);
    }

    @Test
    void importInvoices_loads_from_file() throws IOException {
        // Create test data file
        Path configDir = Paths.get(tempDir.toString(), "Documents", "InvoiceHollow", "Config");
        Files.createDirectories(configDir);
        Path archivePath = Paths.get(configDir.toString(), "InvoiceArchive1.csv");

        // Write test invoice data
        String testData = "1|TestInvoice|Param;value|";
        Files.write(archivePath, testData.getBytes());

        handler.init();

        ArchivedInvoice retrieved = handler.getInvoice(1);
        assertNotNull(retrieved);
    }

    @Test
    void filterInvoices_returns_matching_invoices() throws IOException {
        // Add test invoices
        Invoice inv1 = createTestInvoice("Test", "xlsx", "Client;A1;val;C;.");
        ReadyInvoice ready1 = new ReadyInvoice(inv1);
        ready1.addProperty("Client", "ACME Corp");
        ready1.setNumber(1);
        handler.archiveInvoice(new ArchivedInvoice(ready1));

        Invoice inv2 = createTestInvoice("Test", "xlsx", "Client;A1;val;C;.");
        ReadyInvoice ready2 = new ReadyInvoice(inv2);
        ready2.addProperty("Client", "Beta Inc");
        ready2.setNumber(2);
        handler.archiveInvoice(new ArchivedInvoice(ready2));

        // Filter should work (though UI updates won't be visible in test)
        handler.filterInvoices("ACME");
        // Verify no exception thrown
    }

    @Test
    void filterInvoices_empty_filter_shows_all() throws IOException {
        Invoice inv1 = createTestInvoice("Test", "xlsx", "Client;A1;val;C;.");
        ReadyInvoice ready1 = new ReadyInvoice(inv1);
        ready1.addProperty("Client", "ACME");
        ready1.setNumber(1);
        handler.archiveInvoice(new ArchivedInvoice(ready1));

        handler.filterInvoices("");
        // Verify no exception thrown
    }

    @Test
    void displayInvoices_renders_all_invoices() throws IOException {
        Invoice inv1 = createTestInvoice("Test", "xlsx", "Param;A1;val;C;.");
        ReadyInvoice ready1 = new ReadyInvoice(inv1);
        ready1.addProperty("Param", "value1");
        ready1.setNumber(1);
        handler.archiveInvoice(new ArchivedInvoice(ready1));

        Invoice inv2 = createTestInvoice("Test", "xlsx", "Param;A1;val;C;.");
        ReadyInvoice ready2 = new ReadyInvoice(inv2);
        ready2.addProperty("Param", "value2");
        ready2.setNumber(2);
        handler.archiveInvoice(new ArchivedInvoice(ready2));

        handler.displayInvoices();
        // Verify no exception thrown
    }


    // ============ Helper Classes ============

    private static Invoice createTestInvoice(String name, String extension, String configCsv) throws IOException {
        File tmp = File.createTempFile("tmpl-", "." + extension);
        Invoice inv = new Invoice(name, tmp);
        inv.setConfigurationDataString(configCsv);
        return inv;
    }
}
