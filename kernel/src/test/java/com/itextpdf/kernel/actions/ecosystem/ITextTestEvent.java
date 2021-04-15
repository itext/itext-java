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
package com.itextpdf.kernel.actions.ecosystem;

import com.itextpdf.kernel.actions.data.ProductData;
import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.counter.event.IMetaInfo;
import com.itextpdf.kernel.pdf.PdfDocument;

public class ITextTestEvent extends AbstractITextProductEvent {
    private final String eventType;
    private final String productName;

    public ITextTestEvent(SequenceId sequenceId, IMetaInfo metaInfo, String eventType,
            String productName) {
        super(sequenceId, null, metaInfo);
        this.eventType = eventType;
        this.productName = productName;
    }

    public ITextTestEvent(SequenceId sequenceId, ProductData productData, IMetaInfo metaInfo, String eventType) {
        super(sequenceId, productData, metaInfo);
        this.eventType = eventType;
        this.productName = productData.getModuleName();
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public String getProductName() {
        return productName;
    }
}
