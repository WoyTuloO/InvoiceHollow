package com.woytuloo.accountingapp.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArchiveMaintenanceServiceTest {

    @Test
    public void testMergeIfDifferentAppendsUniquePreservingOrder() {
        ArchiveMaintenanceService service = new ArchiveMaintenanceService();
        List<String> archived = Arrays.asList("a", "b", "c");
        List<String> incoming = Arrays.asList("b", "c", "d", "e", "c", "e", "f", "d");

        List<String> result = service.mergeIfDifferent(archived, incoming);

        List<String> expected = Arrays.asList("a", "b", "c", "d", "e", "f");
        assertEquals(expected, result);
    }

    @Test
    public void testApplyLimitReturnsLastNInOrder() {
        ArchiveMaintenanceService service = new ArchiveMaintenanceService();
        List<String> archived = Arrays.asList("1", "2", "3", "4", "5");

        List<String> result = service.applyLimit(archived, 3);

        List<String> expected = Arrays.asList("3", "4", "5");
        assertEquals(expected, result);
    }

    @Test
    public void testApplyLimitReturnsCopyWhenUnderOrEqualLimit() {
        ArchiveMaintenanceService service = new ArchiveMaintenanceService();
        List<String> archived = new ArrayList<>(Arrays.asList("a", "b", "c"));

        List<String> result = service.applyLimit(archived, 3);

        assertEquals(archived, result);
        assertNotSame(archived, result);
    }

    @Test
    public void testMergeIfDifferentNoChangeWhenIncomingFullyDuplicate() {
        ArchiveMaintenanceService service = new ArchiveMaintenanceService();
        List<String> archived = new ArrayList<>(Arrays.asList("x", "y", "x", "z"));
        List<String> incoming = Arrays.asList("x", "y", "z", "y");

        List<String> result = service.mergeIfDifferent(archived, incoming);

        assertEquals(archived, result);
        assertNotSame(archived, result);
    }

    @Test
    public void testMergeIfDifferentDeduplicatesArchivedOnChange() {
        ArchiveMaintenanceService service = new ArchiveMaintenanceService();
        List<String> archived = Arrays.asList("a", "b", "b", "c", "a");
        List<String> incoming = Arrays.asList("d");

        List<String> result = service.mergeIfDifferent(archived, incoming);

        List<String> expected = Arrays.asList("a", "b", "c", "d");
        assertEquals(expected, result);
    }

    @Test
    public void testApplyLimitNullArchivedReturnsEmptyList() {
        ArchiveMaintenanceService service = new ArchiveMaintenanceService();

        List<String> result = service.applyLimit(null, 100);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}