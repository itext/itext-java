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
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for PDF document events handling based on the event type.
 *
 * <p>
 * Handles {@link AbstractPdfDocumentEvent} event fired by {@link PdfDocument#dispatchEvent(AbstractPdfDocumentEvent)}.
 * Use {@link PdfDocument#addEventHandler(String, AbstractPdfDocumentEventHandler)} to register this handler for
 * specific type of event.
 */
public abstract class AbstractPdfDocumentEventHandler implements IEventHandler {
    private final Set<String> types = new HashSet<>();

    /**
     * Creates new {@link AbstractPdfDocumentEventHandler} instance.
     *
     * <p>
     * By default, this instance handles all types of the {@link AbstractPdfDocumentEvent} events. For specific types
     * handling, use {@link #addType(String)} method.
     */
    protected AbstractPdfDocumentEventHandler() {
    }

    /**
     * Adds new event type to handle by this {@link AbstractPdfDocumentEventHandler} instance.
     *
     * @param type the {@link AbstractPdfDocumentEvent} type to handle
     *
     * @return this {@link AbstractPdfDocumentEventHandler} instance
     */
    public AbstractPdfDocumentEventHandler addType(String type) {
        this.types.add(type);
        return this;
    }

    @Override
    public void onEvent(IEvent event) {
        if (!(event instanceof AbstractPdfDocumentEvent)) {
            return;
        }
        final AbstractPdfDocumentEvent iTextEvent = (AbstractPdfDocumentEvent) event;
        if (types.isEmpty() || types.contains(iTextEvent.getType())) {
            onAcceptedEvent(iTextEvent);
        }
    }

    /**
     * Handles the accepted event.
     *
     * @param event {@link AbstractPdfDocumentEvent} to handle
     */
    protected abstract void onAcceptedEvent(AbstractPdfDocumentEvent event);
}
