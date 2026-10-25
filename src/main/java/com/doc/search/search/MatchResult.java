package com.doc.search.search;

import java.nio.file.Path;
import java.util.List;

/**
 * Contains all match results for a document.
 * @param matchCount How many matches were found.
 * @param docPath The path of the document.
 * @param snippets
 */
public record MatchResult (int matchCount, Path docPath, List<String> snippets, List<Integer> lines) {
}
