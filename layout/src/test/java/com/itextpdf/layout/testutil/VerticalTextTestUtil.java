/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.testutil;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class VerticalTextTestUtil {

    private VerticalTextTestUtil() {
    }

    /**
     * Extracts all text content from page 1 of the given PDF file.
     *
     * @param outFileName path to the PDF file to extract text from
     * @return the extracted text, as returned by {@link LocationTextExtractionStrategy}
     * @throws IOException if the file cannot be read
     */
    public static String extractPageText(String outFileName) throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(outFileName))) {
            return PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1), new LocationTextExtractionStrategy());
        }
    }

    /**
     * Counts occurrences of each non-whitespace character in the given text.
     *
     * @param text the text to count characters in
     * @return a map of character to occurrence count, excluding whitespace characters
     */
    public static Map<Character, Integer> countNonWhitespaceChars(String text) {
        Map<Character, Integer> counts = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!Character.isWhitespace(c)) {
                Integer currentCount = counts.get(c);
                if (currentCount == null) {
                    counts.put(c, 1);
                } else {
                    counts.put(c, currentCount + 1);
                }
            }
        }
        return counts;
    }

    /**
     * Checks whether every non-whitespace character in {@code expected} occurs in {@code extractedCounts}
     * at least as many times as it occurs in {@code expected}. Whitespace and character order are ignored,
     * so this is a multiset containment check rather than a substring or exact-equality check.
     *
     * @param extractedCounts character occurrence counts of the extracted page text,
     *                        as produced by {@link #countNonWhitespaceChars}
     * @param expected        the text whose characters are expected to be present
     * @return true if all non-whitespace characters of {@code expected} are present with sufficient count
     */
    public static boolean containsAllCharacters(Map<Character, Integer> extractedCounts, String expected) {
        for (Map.Entry<Character, Integer> entry : countNonWhitespaceChars(expected).entrySet()) {
            if (extractedCounts.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Extracts text from page 1 of the given PDF file and counts occurrences of each non-whitespace
     * character in it. Equivalent to {@code countNonWhitespaceChars(extractPageText(outFileName))}.
     *
     * @param outFileName path to the PDF file to extract text from
     * @return a map of character to occurrence count, excluding whitespace characters
     * @throws IOException if the file cannot be read
     */
    public static Map<Character, Integer> extractPageCharacterCounts(String outFileName) throws IOException {
        return countNonWhitespaceChars(extractPageText(outFileName));
    }

    /**
     * Checks whether every non-whitespace character in {@code expected} occurs in {@code extractedCounts}
     * at least {@code multiplier} times as often as it occurs in {@code expected}. Used to verify text
     * appears a specific number of times (e.g. once per font in a side-by-side comparison), while
     * remaining robust to the same reading-order caveats as {@link #containsAllCharacters}.
     *
     * @param extractedCounts character occurrence counts of the extracted page text,
     *                        as produced by {@link #countNonWhitespaceChars}
     * @param expected        the text whose characters are expected to be present
     * @param multiplier      how many times each character of {@code expected} is expected to occur
     * @return true if all non-whitespace characters of {@code expected} are present with sufficient count
     */
    public static boolean containsAllCharacters(Map<Character, Integer> extractedCounts, String expected,
                                                int multiplier) {
        for (Map.Entry<Character, Integer> entry : countNonWhitespaceChars(expected).entrySet()) {
            if (extractedCounts.getOrDefault(entry.getKey(), 0) < entry.getValue() * multiplier) {
                return false;
            }
        }
        return true;
    }
}