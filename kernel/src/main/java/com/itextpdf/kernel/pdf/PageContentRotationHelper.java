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
package com.itextpdf.kernel.pdf;

/**
 * Helper class to specify or check whether inverse matrix is already applied to the page content stream in case
 * page rotation is applied and {@link PdfPage#setIgnorePageRotationForContent(boolean)} is set to {@code true}.
 *
 * <p>
 * Page rotation inverse matrix rotates content into the opposite direction from page rotation direction
 * in order to give the impression of the not rotated text. It should be applied only once for the page.
 */
public final class PageContentRotationHelper {

    /**
     * Checks if page rotation inverse matrix (which rotates content into the opposite direction from the page rotation
     * direction in order to give the impression of the not rotated text) is already applied to the page content stream.
     * See {@link PdfPage#setIgnorePageRotationForContent(boolean)}.
     *
     * @param page {@link PdfPage} to check applied content rotation for
     *
     * @return {@code true} if inverse matrix is already applied, {@code false} otherwise
     */
    public static boolean isPageRotationInverseMatrixWritten(PdfPage page) {
        return page.isPageRotationInverseMatrixWritten();
    }

    /**
     * Specify that inverse matrix (which rotates content into the opposite direction from the page rotation
     * direction in order to give the impression of the not rotated text) is applied to the page content stream.
     * See {@link PdfPage#setIgnorePageRotationForContent(boolean)}.
     *
     * @param page {@link PdfPage} for which to specify that content rotation is applied
     */
    public static void setPageRotationInverseMatrixWritten(PdfPage page) {
        page.setPageRotationInverseMatrixWritten();
    }

    private PageContentRotationHelper() {
        // Private constructor will prevent the instantiation of this class directly.
    }
}
