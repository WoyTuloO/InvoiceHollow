package com.woytuloo.accountingapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class FileSuggestionsRepositoryTest {

    @TempDir
    Path tempDir;

    private FileSuggestionsRepository repository;
    private Path testFilePath;

    @BeforeEach
    void setup() {
        testFilePath = tempDir.resolve("suggestions.csv");
        repository = new FileSuggestionsRepository(testFilePath);
    }

    @Test
    void save_creates_file_if_not_exists() throws IOException {
        Map<String, Set<String>> data = new HashMap<>();
        data.put("Client", new HashSet<>(Arrays.asList("ACME", "Beta")));

        repository.save(data);

        assertTrue(Files.exists(testFilePath));
    }

    @Test
    void save_and_load_roundtrip() throws IOException {
        Map<String, Set<String>> original = new HashMap<>();
        original.put("Client", new HashSet<>(Arrays.asList("ACME Corp", "Beta Inc")));
        original.put("Address", new HashSet<>(Arrays.asList("123 Main St", "456 Oak Ave")));

        repository.save(original);
        Map<String, Set<String>> loaded = repository.load();

        assertEquals(2, loaded.size());
        assertEquals(2, loaded.get("Client").size());
        assertEquals(2, loaded.get("Address").size());
        assertTrue(loaded.get("Client").contains("ACME Corp"));
        assertTrue(loaded.get("Address").contains("123 Main St"));
    }

    @Test
    void load_empty_file_returns_empty_map() throws IOException {
        Files.createFile(testFilePath);

        Map<String, Set<String>> result = repository.load();

        assertTrue(result.isEmpty());
    }

    @Test
    void load_creates_file_if_not_exists() throws IOException {
        assertFalse(Files.exists(testFilePath));

        repository.load();

        assertTrue(Files.exists(testFilePath));
    }

    @Test
    void save_overwrites_previous_data() throws IOException {
        Map<String, Set<String>> data1 = new HashMap<>();
        data1.put("Client", new HashSet<>(Arrays.asList("ACME")));
        repository.save(data1);

        Map<String, Set<String>> data2 = new HashMap<>();
        data2.put("Address", new HashSet<>(Arrays.asList("123 Main")));
        repository.save(data2);

        Map<String, Set<String>> loaded = repository.load();
        assertEquals(1, loaded.size());
        assertTrue(loaded.containsKey("Address"));
        assertFalse(loaded.containsKey("Client"));
    }

    @Test
    void save_handles_special_characters() throws IOException {
        Map<String, Set<String>> data = new HashMap<>();
        data.put("Client", new HashSet<>(Arrays.asList("ACME Corp", "Beta & Co.")));

        repository.save(data);
        Map<String, Set<String>> loaded = repository.load();

        assertEquals(2, loaded.get("Client").size());
    }

    @Test
    void save_handles_empty_suggestions() throws IOException {
        Map<String, Set<String>> data = new HashMap<>();
        data.put("Client", new HashSet<>());

        repository.save(data);
        Map<String, Set<String>> loaded = repository.load();

        assertEquals(loaded.get("Client"), null);
    }

    @Test
    void load_handles_malformed_lines() throws IOException {
        // Write malformed data directly
        Files.write(testFilePath, Arrays.asList(
                "Client;ACME$Beta",
                "InvalidLine",
                "Address;123 Main$456 Oak"
        ));

        Map<String, Set<String>> loaded = repository.load();

        assertEquals(2, loaded.size());
        assertTrue(loaded.containsKey("Client"));
        assertTrue(loaded.containsKey("Address"));
    }

    @Test
    void save_multiple_times_accumulates() throws IOException {
        Map<String, Set<String>> data1 = new HashMap<>();
        data1.put("Client", new HashSet<>(Arrays.asList("ACME")));
        repository.save(data1);

        Map<String, Set<String>> data2 = new HashMap<>();
        data2.put("Client", new HashSet<>(Arrays.asList("ACME", "Beta")));
        repository.save(data2);

        Map<String, Set<String>> loaded = repository.load();
        assertEquals(2, loaded.get("Client").size());
    }

    @Test
    void load_handles_blank_lines() throws IOException {
        Files.write(testFilePath, Arrays.asList(
                "Client;ACME$Beta",
                "",
                "Address;123 Main",
                "   "
        ));

        Map<String, Set<String>> loaded = repository.load();

        assertEquals(2, loaded.size());
    }

    @Test
    void save_and_load_preserves_all_suggestions() throws IOException {
        Map<String, Set<String>> data = new HashMap<>();
        Set<String> clients = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            clients.add("Client" + i);
        }
        data.put("Client", clients);

        repository.save(data);
        Map<String, Set<String>> loaded = repository.load();

        assertEquals(10, loaded.get("Client").size());
    }

    @Test
    void file_format_uses_semicolon_and_dollar_separators() throws IOException {
        Map<String, Set<String>> data = new HashMap<>();
        data.put("Client", new HashSet<>(Arrays.asList("ACME", "Beta")));

        repository.save(data);

        String content = Files.readString(testFilePath);
        assertTrue(content.contains(";"));
        assertTrue(content.contains("$"));
    }
}
