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
import com.itextpdf.commons.actions.sequence.SequenceId;

/**
 * Base class to wrap events.
 */
public abstract class AbstractEventWrapper extends AbstractProductProcessITextEvent {
    private final AbstractProductProcessITextEvent event;

    /**
     * Creates a wrapper for the event.
     *
     * @param event is a {@link AbstractProductProcessITextEvent} to wrap
     * @param confirmationType event confirmation type
     */
    protected AbstractEventWrapper(AbstractProductProcessITextEvent event, EventConfirmationType confirmationType) {
        super(event.getSequenceId(), event.getProductData(), event.getMetaInfo(), confirmationType);
        this.event = event;
    }

    /**
     * Creates a wrapper of event associated with {@link SequenceId}.
     *
     * @param updatedSequenceId is a {@link SequenceId} for the document. May be different with
     *                          sequence id of original event
     * @param event is a {@link AbstractProductProcessITextEvent} to wrap
     * @param confirmationType event confirmation type
     */
    protected AbstractEventWrapper(SequenceId updatedSequenceId,
                                   AbstractProductProcessITextEvent event, EventConfirmationType confirmationType) {
        super(updatedSequenceId, event.getProductData(), event.getMetaInfo(), confirmationType);
        this.event = event;
    }

    /**
     * Obtains the wrapped event.
     *
     * @return wrapped event
     */
    public AbstractProductProcessITextEvent getEvent() {
        return event;
    }

    @Override
    public Class<?> getClassFromContext() {
        return getEvent().getClassFromContext();
    }

    @Override
    public String getEventType() {
        return getEvent().getEventType();
    }
}
