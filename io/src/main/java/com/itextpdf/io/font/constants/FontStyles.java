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
package com.itextpdf.io.font.constants;

public final class FontStyles {

    private FontStyles() {
    }

    /**
     * Undefined font style.
     */
    public static final int UNDEFINED = -1;
    /**
     * Normal font style.
     */
    public static final int NORMAL = 0;
    /**
     * Bold font style.
     */
    public static final int BOLD = 1;
    /**
     * Italic font style.
     */
    public static final int ITALIC = 2;
    /**
     * Bold-Italic font style.
     */
    public static final int BOLDITALIC = BOLD | ITALIC;
}
