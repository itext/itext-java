/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class for sign type validation context.
 */
public class SignTypeValidationContext implements IValidationContext {
    private final boolean isCAdES;

    /**
     * Instantiates a new {@link SignTypeValidationContext} based on whether sign is CAdeS or not.
     *
     * @param isCAdES whether sign is CAdeS or not
     */
    public SignTypeValidationContext(boolean isCAdES) {
        this.isCAdES = isCAdES;
    }

    /**
     * Whether sign is CAdeS or not.
     *
     * @return whether sign is CAdeS or not
     */
    public boolean isCAdES() {
        return isCAdES;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.SIGNATURE_TYPE;
    }
}
