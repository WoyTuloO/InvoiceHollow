package com.woytuloo.accountingapp.handlers;

import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Set<String> suggestions = new HashSet<>();
}

class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String prefix, String word) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.suggestions.add(word);
    }


    public Set<String> search(String prefix) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return Collections.emptySet();
            }
            node = node.children.get(ch);
        }
        return node.suggestions;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("kra", "kraków");
        trie.insert("j", "jabłko");
        trie.insert("j", "jedzenie");

        System.out.println(trie.search("kra"));
        System.out.println(trie.search("j"));
        System.out.println(trie.search("ka"));
    }
}
