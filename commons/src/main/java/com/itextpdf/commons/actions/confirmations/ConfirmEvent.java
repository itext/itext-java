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
package com.itextpdf.commons.actions.confirmations;

import com.itextpdf.commons.actions.AbstractEventWrapper;
import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.sequence.SequenceId;

/**
 * Used to confirm that process associated with some {@link AbstractProductProcessITextEvent}
 * ended successfully.
 */
public class ConfirmEvent extends AbstractEventWrapper {

    /**
     * Creates an instance of confirmation event.
     *
     * @param updatedSequenceId is a {@link SequenceId} for the document. May be different with
     *                          sequence id of original event
     * @param confirmedEvent is an event to confirm
     */
    public ConfirmEvent(SequenceId updatedSequenceId, AbstractProductProcessITextEvent confirmedEvent) {
        super(updatedSequenceId, confirmedEvent, EventConfirmationType.UNCONFIRMABLE);
    }

    /**
     * Creates an instance of confirmation event.
     *
     * @param confirmedEvent is an event to confirm
     */
    public ConfirmEvent(AbstractProductProcessITextEvent confirmedEvent) {
        this(confirmedEvent.getSequenceId(), confirmedEvent);
    }

    /**
     * Returns the {@link AbstractProductProcessITextEvent} associated with confirmed process.
     *
     * @return confirmed event
     */
    public AbstractProductProcessITextEvent getConfirmedEvent() {
        AbstractProductProcessITextEvent event = getEvent();
        if (event instanceof ConfirmEvent) {
            return ((ConfirmEvent) event).getConfirmedEvent();
        }
        return event;
    }
}
