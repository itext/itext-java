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

import com.itextpdf.kernel.actions.sequence.SequenceId;

/**
 * Used to confirm that process associated with some {@link AbstractProductProcessITextEvent}
 * ended successfully.
 */
public class ConfirmEvent extends AbstractProductProcessITextEvent {
    private final AbstractProductProcessITextEvent confirmedEvent;

    /**
     * Creates an instance of confirmation event.
     *
     * @param updatedSequenceId is a {@link SequenceId} for the document. May be different with
     *                          sequence id of original event if {@link LinkDocumentIdEvent}
     *                          was generated before to link some events with another document
     * @param confirmedEvent is an event to confirm
     */
    public ConfirmEvent(SequenceId updatedSequenceId, AbstractProductProcessITextEvent confirmedEvent) {
        super(updatedSequenceId, confirmedEvent.getProductData(), confirmedEvent.getMetaInfo(),
                EventConfirmationType.UNCONFIRMABLE);

        this.confirmedEvent = confirmedEvent;
    }

    /**
     * Creates an instance of confirmation event.
     *
     * @param confirmedEvent is an event to confirm
     */
    public ConfirmEvent(AbstractProductProcessITextEvent confirmedEvent) {
        this(confirmedEvent.getSequenceId(), confirmedEvent);
    }

    @Override
    public String getEventType() {
        return confirmedEvent.getEventType();
    }

    @Override
    public String getProductName() {
        return confirmedEvent.getProductName();
    }

    /**
     * Returns the {@link AbstractProductProcessITextEvent} associated with confirmed process.
     *
     * @return confirmed event
     */
    public AbstractProductProcessITextEvent getConfirmedEvent() {
        if (confirmedEvent instanceof ConfirmEvent) {
            return ((ConfirmEvent) confirmedEvent).getConfirmedEvent();
        }
        return confirmedEvent;
    }

    @Override
    public Class<?> getClassFromContext() {
        return confirmedEvent.getClassFromContext();
    }
}
