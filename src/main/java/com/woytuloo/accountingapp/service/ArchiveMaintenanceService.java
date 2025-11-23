package com.woytuloo.accountingapp.service;

import java.util.*;

public class ArchiveMaintenanceService {

    // limit to 1000 last entries (assuming natural order in provided list reflects chronology)
    public List<String> applyLimit(List<String> archived, int limit) {
        if (archived == null) return Collections.emptyList();
        if (archived.size() <= limit) return new ArrayList<>(archived);
        return new ArrayList<>(archived.subList(archived.size() - limit, archived.size()));
    }

    // merge unique lines from incoming into archived
    public List<String> mergeIfDifferent(List<String> archived, List<String> incoming) {
        if (archived == null) archived = new ArrayList<>();
        if (incoming == null || incoming.isEmpty()) return new ArrayList<>(archived);
        Set<String> set = new LinkedHashSet<>(archived);
        boolean changed = false;
        for (String line : incoming) {
            if (!set.contains(line)) { set.add(line); changed = true; }
        }
        if (!changed) return new ArrayList<>(archived);
        return new ArrayList<>(set);
    }
}
