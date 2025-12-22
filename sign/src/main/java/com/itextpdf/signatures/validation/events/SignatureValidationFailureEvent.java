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
package com.itextpdf.signatures.validation.events;

/**
 * This event is triggered after signature validation failed for the current signature.
 */
public class SignatureValidationFailureEvent implements IValidationEvent {
    private final boolean isInconclusive;
    private final String reason;

    /**
     * Create a new event instance.
     *
     * @param isInconclusive {@code true} when validation is neither valid nor invalid,
     *                       {@code false} when it is invalid
     * @param reason         the failure reason
     */
    public SignatureValidationFailureEvent(boolean isInconclusive, String reason) {
        this.isInconclusive = isInconclusive;
        this.reason = reason;
    }

    /**
     * Returns whether the result was inconclusive.
     *
     * @return whether the result was inconclusive
     */
    public boolean isInconclusive() {
        return isInconclusive;
    }

    /**
     * Returns the reason of the failure.
     *
     * @return  the reason of the failure
     */
    public String getReason() {
        return reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.SIGNATURE_VALIDATION_FAILURE;
    }
}
