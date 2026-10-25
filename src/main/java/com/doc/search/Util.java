package com.doc.search;

import java.util.Arrays;
import java.util.List;

public class Util {

    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return List.of();
        return Arrays.asList(
                text.toLowerCase()
                        .replaceAll("[^a-z0-9\\s]", " ")
                        .split("\\s+")
        );
    }

}
