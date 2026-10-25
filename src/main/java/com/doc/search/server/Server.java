package com.doc.search.server;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import com.doc.search.search.SearchEngine;

public class Server {
    public Server(SearchEngine searchEngine) {
        SearchController controller = new SearchController(searchEngine);

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(7000);

        // Register the api endpoints
        app.post("/api/directory", controller::setDirectory);
        app.get("/api/status", controller::getStatus);
        app.get("/api/search", controller::search);
    }
}