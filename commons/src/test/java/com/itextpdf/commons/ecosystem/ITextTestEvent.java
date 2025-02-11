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
package com.itextpdf.commons.ecosystem;

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.sequence.SequenceId;

public class ITextTestEvent extends AbstractProductProcessITextEvent {
    private final String eventType;

    public ITextTestEvent(SequenceId sequenceId, IMetaInfo metaInfo, String eventType,
            String productName) {
        super(sequenceId, new ProductData("", productName, "", 2000, 2100), metaInfo, EventConfirmationType.ON_CLOSE);
        this.eventType = eventType;
    }

    public ITextTestEvent(SequenceId sequenceId, ProductData productData, IMetaInfo metaInfo, String eventType,
            EventConfirmationType confirmationType) {
        super(sequenceId, productData, metaInfo, confirmationType);
        this.eventType = eventType;
    }

    public ITextTestEvent(SequenceId sequenceId, ProductData productData, IMetaInfo metaInfo, String eventType) {
        this(sequenceId, productData, metaInfo, eventType, EventConfirmationType.ON_CLOSE);
    }

    @Override
    public String getEventType() {
        return eventType;
    }
}
