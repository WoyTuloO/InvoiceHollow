package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.service.SuggestionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class AutoCompleteHandlerTest {

    private AutoCompleteHandler handler;
    private TestSuggestionsRepository repository;
    private JPanel testPanel;
    private JComboBox<String> testCombo;
    private JTextArea testArea;
    private JButton saveBtn;
    private JButton deleteBtn;

    @BeforeEach
    void setup() {
        repository = new TestSuggestionsRepository();
        testPanel = new JPanel();
        testCombo = new JComboBox<>();
        testArea = new JTextArea();
        saveBtn = new JButton("Save");
        deleteBtn = new JButton("Delete");

        handler = new AutoCompleteHandler(testPanel, testCombo, testArea, saveBtn, deleteBtn, repository);
    }

    @Test
    void fillSuggestions_stores_non_empty_values() {
        handler.fillSuggestions("Client", "ACME Corp");
        handler.fillSuggestions("Client", "Beta Inc");

        Set<String> suggestions = handler.getSuggestions("Client", "");
        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("ACME Corp"));
        assertTrue(suggestions.contains("Beta Inc"));
    }

    @Test
    void fillSuggestions_ignores_null_values() {
        handler.fillSuggestions("Client", null);

        Set<String> suggestions = handler.getSuggestions("Client", "");
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void fillSuggestions_ignores_blank_values() {
        handler.fillSuggestions("Client", "   ");

        Set<String> suggestions = handler.getSuggestions("Client", "");
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void fillSuggestions_ignores_generic_phrases() {
        handler.fillSuggestions("Client", "Autouzupełnianie");

        Set<String> suggestions = handler.getSuggestions("Client", "");
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void getSuggestions_filters_by_prefix() {
        handler.fillSuggestions("Client", "ACME Corp");
        handler.fillSuggestions("Client", "ACME Inc");
        handler.fillSuggestions("Client", "Beta Corp");

        Set<String> results = handler.getSuggestions("Client", "ACME");
        assertEquals(2, results.size());
        assertTrue(results.contains("ACME Corp"));
        assertTrue(results.contains("ACME Inc"));
    }

    @Test
    void getSuggestions_case_sensitive_prefix() {
        handler.fillSuggestions("Client", "ACME");
        handler.fillSuggestions("Client", "acme");

        Set<String> results = handler.getSuggestions("Client", "ACM");
        assertEquals(1, results.size());
        assertTrue(results.contains("ACME"));
    }

    @Test
    void getSuggestions_empty_prefix_returns_all() {
        handler.fillSuggestions("Client", "ACME");
        handler.fillSuggestions("Client", "Beta");
        handler.fillSuggestions("Client", "Gamma");

        Set<String> results = handler.getSuggestions("Client", "");
        assertEquals(3, results.size());
    }

    @Test
    void getSuggestions_no_match_returns_empty() {
        handler.fillSuggestions("Client", "ACME");

        Set<String> results = handler.getSuggestions("Client", "XYZ");
        assertTrue(results.isEmpty());
    }

    @Test
    void getSuggestions_unknown_param_returns_empty() {
        Set<String> results = handler.getSuggestions("UnknownParam", "");
        assertTrue(results.isEmpty());
    }

    @Test
    void load_delegates_to_repository() throws IOException {
        SuggestionsRepository mockRepo = mock(SuggestionsRepository.class);
        Map<String, Set<String>> testData = new HashMap<>();
        testData.put("Client", new HashSet<>(Arrays.asList("ACME", "Beta")));
        when(mockRepo.load()).thenReturn(testData);

        AutoCompleteHandler testHandler = new AutoCompleteHandler(testPanel, testCombo, testArea, saveBtn, deleteBtn, mockRepo);
        testHandler.load();

        verify(mockRepo).load();
    }

    @Test
    void save_delegates_to_repository() throws IOException {
        SuggestionsRepository mockRepo = mock(SuggestionsRepository.class);
        AutoCompleteHandler testHandler = new AutoCompleteHandler(testPanel, testCombo, testArea, saveBtn, deleteBtn, mockRepo);

        testHandler.fillSuggestions("Client", "ACME");
        testHandler.save();

        verify(mockRepo).save(any(Map.class));
    }

    @Test
    void fillSuggestions_multiple_params() {
        handler.fillSuggestions("Client", "ACME");
        handler.fillSuggestions("Address", "123 Main St");
        handler.fillSuggestions("Phone", "555-1234");

        Set<String> clientSuggestions = handler.getSuggestions("Client", "");
        Set<String> addressSuggestions = handler.getSuggestions("Address", "");
        Set<String> phoneSuggestions = handler.getSuggestions("Phone", "");

        assertEquals(1, clientSuggestions.size());
        assertEquals(1, addressSuggestions.size());
        assertEquals(1, phoneSuggestions.size());
    }

    @Test
    void fillSuggestions_duplicate_values_stored_once() {
        handler.fillSuggestions("Client", "ACME");
        handler.fillSuggestions("Client", "ACME");
        handler.fillSuggestions("Client", "ACME");

        Set<String> suggestions = handler.getSuggestions("Client", "");
        assertEquals(1, suggestions.size());
    }

    // ============ Helper Classes ============

    private static class TestSuggestionsRepository implements SuggestionsRepository {
        private final Map<String, Set<String>> data = new HashMap<>();

        @Override
        public Map<String, Set<String>> load() throws IOException {
            return new HashMap<>(data);
        }

        @Override
        public void save(Map<String, Set<String>> data) throws IOException {
            this.data.clear();
            this.data.putAll(data);
        }
    }
}
