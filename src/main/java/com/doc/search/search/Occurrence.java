package com.doc.search.search;

/**
 * Represents a single occurrence.
 * @param line
 * @param tokenByteOffset
 */
public record Occurrence(int line, int tokenByteOffset){
}