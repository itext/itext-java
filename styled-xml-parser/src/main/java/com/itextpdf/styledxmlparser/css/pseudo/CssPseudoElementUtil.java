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
package com.itextpdf.styledxmlparser.css.pseudo;


import com.itextpdf.styledxmlparser.node.IElementNode;

/**
 * Utilities class for pseudo elements.
 */
public class CssPseudoElementUtil {

    /**
     * The prefix for pseudo elements.
     */
    private static final String TAG_NAME_PREFIX = "pseudo-element::";

    /**
     * Creates the pseudo element tag name.
     *
     * @param pseudoElementName the pseudo element name
     * @return the tag name
     */
    public static String createPseudoElementTagName(String pseudoElementName) {
        return TAG_NAME_PREFIX + pseudoElementName;
    }

    /**
     * Checks for before or after elements.
     *
     * @param node the node
     * @return true, if successful
     */
    public static boolean hasBeforeAfterElements(IElementNode node) {
        if (node == null || node instanceof CssPseudoElementNode || node.name().startsWith(TAG_NAME_PREFIX)) {
            return false;
        }
        return true;
    }
}
