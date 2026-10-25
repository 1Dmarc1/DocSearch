package com.doc.search.search;

import com.doc.search.Util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchIndex {
    private final ConcurrentMap<String, Map<Integer, List<Occurrence>>> invertedIndex = new ConcurrentHashMap<>();
    private final AtomicInteger docIdCtr = new AtomicInteger(0);
    private final Map<Integer, Path> documentIDMap = new ConcurrentHashMap<>();

    public List<IndexMatch> search(String query) {
        List<String> searchTokens = Util.tokenize(query.trim());
        List<IndexMatch> result = new ArrayList<>();

        for (String token : searchTokens) {
            if (invertedIndex.containsKey(token)) {
                invertedIndex.get(token).forEach((docID, occurrences) -> {
                    Path documentPath = documentIDMap.get(docID);
                    result.add(new IndexMatch(docID, documentPath, List.copyOf(occurrences)));
                });
                return result;
            }
        }
        return List.of();
    }

    /// Adds all occurrences in a file to the index.
    public void addDocument(Path path, Map<String, List<Occurrence>> wordMap) {
        int id = docIdCtr.incrementAndGet();
        documentIDMap.put(id, path);

        for (String word : wordMap.keySet()) {
            if (!invertedIndex.containsKey(word)) {
                invertedIndex.put(word, new HashMap<>());
            }
            invertedIndex.get(word).put(id, List.copyOf(wordMap.get(word)));
        }
    }
}