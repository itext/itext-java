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
package com.itextpdf.kernel.pdf.event;

import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Describes abstract PDF document event of the specified type.
 *
 * <p>
 * Use {@link PdfDocument#dispatchEvent(AbstractPdfDocumentEvent)} to fire an event
 * and {@link PdfDocument#addEventHandler(String, AbstractPdfDocumentEventHandler)}
 * to register {@link AbstractPdfDocumentEventHandler} handler for that type of event.
 */
public abstract class AbstractPdfDocumentEvent implements IEvent {

    /** A type of event. */
    protected String type;
    private PdfDocument document;

    /**
     * Creates an event of the specified type.
     *
     * @param type the type of event
     */
    protected AbstractPdfDocumentEvent(String type) {
        this.type = type;
    }

    /**
     * Returns the type of this event.
     *
     * @return type of this event
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the document associated with this event.
     *
     * @return {@link PdfDocument} that triggered this event
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Sets the document associated with this event.
     *
     * @param document {@link PdfDocument} that triggered this event
     *
     * @return this {@link AbstractPdfDocumentEvent} instance
     */
    public AbstractPdfDocumentEvent setDocument(PdfDocument document) {
        this.document = document;
        return this;
    }
}
