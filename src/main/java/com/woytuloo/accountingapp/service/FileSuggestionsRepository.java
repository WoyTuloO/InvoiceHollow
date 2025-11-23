package com.woytuloo.accountingapp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class FileSuggestionsRepository implements SuggestionsRepository {
    private static final Logger logger = LogManager.getLogger(FileSuggestionsRepository.class);

    private final Path filePath;

    public FileSuggestionsRepository() {
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        Path configDirPath = Paths.get(userDocuments + File.separator + "InvoiceHollow", "Config");
        this.filePath = Paths.get(configDirPath.toString(), "autoCompleteSuggestions.csv");
    }

    public FileSuggestionsRepository(Path filePath) {
        this.filePath = filePath;
    }

    private void ensureFile() throws IOException {
        Files.createDirectories(filePath.getParent());
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    @Override
    public Map<String, Set<String>> load() throws IOException {
        ensureFile();
        Map<String, Set<String>> map = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length < 2) continue;
                String paramName = parts[0];
                String[] suggestions = parts[1].split("\\$");
                map.put(paramName, new HashSet<>(Arrays.asList(suggestions)));
            }
        } catch (IOException ex) {
            logger.error("Błąd odczytu sugestii: {}", ex.getMessage());
            throw ex;
        }
        return map;
    }

    @Override
    public void save(Map<String, Set<String>> data) throws IOException {
        ensureFile();
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Map.Entry<String, Set<String>> e : data.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(e.getKey()).append(";");
                Iterator<String> it = e.getValue().iterator();
                while (it.hasNext()) {
                    sb.append(it.next());
                    if (it.hasNext()) sb.append("$");
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException ex) {
            logger.error("Błąd zapisu sugestii: {}", ex.getMessage());
            throw ex;
        }
    }
}
