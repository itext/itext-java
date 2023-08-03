/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.selector.item;

/**
 * Class that bundles some CSS specificity constants.
 */
final class CssSpecificityConstants {

    /**
     * Creates a new {@link CssSpecificityConstants} instance.
     */
    private CssSpecificityConstants() {
    }

    /** The Constant ID_SPECIFICITY. */
    public static final int ID_SPECIFICITY = 1 << 20;
    
    /** The Constant CLASS_SPECIFICITY. */
    public static final int CLASS_SPECIFICITY = 1 << 10;
    
    /** The Constant ELEMENT_SPECIFICITY. */
    public static final int ELEMENT_SPECIFICITY = 1;

}
