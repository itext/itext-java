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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.sequence.SequenceId;

/**
 * Class represents events registered in iText core module.
 */
public final class ITextCoreProductEvent extends AbstractProductProcessITextEvent {
    /**
     * Process pdf event type.
     */
    public static final String PROCESS_PDF = "process-pdf";

    private final String eventType;

    /**
     * Creates an event associated with a general identifier and additional meta data.
     *
     * @param sequenceId is an identifier associated with the event
     * @param metaInfo   is an additional meta info
     * @param eventType  is a string description of the event
     * @param confirmationType defines when the event should be confirmed to notify that the
     *                         associated process has finished successfully
     */
    private ITextCoreProductEvent(SequenceId sequenceId, IMetaInfo metaInfo, String eventType,
            EventConfirmationType confirmationType) {
        super(sequenceId, ITextCoreProductData.getInstance(), metaInfo, confirmationType);
        this.eventType = eventType;
    }

    /**
     * Creates an process pdf event which associated with a general identifier and additional meta data.
     *
     * @param sequenceId is an identifier associated with the event
     * @param metaInfo is an additional meta info
     * @param confirmationType defines when the event should be confirmed to notify that the
     *                         associated process has finished successfully
     *
     * @return the process pdf event
     */
    public static ITextCoreProductEvent createProcessPdfEvent(SequenceId sequenceId, IMetaInfo metaInfo,
            EventConfirmationType confirmationType) {
        return new ITextCoreProductEvent(sequenceId, metaInfo, PROCESS_PDF, confirmationType);
    }

    @Override
    public String getEventType() {
        return eventType;
    }
}
