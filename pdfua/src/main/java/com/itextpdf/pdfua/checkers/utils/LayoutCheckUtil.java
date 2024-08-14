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
package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;

/**
 * Utility class for delegating the layout checks to the correct checking logic.
 */
public final class LayoutCheckUtil {

    private final PdfUAValidationContext context;

    /**
     * Creates a new {@link LayoutCheckUtil} instance.
     *
     * @param context The validation context.
     */
    public LayoutCheckUtil(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks renderer for PDF UA compliance.
     *
     * @param rendererObj The renderer to check.
     */
    public void checkRenderer(Object rendererObj) {
        if (rendererObj == null) {
            return;
        }
        IPropertyContainer layoutElement = ((IRenderer) rendererObj).getModelElement();
        if (layoutElement instanceof Image) {
            new GraphicsCheckUtil(context).checkLayoutElement((Image) layoutElement);
        } else if (layoutElement instanceof Table) {
            new TableCheckUtil(context).checkTable((Table) layoutElement);
        }
    }
}
