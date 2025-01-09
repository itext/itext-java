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

import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class that makes it cleaner to process content from pages of a {@link PdfDocument}
 * through a specified RenderListener.
 */
public class PdfDocumentContentParser {

    private final PdfDocument pdfDocument;

    public PdfDocumentContentParser(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * Processes content from the specified page number using the specified listener.
     * Also allows registration of custom IContentOperators that can influence
     * how (and whether or not) the PDF instructions will be parsed.
     *
     * @param <E>                        the type of the renderListener - this makes it easy to chain calls
     * @param pageNumber                 the page number to process
     * @param renderListener             the listener that will receive render callbacks
     * @param additionalContentOperators an optional map of custom ContentOperators for rendering instructions
     * @return the provided renderListener
     */
    public <E extends IEventListener> E processContent(int pageNumber, E renderListener, Map<String, IContentOperator> additionalContentOperators) {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(renderListener, additionalContentOperators);
        processor.processPageContent(pdfDocument.getPage(pageNumber));
        return renderListener;
    }

    /**
     * Processes content from the specified page number using the specified listener
     *
     * @param <E>            the type of the renderListener - this makes it easy to chain calls
     * @param pageNumber     the page number to process
     * @param renderListener the listener that will receive render callbacks
     * @return the provided renderListener
     */
    public <E extends IEventListener> E processContent(int pageNumber, E renderListener) {
        return processContent(pageNumber, renderListener, new HashMap<String, IContentOperator>());
    }

}
