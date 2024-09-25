/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.events;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;

/**
 * Event dispatched by PdfDocument.
 */
public class PdfDocumentEvent extends Event {

    /**
     * Dispatched after page is created.
     */
    public static final String START_PAGE = "StartPdfPage";

    /**
     * Dispatched after page is inserted/added into a document.
     */
    public static final String INSERT_PAGE = "InsertPdfPage";

    /**
     * Dispatched after page is removed from a document.
     */
    public static final String REMOVE_PAGE = "RemovePdfPage";

    /**
     * Dispatched before page is flushed to a document.
     * This event isn't necessarily dispatched when a successive page has been created.
     * Keep it in mind when using with highlevel iText API.
     */
    public static final String END_PAGE = "EndPdfPage";

    /**
     * Dispatched before writer is closed.
     */
    public static final String START_WRITER_CLOSING = "StartWriterClosing";
    /**
     * Dispatched after writer is flushed to a document.
     */
    public static final String START_DOCUMENT_CLOSING = "StartDocumentClosing";

    /**
     * The PdfPage associated with this event.
     */
    protected PdfPage page;

    /**
     * The PdfDocument associated with this event.
     */
    private PdfDocument document;

    /**
     * Creates a PdfDocumentEvent.
     *
     * @param type type of the event that fired this event
     * @param document document that fired this event
     */
    public PdfDocumentEvent(String type, PdfDocument document) {
        super(type);
        this.document = document;
    }

    /**
     * Creates a PdfDocumentEvent.
     *
     * @param type type of the event that fired this event
     * @param page page that fired this event
     */
    public PdfDocumentEvent(String type, PdfPage page) {
        super(type);
        this.page = page;
        this.document = page.getDocument();
    }

    /**
     * Returns the PdfDocument associated with this event.
     *
     * @return the PdfDocument associated with this event
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Returns the PdfPage associated with this event. Warning: this can be null.
     *
     * @return the PdfPage associated with this event
     */
    public PdfPage getPage() {
        return page;
    }
}
