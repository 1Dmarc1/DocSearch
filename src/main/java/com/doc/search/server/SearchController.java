package com.doc.search.server;

import io.javalin.http.Context;
import com.doc.search.search.SearchEngine;
import com.doc.search.search.SearchResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SearchController {
    private final SearchEngine searchEngine;

    public SearchController(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public void setDirectory(Context ctx) {
        SetDirectoryRequest request = ctx.bodyAsClass(SetDirectoryRequest.class);

        Path rootDir = Path.of(request.directoryPath());
        if (!Files.exists(rootDir)) {
            ctx.status(400).json(Map.of("error", "Directory does not exist or is invalid."));
            return;
        }

        searchEngine.setSearchDirectory(rootDir);
        searchEngine.startIndexing();

        ctx.status(200).json(Map.of("message", "Indexing..."));
    }

    public void getStatus(Context ctx) {
        StatusResponse response = new StatusResponse(searchEngine.isIndexing());
        ctx.status(200).json(response);
    }

    public void search(Context ctx) {
        String query = ctx.queryParam("q");
        if (query == null || query.isBlank()) {
            ctx.json(List.of());
            return;
        }
        SearchResult results = searchEngine.search(query);
        ctx.json(results);
    }
}

record SetDirectoryRequest(String directoryPath) {
}

record StatusResponse(boolean isIndexing) {
}