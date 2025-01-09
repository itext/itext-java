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
package com.itextpdf.styledxmlparser.node;

import java.util.List;
import java.util.Map;

/**
 * Interface for node classes that have a parent and children, and for which
 * styles can be defined; each of these nodes can also have a name and attributes.
 */
public interface IElementNode extends INode, IStylesContainer, IAttributesContainer, INameContainer {
    /**
     * Gets additional styles, more specifically styles that affect an element
     * based on its position in the HTML DOM, e.g. cell borders that are set
     * due to the parent table "border" attribute, or styles from "col" tags
     * that affect table elements, or blocks horizontal alignment that is
     * the result of parent's "align" attribute.
     *
     * @return the additional html styles
     */
    List<Map<String, String>> getAdditionalHtmlStyles();

    /**
     * Adds additional HTML styles.
     *
     * @param styles the styles
     */
    void addAdditionalHtmlStyles(Map<String, String> styles);

    /**
     * Gets the language.
     *
     * @return the language value
     */
    String getLang();
}
