package com.doc.search.search;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchEngine {
    private SearchIndex index;
    private final AtomicBoolean isIndexing = new AtomicBoolean(false);
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private Path searchDir;
    private ExecutorService service;

    public SearchResult search(String query) {
        List<IndexMatch> matches = index.search(query);

        List<MatchResult> results = new ArrayList<>();
        for (IndexMatch match : matches) {
            List<String> snippets = new ArrayList<>();
            List<Integer> lines = new ArrayList<>();

            for (Occurrence occurrence : match.occurrences()) {
                String snippet;
                try (RandomAccessFile file = new RandomAccessFile(match.docPath().toString(), "r")) {
                    file.seek(occurrence.lineByteOffset());
                    snippet = file.readLine();
                } catch (IOException e) {
                    snippet = "Unable to read file.";
                }
                snippets.add(snippet);
                lines.add(occurrence.line());
            }
            results.add(new MatchResult(match.occurrences().size(), match.docPath(), snippets, lines));
        }
        return new SearchResult(results);
    }

    public boolean isIndexing() {
        return isIndexing.get();
    }

    public void setSearchDirectory(Path path) {
        this.searchDir = path;
    }

    public void stopIndexing() {
        if (isIndexing.getAndSet(false)) {
            service.shutdownNow();
            activeTasks.set(0);
            service = null;
        }
    }

    public void startIndexing() {
        stopIndexing();
        isIndexing.set(true);
        this.index = new SearchIndex();
        this.service = Executors.newVirtualThreadPerTaskExecutor();

        try {
            Files.walk(searchDir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt") || p.toString().endsWith(".md"))
                    .forEach(filePath -> service.submit(
                            () -> {
                                activeTasks.incrementAndGet();
                                try {
                                    new FileIndexerTask(filePath, index).run();
                                } finally {
                                    int active = activeTasks.decrementAndGet();
                                    if (active == 0) {
                                        isIndexing.set(false);
                                    }
                                }
                            }));
        } catch (IOException e) {
            System.err.println("An io exceptgio occurred.");
        }
    }
}