/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfPage;

import java.util.HashMap;
import java.util.Map;

public final class PdfTextExtractor {

    private PdfTextExtractor() {
    }

    /**
     * Extract text from a specified page using an extraction strategy.
     * Also allows registration of custom IContentOperators that can influence
     * how (and whether or not) the PDF instructions will be parsed.
     * Extraction strategy must be passed as a new object for every single page.
     *
     * @param page     the page for the text to be extracted from
     * @param strategy the strategy to use for extracting text
     * @param additionalContentOperators an optional map of custom {@link IContentOperator}s for rendering instructions
     * @return the extracted text
     */
    public static String getTextFromPage(PdfPage page, ITextExtractionStrategy strategy, Map<String, IContentOperator> additionalContentOperators) {
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy, additionalContentOperators);
        parser.processPageContent(page);
        return strategy.getResultantText();
    }
    
    /**
     * Extract text from a specified page using an extraction strategy.
     * Extraction strategy must be passed as a new object for every single page.
     *
     * @param page     the page for the text to be extracted from
     * @param strategy the strategy to use for extracting text
     * @return the extracted text
     */
    public static String getTextFromPage(PdfPage page, ITextExtractionStrategy strategy) {
        return getTextFromPage(page, strategy, new HashMap<String, IContentOperator>());
    }

    /**
     * Extract text from a specified page using the default strategy.
     * Node: the default strategy is subject to change. If using a specific strategy
     * is important, please use {@link PdfTextExtractor#getTextFromPage(PdfPage, ITextExtractionStrategy)}.
     *
     * @param page the page for the text to be extracted from
     * @return the extracted text
     */
    public static String getTextFromPage(PdfPage page) {
        return getTextFromPage(page, new LocationTextExtractionStrategy());
    }
}
