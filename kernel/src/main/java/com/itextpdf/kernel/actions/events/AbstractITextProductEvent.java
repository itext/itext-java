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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.kernel.actions.AbstractContextBasedITextEvent;
import com.itextpdf.kernel.actions.data.ProductData;
import com.itextpdf.kernel.counter.event.IMetaInfo;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.lang.ref.WeakReference;

/**
 * Class is recommended for internal usage. Please see {@link AbstractContextBasedITextEvent} for customizable
 * event associated with {@link PdfDocument} or {@link SequenceId}.
 */
public abstract class AbstractITextProductEvent extends AbstractContextBasedITextEvent {
    private final WeakReference<SequenceId> sequenceId;
    private final ProductData productData;

    /**
     * Creates an event associated with {@link SequenceId}. It may contain auxiliary meta data.
     *
     * @param sequenceId is a general identifier for the event
     * @param productData is a description of the product which has generated an event
     * @param metaInfo is an auxiliary meta info
     */
    public AbstractITextProductEvent(SequenceId sequenceId, ProductData productData, IMetaInfo metaInfo) {
        super(metaInfo);
        this.sequenceId = new WeakReference<>(sequenceId);
        this.productData = productData;
    }

    /**
     * Creates an event which is not associated with any object. It may contain auxiliary meta data.
     *
     * @param productData is a description of the product which has generated an event
     * @param metaInfo is an auxiliary meta info
     */
    public AbstractITextProductEvent(ProductData productData, IMetaInfo metaInfo) {
        this(null, productData, metaInfo);
    }

    /**
     * Retrieves an identifier of event source.
     *
     * @return an identifier of event source
     */
    public SequenceId getSequenceId() {
        return (SequenceId) sequenceId.get();
    }

    /**
     * Retrieves a product data.
     *
     * @return information about the product which triggered the event
     */
    public ProductData getProductData() {
        return productData;
    }
}
