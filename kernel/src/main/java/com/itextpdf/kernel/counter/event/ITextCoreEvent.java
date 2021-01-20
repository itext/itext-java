/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.counter.event;

import com.itextpdf.kernel.actions.ProductNameConstant;
import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Class represents events registered in iText core module.
 */
public class ITextCoreEvent extends AbstractITextProductEvent {
    public static final String OPEN_DOCUMENT = "open-document-event";

    private final String eventType;

    /**
     * Creates an event associated with the provided Pdf Document and additional meta data.
     *
     * @param document is a document associated with the event
     * @param metaInfo is an additional meta info
     */
    public ITextCoreEvent(PdfDocument document, IMetaInfo metaInfo, String eventType) {
        super(document, metaInfo);
        this.eventType = eventType;
    }

    /**
     * Creates an event associated with a general identifier and additional meta data.
     *
     * @param sequenceId is an identifier associated with the event
     * @param metaInfo is an additional meta info
     */
    public ITextCoreEvent(SequenceId sequenceId, IMetaInfo metaInfo, String eventType) {
        super(sequenceId, metaInfo);
        this.eventType = eventType;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getProductName() {
        return ProductNameConstant.ITEXT_CORE;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return eventType;
    }
}
