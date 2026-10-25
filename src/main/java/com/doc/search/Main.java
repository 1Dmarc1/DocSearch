package com.doc.search;

import com.doc.search.search.SearchEngine;
import com.doc.search.server.Server;


public class Main {
    static void main() {
        SearchEngine engine = new SearchEngine();
        Server server = new Server(engine);
    }
}
