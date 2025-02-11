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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.sequence.SequenceId;

import java.lang.ref.WeakReference;

/**
 * Abstract class which defines product process event. Only for internal usage.
 */
public abstract class AbstractProductProcessITextEvent extends AbstractContextBasedITextEvent {
    private final WeakReference<SequenceId> sequenceId;
    private final EventConfirmationType confirmationType;

    /**
     * Creates an event associated with {@link SequenceId}. It may contain auxiliary meta data.
     *
     * @param sequenceId is a general identifier for the event
     * @param productData is a description of the product which has generated an event
     * @param metaInfo is an auxiliary meta info
     * @param confirmationType defines when the event should be confirmed to notify that the
     *                         associated process has finished successfully
     */
    protected AbstractProductProcessITextEvent(SequenceId sequenceId, ProductData productData, IMetaInfo metaInfo,
            EventConfirmationType confirmationType) {
        super(productData, metaInfo);

        this.sequenceId = new WeakReference<>(sequenceId);
        this.confirmationType = confirmationType;
    }

    /**
     * Creates an event which is not associated with any object. It may contain auxiliary meta data.
     *
     * @param productData is a description of the product which has generated an event
     * @param metaInfo is an auxiliary meta info
     * @param confirmationType defines when the event should be confirmed to notify that the
     *                         associated process has finished successfully
     */
    protected AbstractProductProcessITextEvent(ProductData productData, IMetaInfo metaInfo,
            EventConfirmationType confirmationType) {
        this(null, productData, metaInfo, confirmationType);
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
     * Returns an event type.
     *
     * @return event type
     */
    public abstract String getEventType();

    /**
     * Retrieves an {@link EventConfirmationType event confirmation type}.
     *
     * @return value of event confirmation type which defines when the event should be confirmed
     * to notify that the associated process has finished successfully
     */
    public EventConfirmationType getConfirmationType() {
        return confirmationType;
    }
}
