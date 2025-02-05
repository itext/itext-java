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
 * Represents the fields element, a child of the xfdf element and is the container for form field elements.
 * Content model: ( field* ).
 * Attributes: none.
 * For more details see paragraph 6.3.1 in Xfdf specification.
 */
public class FieldsObject {

    /**
     * Represents a list of children fields
     */
    private List<FieldObject> fieldList;

    /**
     * Creates an instance of {@link FieldsObject}.
     */
    public FieldsObject() {
        this.fieldList = new ArrayList<>();
    }

    /**
     * Gets a list of children fields
     *
     * @return {@link List} containing all children {@link FieldObject field objects}
     */
    public List<FieldObject> getFieldList() {
        return fieldList;
    }

    /**
     * Adds a new field to the list.
     *
     * @param field FieldObject containing the info about the form field
     *
     * @return current {@link FieldsObject fields object}
     */
    public FieldsObject addField(FieldObject field) {
        this.fieldList.add(field);
        return this;
    }
}
