package com.doc.search.search;

import com.doc.search.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileIndexerTask implements Runnable {
    private final Path path;
    private final SearchIndex index;

    public FileIndexerTask(Path path, SearchIndex index) {
        this.path = path;
        this.index = index;
    }

    @Override
    public void run() {
        Map<String, List<Occurrence>> wordMap = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            int lineByteOffset = 0;
            int lineCount = 1;

            // Read the file line by line
            String line;
            while ((line = reader.readLine()) != null){
                List<String> tokens = Util.tokenize(line); // Split each line into tokens

                for (String token : tokens) {
                    if (!wordMap.containsKey(token)) {
                        wordMap.put(token, new ArrayList<>());
                    }
                    wordMap.get(token).add(new Occurrence(lineCount, lineByteOffset));
                }
                lineByteOffset += line.getBytes(StandardCharsets.UTF_8).length + 2;
                lineCount++;
            }
            // Add the result of this document to the index
            index.addDocument(path, wordMap);
        } catch (IOException e) {
            System.err.println("Failed to index file. " + e);
        }
    }


}
