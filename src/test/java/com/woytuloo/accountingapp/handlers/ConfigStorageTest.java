package com.woytuloo.accountingapp.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigStorageTest {

    private String originalUserHome;
    private Path tempHomeDir;
    private Path documentsDir;
    private Path invooFolder;
    private Path configDir;
    private Path configFile;

    private static final String[] MONTHS_PL = new String[]{
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
    };

    @BeforeEach
    void setUp() throws Exception {
        originalUserHome = System.getProperty("user.home");
        tempHomeDir = Files.createTempDirectory("user-home-test");
        documentsDir = tempHomeDir.resolve("Documents");
        invooFolder = documentsDir.resolve("InvoiceHollow");
        configDir = invooFolder.resolve("Config");
        configFile = configDir.resolve("appConfig.csv");
        Files.createDirectories(documentsDir);
        System.setProperty("user.home", tempHomeDir.toString());
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setProperty("user.home", originalUserHome);
        if (Files.exists(tempHomeDir)) {
            deleteRecursively(tempHomeDir.toFile());
        }
    }

    private void deleteRecursively(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) deleteRecursively(c);
            }
        }
        f.delete();
    }

    @Test
    void testLoadAndSaveRoundTripPersistsConfigState() throws Exception {
        Files.createDirectories(configDir);

        ConfigStorage cs = new ConfigStorage();
        cs.setInvoiceTreePath(documentsDir.toString());
        cs.setCurrentInvoiceNum(42);
        cs.setYearlyTarget(123456);

        // Prepare month maps with deterministic data
        Map<String, Integer> amt = cs.getMonthAmmountMap();
        Map<String, Double> inc = cs.getMonthIncomeMap();
        amt.clear();
        inc.clear();
        LinkedHashMap<String, Integer> expectedAmt = new LinkedHashMap<>();
        LinkedHashMap<String, Double> expectedInc = new LinkedHashMap<>();
        for (int i = 0; i < 12; i++) {
            expectedAmt.put(MONTHS_PL[i], i);
            expectedInc.put(MONTHS_PL[i], i * 100.5);
        }
        expectedAmt.forEach(amt::put);
        expectedInc.forEach(inc::put);

        ConfigStorage.saveConfigToFile();

        // Mutate current state to ensure load actually changes it
        cs.setCurrentInvoiceNum(7);
        cs.setInvoiceTreePath(tempHomeDir.resolve("Other").toString());
        cs.setYearlyTarget(1);
        amt.clear();
        inc.clear();

        // Load from file
        ConfigStorage cs2 = new ConfigStorage();
        cs2.loadConfigFile();

        assertEquals(42, ConfigStorage.getCurrentInvoiceNum());
        assertEquals(documentsDir.toString(), ConfigStorage.getInvoiceTreePath());
        assertEquals(123456, cs2.getYearlyTarget());

        assertEquals(expectedAmt, cs2.getMonthAmmountMap());
        assertEquals(expectedInc, cs2.getMonthIncomeMap());
    }

    @Test
    void testIncrementEarningsAndCountUpdatesCurrentMonthAndInvoiceNumber() {
        ConfigStorage cs = new ConfigStorage();
        cs.setInvoiceTreePath(documentsDir.toString()); // ensure path set

        // Initialize maps with zeros for all months
        Map<String, Integer> amt = cs.getMonthAmmountMap();
        Map<String, Double> inc = cs.getMonthIncomeMap();
        amt.clear();
        inc.clear();
        for (String m : MONTHS_PL) {
            amt.put(m, 0);
            inc.put(m, 0.0);
        }

        int beforeInvoiceNum = ConfigStorage.getCurrentInvoiceNum();
        int add = 250;

        cs.incrementEarningsAndInvoiceCount(add);

        LocalDate now = LocalDate.now();
        String currentMonthPL = MONTHS_PL[now.getMonthValue() - 1];

        assertEquals(add, cs.getThisMonthsEarnings(), 0.0001);
        assertEquals(1, cs.getThisMonthsInvoiceCount());
        assertEquals(add, cs.getCurrentlyEarned());
        assertEquals(beforeInvoiceNum + 1, ConfigStorage.getCurrentInvoiceNum());
    }

    @Test
    void testSetupTreeCreatesInvoiceFolderStructureForToday() throws Exception {
        ConfigStorage cs = new ConfigStorage();
        cs.setInvoiceTreePath(documentsDir.toString());

        LocalDate now = LocalDate.now();
        LocalDateTime ldt = LocalDateTime.now();

        Path expectedMonthDir = documentsDir.resolve("InvoiceHollow").resolve(MONTHS_PL[now.getMonthValue() - 1]);
        Path expectedDayDir = expectedMonthDir.resolve(String.valueOf(ldt.getDayOfMonth()));

        assertTrue(Files.exists(expectedMonthDir), "Month directory should exist");
        assertTrue(Files.exists(expectedDayDir), "Day directory should exist");
    }

    @Test
    void testUpdateTotalByTruncatesCurrentlyEarnedButPreservesMonthlyDouble() {
        ConfigStorage cs = new ConfigStorage();
        cs.setInvoiceTreePath(documentsDir.toString());

        // Initialize maps with zeros for all months
        Map<String, Integer> amt = cs.getMonthAmmountMap();
        Map<String, Double> inc = cs.getMonthIncomeMap();
        amt.clear();
        inc.clear();
        for (String m : MONTHS_PL) {
            amt.put(m, 0);
            inc.put(m, 0.0);
        }

        LocalDate now = LocalDate.now();
        String monthPL = MONTHS_PL[now.getMonthValue() - 1];

        cs.updateTotalBy(123.75);

        assertEquals(123.75, inc.get(monthPL), 0.0001);
        assertEquals(123, cs.getCurrentlyEarned());
    }

    @Test
    void testSaveConfigToFileHandlesMissingConfigDirectoryGracefully() {
        // Ensure Config directory does not exist
        assertFalse(Files.exists(configDir));
        assertDoesNotThrow(ConfigStorage::saveConfigToFile);
    }
}
