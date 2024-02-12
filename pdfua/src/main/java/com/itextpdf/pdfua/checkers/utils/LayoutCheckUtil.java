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

import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;

/**
 * Utility class for delegating the layout checks to the correct checking logic.
 */
public final class LayoutCheckUtil {

    /**
     * Creates a new {@link LayoutCheckUtil} instance.
     */
    private LayoutCheckUtil() {
        // Empty constructor
    }

    /**
     * Checks if a layout element is valid against the PDF/UA specification.
     *
     * @param layoutElement layout element to check
     */
    public static void checkLayoutElements(Object layoutElement) {
        if (layoutElement instanceof Image) {
            GraphicsCheckUtil.checkLayoutImage((Image) layoutElement);
            return;
        }
        if (layoutElement instanceof Table){
            TableCheckUtil.checkLayoutTable((Table) layoutElement);
            return;
        }
    }
}
