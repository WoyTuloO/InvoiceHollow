package com.woytuloo.accountingapp.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


public interface SuggestionsRepository {
    Map<String, Set<String>> load() throws IOException;
    void save(Map<String, Set<String>> data) throws IOException;
}
