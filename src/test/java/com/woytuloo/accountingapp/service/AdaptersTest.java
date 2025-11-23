package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;
import com.woytuloo.accountingapp.handlers.AutoCompleteHandler;
import com.woytuloo.accountingapp.handlers.ConfigStorage;
import com.woytuloo.accountingapp.handlers.StorageHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AdaptersTest {

    // ============ ConfigStorageAdapter Tests ============

    @Test
    void configStorageAdapter_incrementEarningsAndInvoiceCount_delegates() {
        ConfigStorage mockConfig = mock(ConfigStorage.class);
        Adapters.ConfigStorageAdapter adapter = new Adapters.ConfigStorageAdapter(mockConfig);

        adapter.incrementEarningsAndInvoiceCount(500);

        verify(mockConfig).incrementEarningsAndInvoiceCount(500);
    }

    @Test
    void configStorageAdapter_updateTotalBy_delegates() {
        ConfigStorage mockConfig = mock(ConfigStorage.class);
        Adapters.ConfigStorageAdapter adapter = new Adapters.ConfigStorageAdapter(mockConfig);

        adapter.updateTotalBy(250.75);

        verify(mockConfig).updateTotalBy(250.75);
    }

    // ============ StorageHandlerArchiveAdapter Tests ============

    @Test
    void storageHandlerArchiveAdapter_getByNumber_delegates_to_storageHandler() {
        StorageHandler mockStorage = mock(StorageHandler.class);
        ArchivedInvoice mockInvoice = mock(ArchivedInvoice.class);
        when(mockStorage.getInvoice(5)).thenReturn(mockInvoice);

        Adapters.StorageHandlerArchiveAdapter adapter = new Adapters.StorageHandlerArchiveAdapter(mockStorage);
        ArchivedInvoice result = adapter.getByNumber(5);

        assertEquals(mockInvoice, result);
        verify(mockStorage).getInvoice(5);
    }

    @Test
    void storageHandlerArchiveAdapter_saveNew_creates_archived_and_saves() throws IOException {
        StorageHandler mockStorage = mock(StorageHandler.class);
        Invoice inv = createTestInvoice("Test", "xlsx", "Param;A1;val;C;.");
        ReadyInvoice ready = new ReadyInvoice(inv);
        ready.addProperty("Param", "value");
        ready.setNumber(1);

        Adapters.StorageHandlerArchiveAdapter adapter = new Adapters.StorageHandlerArchiveAdapter(mockStorage);
        adapter.saveNew(ready);

        verify(mockStorage).archiveInvoice(any(ArchivedInvoice.class));
        verify(mockStorage).saveCurrentInvoice(ready);
    }

    @Test
    void storageHandlerArchiveAdapter_updateExisting_updates_and_saves() throws IOException {
        StorageHandler mockStorage = mock(StorageHandler.class);
        Invoice inv = createTestInvoice("Test", "xlsx", "Param;A1;val;C;.");
        ReadyInvoice ready = new ReadyInvoice(inv);
        ready.addProperty("Param", "updated");
        ready.setNumber(3);

        Adapters.StorageHandlerArchiveAdapter adapter = new Adapters.StorageHandlerArchiveAdapter(mockStorage);
        adapter.updateExisting(ready);

        verify(mockStorage).updateArchive(ready);
        verify(mockStorage).saveCurrentInvoice(ready);
    }

    // ============ StorageHandlerArchiveRepoAdapter Tests ============

    @Test
    void storageHandlerArchiveRepoAdapter_all_returns_unmodifiable_map() throws IOException {
        StorageHandler mockStorage = mock(StorageHandler.class);
        Map<Integer, ArchivedInvoice> testMap = new HashMap<>();
        testMap.put(1, mock(ArchivedInvoice.class));
        testMap.put(2, mock(ArchivedInvoice.class));
        when(mockStorage.getAllInvoicesView()).thenReturn(Collections.unmodifiableMap(testMap));

        Adapters.StorageHandlerArchiveRepoAdapter adapter = new Adapters.StorageHandlerArchiveRepoAdapter(mockStorage);
        Map<Integer, ArchivedInvoice> result = adapter.all();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(2));
    }

    @Test
    void storageHandlerArchiveRepoAdapter_getByNumber_delegates() {
        StorageHandler mockStorage = mock(StorageHandler.class);
        ArchivedInvoice mockInvoice = mock(ArchivedInvoice.class);
        when(mockStorage.getInvoice(7)).thenReturn(mockInvoice);

        Adapters.StorageHandlerArchiveRepoAdapter adapter = new Adapters.StorageHandlerArchiveRepoAdapter(mockStorage);
        ArchivedInvoice result = adapter.getByNumber(7);

        assertEquals(mockInvoice, result);
    }

    // ============ AutoCompleteSuggestionsAdapter Tests ============

    @Test
    void autoCompleteSuggestionsAdapter_rememberSuggestion_delegates() {
        AutoCompleteHandler mockHandler = mock(AutoCompleteHandler.class);
        Adapters.AutoCompleteSuggestionsAdapter adapter = new Adapters.AutoCompleteSuggestionsAdapter(mockHandler);

        adapter.rememberSuggestion("Client", "ACME Corp");

        verify(mockHandler).fillSuggestions("Client", "ACME Corp");
    }

    // ============ SystemDateTimeProvider Tests ============

    @Test
    void systemDateTimeProvider_today_returns_current_date() {
        Adapters.SystemDateTimeProvider provider = new Adapters.SystemDateTimeProvider();
        LocalDate today = provider.today();

        assertNotNull(today);
        assertEquals(LocalDate.now(), today);
    }

    @Test
    void systemDateTimeProvider_now_returns_current_datetime() {
        Adapters.SystemDateTimeProvider provider = new Adapters.SystemDateTimeProvider();
        LocalDateTime now = provider.now();

        assertNotNull(now);
        // Allow 1 second tolerance for test execution time
        assertTrue(Math.abs(LocalDateTime.now().getSecond() - now.getSecond()) <= 1);
    }

    // ============ PolishNumberToWordsAdapter Tests ============

    @Test
    void polishNumberToWordsAdapter_numberToWords_delegates() {
        Adapters.PolishNumberToWordsAdapter adapter = new Adapters.PolishNumberToWordsAdapter();
        String result = adapter.numberToWords(123);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // ============ InvoiceBlueprintRepoAdapter Tests ============

    @Test
    void invoiceBlueprintRepoAdapter_getByName_returns_blueprint() throws Exception {
        Invoice testInvoice = createTestInvoice("Blueprint1", "xlsx", "Param;A1;val;C;.");
        registerBlueprint("Blueprint1", testInvoice);

        Adapters.InvoiceBlueprintRepoAdapter adapter = new Adapters.InvoiceBlueprintRepoAdapter();
        Invoice result = adapter.getByName("Blueprint1");

        assertNotNull(result);
        assertEquals("Blueprint1", result.getName());
    }

    // ============ Helper Methods ============

    private static Invoice createTestInvoice(String name, String extension, String configCsv) throws IOException {
        File tmp = File.createTempFile("tmpl-", "." + extension);
        Invoice inv = new Invoice(name, tmp);
        inv.setConfigurationDataString(configCsv);
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
