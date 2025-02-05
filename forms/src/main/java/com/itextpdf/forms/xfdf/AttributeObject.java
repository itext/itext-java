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


import com.itextpdf.forms.exceptions.XfdfException;

/**
 * Represents the attribute of any XFDF element.
 */
public class AttributeObject {

    private String name;
    private String value;

    /**
     * Creates an instance with given attribute name and value.
     * @param name the name of the attribute, constrained by XML attributes specification.
     * @param value the value of the attribute, constrained by XML attributes specification.
     */
    public AttributeObject(String name, String value) {
        if(name == null || value == null) {
            throw new XfdfException(XfdfException.ATTRIBUTE_NAME_OR_VALUE_MISSING);
        }
        this.name = name;
        this.value = value;
    }

    /**
     * Returns attribute name.
     * @return a string representation of attribute name, case-sensitive as per XML specification.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns attribute value.
     * @return a string representation of attribute value.
     */
    public String getValue() {
        return value;
    }
}
