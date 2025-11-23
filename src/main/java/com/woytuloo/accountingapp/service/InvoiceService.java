package com.woytuloo.accountingapp.service;

import com.woytuloo.accountingapp.InvoiceManagement.ArchivedInvoice;
import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.ReadyInvoice;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class InvoiceService {

    private final ConfigPort configPort;
    private final ArchivePort archivePort;
    private final SuggestionsPort suggestionsPort;
    private final DocumentFillerPort documentFiller;
    private final DateTimeProvider dateTimeProvider;
    private final NumberToWordsPort numberToWordsPort;

    private static final List<String> GENERIC_PHRASES = Arrays.asList(
            "Naciśnij by uzyskać kwotę",
            "Błędne formatowanie wartości",
            "Podaj Ilość produktów",
            "Podaj cenę produktu",
            "Nacisnij by uzyskać kwotę słownie",
            "Autouzupełnianie"
    );

    private static final String[] MONTHS_PL = {
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
    };

    public InvoiceService(ConfigPort configPort,
                          ArchivePort archivePort,
                          SuggestionsPort suggestionsPort,
                          DocumentFillerPort documentFiller,
                          DateTimeProvider dateTimeProvider,
                          NumberToWordsPort numberToWordsPort) {
        this.configPort = configPort;
        this.archivePort = archivePort;
        this.suggestionsPort = suggestionsPort;
        this.documentFiller = documentFiller;
        this.dateTimeProvider = dateTimeProvider;
        this.numberToWordsPort = numberToWordsPort;
    }

    /**
     * Build ReadyInvoice from UI inputs with placeholder and automation fallback logic.
     */
    public ReadyInvoice buildReadyInvoice(Invoice invoice, Map<String, String> userInputs) {
        ReadyInvoice ready = new ReadyInvoice(invoice);
        String[] params = invoice.getConfigurationDataString().split(",");
        Map<String, String> confByName = new LinkedHashMap<>();
        for (String p : params) {
            String[] s = p.split(";");
            confByName.put(s[0], p);
        }

        // For each configured param, compute value according to rules
        for (String paramName : confByName.keySet()) {
            String[] split = confByName.get(paramName).split(";");
            String placeholder = split[2];
            String auto = split.length > 4 ? split[4] : ".";
            String inputVal = userInputs.getOrDefault(paramName, "");

            String val = computeParamValue(paramName, inputVal, placeholder, auto, invoice, confByName);
            ready.addProperty(paramName, val);
        }

        // Set invoice number if present among params, else current
        int nr = configPort.getCurrentInvoiceNum();
        for (String paramName : confByName.keySet()) {
            String[] split = confByName.get(paramName).split(";");
            String auto = split.length > 4 ? split[4] : ".";
            if ("N".equals(auto)) {
                String val = ready.getPropertyDataMap().get(paramName);
                try {
                    nr = Integer.parseInt(val.split("/")[0]);
                } catch (Exception ignored) { }
                break;
            }
        }
        ready.setNumber(nr);

        return ready;
    }

    private String computeParamValue(String paramName,
                                     String inputVal,
                                     String placeholder,
                                     String auto,
                                     Invoice invoice,
                                     Map<String, String> confByName) {
        String val = inputVal;
        if (val == null || val.isBlank() || GENERIC_PHRASES.contains(val)) {
            // fallback to placeholder
            val = placeholder;
            // If placeholder contains @, fallback to default of the same automation (or T for S)
            if (val.contains("@")) {
                String lookForAuto = "S".equals(auto) ? "T" : auto;
                Optional<String> def = confByName.values().stream()
                        .map(s -> s.split(";"))
                        .filter(s -> s.length > 4 && lookForAuto.equals(s[4]) && !s[2].contains("@"))
                        .map(s -> String.join(";", s))
                        .findFirst();
                if (def.isPresent()) {
                    String[] parts = def.get().split(";");
                    if ("S".equals(auto)) {
                        try {
                            int t = Integer.parseInt(parts[2]);
                            val = numberToWordsPort.numberToWords(t);
                        } catch (NumberFormatException ex) {
                            val = "Błędny format liczby, popraw kwotę całkowitą i spróbuj ponownie";
                        }
                    } else {
                        val = parts[2];
                    }
                } else {
                    val = "Nieznana wartość!!";
                }
            }
        }
        return val;
    }

    public Path computeOutputPath(Invoice invoice, int number) {
        String base = configPort.getInvoiceTreePath();
        LocalDate now = dateTimeProvider.today();
        LocalDateTime nowDT = dateTimeProvider.now();
        String month = MONTHS_PL[now.getMonthValue() - 1];
        return Paths.get(base, "InvoiceHollow", month, String.valueOf(nowDT.getDayOfMonth()),
                invoice.getName() + number + "." + invoice.getExtension());
    }

    public void generateNewInvoice(Invoice invoice, Map<String, String> userInputs) throws IOException {
        ReadyInvoice ready = buildReadyInvoice(invoice, userInputs);
        Path path = computeOutputPath(invoice, ready.getNumber());

        documentFiller.fill(invoice, ready, path);

        // suggestions persistence for autofill fields (simple heuristic: non-generic, non-empty)
//        ready.getPropertyDataMap().forEach((k, v) -> {
//            if (v != null && !v.isBlank() && !GENERIC_PHRASES.contains(v)) {
//                suggestionsPort.rememberSuggestion(k, v);
//            }
//        });

        // archive and persist
        archivePort.saveNew(ready);

        // config updates using total
        int total = computeTotalForConfig(invoice, ready);
        configPort.incrementEarningsAndInvoiceCount(total);
    }

    public void updateExistingInvoice(Invoice invoice, Map<String, String> userInputs) throws IOException {
        ReadyInvoice ready = buildReadyInvoice(invoice, userInputs);
        ready.setupParameterCellMap(invoice.getConfigurationDataString());
        Path path = computeOutputPath(invoice, ready.getNumber());

        ArchivedInvoice old = archivePort.getByNumber(ready.getNumber());
        if (!comparePricingAndUpdate(old, ready)) {
            return;
        }

        documentFiller.fill(invoice, ready, path);
        archivePort.updateExisting(ready);
    }

    private int computeTotalForConfig(Invoice invoice, ReadyInvoice readyInvoice) {
        // If total not present in UI, try take placeholder
        double total = readyInvoice.getTotal();
        if (total == -1) {
            Optional<String> totalField = Arrays.stream(invoice.getConfigurationDataString().split(","))
                    .map(s -> s.split(";"))
                    .filter(s -> s.length > 4 && "T".equals(s[4]))
                    .map(s -> String.join(";", s))
                    .findFirst();
            if (totalField.isPresent()) {
                String[] split = totalField.get().split(";");
                try {
                    total = Double.parseDouble(split[2]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Niepoprawna suma faktury. Sprawdź dane w formularzu.");
                }
            }
        }
        return (int) total;
    }

    private boolean comparePricingAndUpdate(ArchivedInvoice oldInvoice, ReadyInvoice newInvoice) {
        if (oldInvoice != null) {
            TotalCalculatorService totalSvc = new TotalCalculatorService(new Adapters.InvoiceBlueprintRepoAdapter());
            double oldPrice = totalSvc.computeTotal(oldInvoice.getName(), oldInvoice.getPropertyDataMap());
            double newPrice = newInvoice.getTotal();
            if (oldPrice == -1 || newPrice == -1) {
                throw new IllegalArgumentException("Niepoprawna suma faktury. Sprawdź dane w formularzu.");
            }
            if (oldPrice != newPrice) {
                configPort.updateTotalBy(newPrice - oldPrice);
            }
        }
        return true;
    }
}






