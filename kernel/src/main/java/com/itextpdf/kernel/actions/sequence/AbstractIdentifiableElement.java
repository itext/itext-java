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
package com.itextpdf.kernel.actions.sequence;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.KernelLogMessageConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class allows to make any element identifiable so that it is possible to store some metadata
 * for it.
 */
public abstract class AbstractIdentifiableElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIdentifiableElement.class);

    private SequenceId sequenceId;

    /**
     * Obtains an identifier if it was set.
     *
     * @return identifier
     */
    public SequenceId getSequenceId() {
        return sequenceId;
    }

    /**
     * Sets an identifier. Note that it is forbidden to override already existing identifier. New
     * one will be ignored if element already has an identifier.
     *
     * @param sequenceId is a new identifier for the element
     */
    public void setSequenceId(SequenceId sequenceId) {
        if (this.sequenceId == null) {
            this.sequenceId = sequenceId;
        } else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(MessageFormatUtil.format(KernelLogMessageConstant.ELEMENT_ALREADY_HAS_AN_IDENTIFIER,
                        this.sequenceId, sequenceId));
            }
        }
    }
}
