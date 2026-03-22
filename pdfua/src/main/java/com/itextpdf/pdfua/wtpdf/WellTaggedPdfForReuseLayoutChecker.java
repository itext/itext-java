/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.pdfua.wtpdf;

import com.itextpdf.forms.form.renderer.SignatureAppearanceRenderer;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;

/**
 * Performs layout checks for a PDF document being validated against the Well Tagged PDF for Reuse standard.
 *
 */
public class WellTaggedPdfForReuseLayoutChecker {
    private final PdfUAValidationContext context;

    /**
     * Creates a new {@link WellTaggedPdfForReuseLayoutChecker} instance.
     *
     * @param context the validation context
     */
    public WellTaggedPdfForReuseLayoutChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks renderer for PDF UA compliance.
     *
     * @param renderer the renderer to check
     */
    public void checkRenderer(IRenderer renderer) {
        if (renderer == null) {
            return;
        }
        if (isPartOfSignatureAppearance(renderer)) {
            // Tagging of the current layout element will be skipped in that case.
            return;
        }
        IPropertyContainer layoutElement = renderer.getModelElement();
        if (layoutElement instanceof Table) {
            new TableCheckUtil(context).checkTable((Table) layoutElement);
        }
    }

    private static boolean isPartOfSignatureAppearance(IRenderer renderer) {
        IRenderer parent = renderer.getParent();
        while (parent != null) {
            if (parent instanceof SignatureAppearanceRenderer) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}
