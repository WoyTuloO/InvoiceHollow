package com.woytuloo.accountingapp.handlers;

import org.apache.commons.collections4.list.TreeList;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCompleteHandler {

    private Map<String, TreeList<String>> suggestionsMap;


    public AutoCompleteHandler(){
        suggestionsMap =  new HashMap<>();
    }


    public void getSuggestions(String parameterName, String text) {
        TreeList<String> suggestions = suggestionsMap.getOrDefault(parameterName, new TreeList<>());
        suggestions.

                ///  zaimplementowac trie
    }
}
