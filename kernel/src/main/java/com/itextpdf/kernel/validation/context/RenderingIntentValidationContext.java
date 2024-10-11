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

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class for rendering intent validation context.
 */
public class RenderingIntentValidationContext implements IValidationContext {
    private final PdfName intent;

    /**
     * Instantiates a new {@link RenderingIntentValidationContext} based on pdf name.
     *
     * @param intent the intent pdf name
     */
    public RenderingIntentValidationContext(PdfName intent) {
        this.intent = intent;
    }

    /**
     * Gets the intent pdf name.
     *
     * @return the intent pdf name
     */
    public PdfName getIntent() {
        return intent;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.RENDERING_INTENT;
    }
}
