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
package com.itextpdf.forms.xfdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent annots tag in xfdf document structure.
 * Content model: ( text | caret | freetext | fileattachment | highlight | ink | line | link
 * | circle | square | polygon | polyline | sound | squiggly | stamp |
 * strikeout | underline )*.
 * Attributes: none.
 * For more details see paragraph 6.4.1 in Xfdf specification.
 */
public class AnnotsObject {

    /**
     * Represents a list of children annotations.
     */
    private List<AnnotObject> annotsList;

    /**
     * Creates an instance with the empty list of children annotations.
     */
    public AnnotsObject() {
        annotsList = new ArrayList<>();
    }

    /**
     * Gets children annotations.
     * @return a {@link List} of {@link AnnotObject} each representing a child annotation of this annots tag.
     */
    public List<AnnotObject> getAnnotsList() {
        return annotsList;
    }

    /**
     * Adds a new {@link AnnotObject} to the list of children annotations.
     * @param annot {@link AnnotObject} containing info about pdf document annotation.
     * @return this {@link AnnotsObject} instance.
     */
    public AnnotsObject addAnnot(AnnotObject annot) {
        this.annotsList.add(annot);
        return this;
    }
}
