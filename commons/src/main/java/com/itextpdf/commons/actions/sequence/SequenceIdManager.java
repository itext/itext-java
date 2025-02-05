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
package com.itextpdf.commons.actions.sequence;

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;

/**
 * Util class which is responsible for marking of {@link AbstractIdentifiableElement} with
 * appropriate {@link SequenceId}.
 */
public final class SequenceIdManager {
    private SequenceIdManager() {}

    /**
     * Provides an {@link AbstractIdentifiableElement} with a {@link SequenceId}. Note that it is
     * forbidden to override already existing identifier. If try to provide a new one then exception
     * will be thrown.
     *
     * @param element    is an identifiable element
     * @param sequenceId is an identifier to set
     *
     * @throws IllegalStateException if element already has an identifier
     */
    public static void setSequenceId(AbstractIdentifiableElement element, SequenceId sequenceId) {
        synchronized (element) {
            if (element.getSequenceId() == null) {
                element.setSequenceId(sequenceId);
            } else {
                    throw new IllegalStateException(MessageFormatUtil.format(
                            CommonsExceptionMessageConstant.ELEMENT_ALREADY_HAS_IDENTIFIER,
                            element.getSequenceId().getId(), sequenceId.getId()));
            }
        }
    }

    /**
     * Gets an identifier of the element. If it was not provided will return <code>null</code>.
     *
     * @param element is an identifiable element
     *
     * @return the identifier of the element if presented and <code>null</code> otherwise
     */
    public static SequenceId getSequenceId(AbstractIdentifiableElement element) {
        return element.getSequenceId();
    }
}
