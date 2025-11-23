package com.woytuloo.accountingapp.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

import com.woytuloo.accountingapp.service.SuggestionsRepository;

public class AutoCompleteHandler {

    private static final Logger logger = LogManager.getLogger(AutoCompleteHandler.class);

    // Ostatnia utworzona instancja, aby zachować kompatybilność ze statycznym wywołaniem saveSuggestionsToFile()
    private static volatile AutoCompleteHandler lastInstance;

    private final SuggestionsRepository repository;
    private final Map<String, Set<String>> paramSuggestionsMap;

    public AutoCompleteHandler(JPanel rememberedJPanel, JComboBox<String> parametersCombo, JTextArea suggestionsTextArea, JButton save, JButton delete, SuggestionsRepository repository) {
        this.repository = repository;
        this.paramSuggestionsMap = new HashMap<>();
        lastInstance = this;

        rememberedJPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                parametersCombo.removeAllItems();
                for (String paramName : paramSuggestionsMap.keySet()) {
                    parametersCombo.addItem(paramName);
                }
            }
        });

        parametersCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String selectedParam = (String) parametersCombo.getSelectedItem();
                if (selectedParam != null) {
                    suggestionsTextArea.setText("");
                    Set<String> set = paramSuggestionsMap.getOrDefault(selectedParam, Collections.emptySet());
                    for (String suggestion : set) {
                        suggestionsTextArea.append(suggestion + "\n");
                    }
                }
            }
        });

        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String selectedParam = (String) parametersCombo.getSelectedItem();
                if (selectedParam != null) {
                    String[] suggestions = suggestionsTextArea.getText().split("\n");
                    paramSuggestionsMap.put(selectedParam, new HashSet<>(Arrays.asList(suggestions)));
                }
            }
        });

        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String selectedParam = (String) parametersCombo.getSelectedItem();
                if (selectedParam != null) {
                    paramSuggestionsMap.remove(selectedParam);
                    parametersCombo.removeItem(selectedParam);
                }
            }
        });
        // brak IO w konstruktorze – testy mogą tworzyć instancję bez dotykania dysku
    }

    // Jawne ładowanie sugestii (do wywołania w MainApp po utworzeniu handlera)
    public void load() {
        try {
            Map<String, Set<String>> data = repository.load();
            paramSuggestionsMap.clear();
            paramSuggestionsMap.putAll(data);
        } catch (IOException ex) {
            logger.error("Błąd ładowania sugestii: {}", ex.getMessage());
        }
    }

    // Jawny zapis sugestii (wykorzystany przez statyczny mostek i testy)
    public void save() {
        try {
            repository.save(paramSuggestionsMap);
        } catch (IOException ex) {
            logger.error("Błąd zapisu sugestii: {}", ex.getMessage());
        }
    }

    public Set<String> getSuggestions(String paramName, String text) {
        Set<String> result = new HashSet<>();
        Set<String> suggestions = paramSuggestionsMap.getOrDefault(paramName, Collections.emptySet());
        for (String word : suggestions) {
            if (word != null && word.startsWith(text)) {
                result.add(word);
            }
        }
        return result;
    }

    public void fillSuggestions(String paramName, String value) {
        if (value == null || "Autouzupełnianie".equals(value) || value.isBlank()) return;
        paramSuggestionsMap.computeIfAbsent(paramName, k -> new HashSet<>()).add(value);
    }

    // Zachowanie kompatybilności z istniejącym wywołaniem statycznym w ControllJButton
    public static void saveSuggestionsToFile() {
        AutoCompleteHandler inst = lastInstance;
        if (inst != null) {
            inst.save();
        } else {
            logger.warn("Brak instancji AutoCompleteHandler – pomijam zapis sugestii.");
        }
    }
}
