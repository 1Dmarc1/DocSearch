package com.doc.search.search;

import java.util.List;

/// Contains all the match results of a search.
public record SearchResult(List<MatchResult> matchResults) {
}
