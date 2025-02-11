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
package com.itextpdf.kernel.pdf;

/**
 * Page label numbering style constants for {@link PdfPage#setPageLabel(PageLabelNumberingStyle, String)}.
 */
public enum PageLabelNumberingStyle {
    /**
     * 1, 2, 3, 4...
     */
    DECIMAL_ARABIC_NUMERALS,
    /**
     * I, II, III, IV...
     */
    UPPERCASE_ROMAN_NUMERALS,
    /**
     * i, ii, iii, iv...
     */
    LOWERCASE_ROMAN_NUMERALS,
    /**
     * A, B, C, D...
     */
    UPPERCASE_LETTERS,
    /**
     * a, b, c, d...
     */
    LOWERCASE_LETTERS
}
