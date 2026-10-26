package com.doc.search.search;

import com.doc.search.Util;

import java.io.IOException;
import java.io.RandomAccessFile;
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

        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
            int lineCount = 1;

            while (true) {
                // Check for interruption at the top of every iteration
                if (Thread.currentThread().isInterrupted()) {
                    return; // exit task
                }
                long lineByteOffset = file.getFilePointer();
                String line = file.readLine();
                if (line == null) {
                    break; // End of file reached
                }

                List<String> tokens = Util.tokenize(line);
                int searchStartIndex = 0;
                for (String token : tokens) {
                    int tokenCharIndex = line.toLowerCase().indexOf(token, searchStartIndex);

                    if (tokenCharIndex != -1) {
                        long tokenByteOffset = lineByteOffset + tokenCharIndex;

                        wordMap.computeIfAbsent(token, _ -> new ArrayList<>())
                                .add(new Occurrence(lineCount, (int) tokenByteOffset));
                        searchStartIndex = tokenCharIndex + token.length();
                    }
                }
                lineCount++;
            }
            index.addDocument(path, wordMap);
        } catch (IOException e) {
            System.err.println("Failed to index file. " + e);
        }
    }
}
