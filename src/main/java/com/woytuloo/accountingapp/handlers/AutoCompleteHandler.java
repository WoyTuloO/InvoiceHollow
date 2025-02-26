package com.woytuloo.accountingapp.handlers;

import org.apache.commons.collections4.list.TreeList;

import javax.swing.*;
import java.util.*;

public class AutoCompleteHandler {

    private Map<String, HashSet<String>> paramSuggestionsMap;

    public AutoCompleteHandler(){
        paramSuggestionsMap = new HashMap<>();
    }



    public HashSet<String> getSuggestions(String paramName, String text) {
        HashSet<String> result = new HashSet<>();
        HashSet<String> suggestions = paramSuggestionsMap.getOrDefault(paramName, new HashSet<>());

        for(String word : suggestions){
            if(word.startsWith(text)){
                result.add(word);
            }
        }
        return result;
    }

    public void fillSuggestions(String paramName, String value){
        paramSuggestionsMap.getOrDefault(paramName, new HashSet<>()).add(value);
    }

}
