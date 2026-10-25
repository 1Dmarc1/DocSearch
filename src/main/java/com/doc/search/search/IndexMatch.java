package com.doc.search.search;

import java.nio.file.Path;
import java.util.List;

public record IndexMatch(int docId, Path docPath, List<Occurrence> occurrences) {}
