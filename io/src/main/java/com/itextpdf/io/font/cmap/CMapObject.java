/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.font.cmap;

/**
 * Represents a typed object parsed from CMap content.
 */
public class CMapObject {

    protected static final int STRING = 1;
    protected static final int HEX_STRING = 2;
    protected static final int NAME = 3;
    protected static final int NUMBER = 4;
    protected static final int LITERAL = 5;
    protected static final int ARRAY = 6;
    protected static final int DICTIONARY = 7;
    protected static final int TOKEN = 8;

    private int type;
    private Object value;

    /**
     * Creates a typed CMap object.
     *
     * @param objectType one of this class's object-type constants
     * @param value      the value associated with the type
     */
    public CMapObject(int objectType, Object value) {
        this.type = objectType;
        this.value = value;
    }

    /**
     * Returns the value represented by this object.
     *
     * @return the stored value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns this object's type code.
     *
     * @return one of this class's object type constants
     */
    public int getType() {
        return type;
    }

    /**
     * Replaces this object's stored value.
     *
     * @param value the new value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Tests whether this object is a literal or hexadecimal string.
     *
     * @return {@code true} for either string type
     */
    public boolean isString() {
        return type == STRING || type == HEX_STRING;
    }

    /**
     * Tests whether this object is a hexadecimal string.
     *
     * @return {@code true} when this object's type is hexadecimal string
     */
    public boolean isHexString() {
        return type == HEX_STRING;
    }

    /**
     * Tests whether this object is a PDF name.
     *
     * @return {@code true} when this object's type is name
     */
    public boolean isName() {
        return type == NAME;
    }

    /**
     * Tests whether this object is a number.
     *
     * @return {@code true} when this object's type is number
     */
    public boolean isNumber() {
        return type == NUMBER;
    }

    /**
     * Tests whether this object is a command literal.
     *
     * @return {@code true} when this object's type is literal
     */
    public boolean isLiteral() {
        return type == LITERAL;
    }

    /**
     * Tests whether this object is an array.
     *
     * @return {@code true} when this object's type is array
     */
    public boolean isArray() {
        return type == ARRAY;
    }

    /**
     * Tests whether this object is a dictionary.
     *
     * @return {@code true} when this object's type is dictionary
     */
    public boolean isDictionary() {
        return type == DICTIONARY;
    }

    /**
     * Tests whether this object is a structural token.
     *
     * @return {@code true} when this object's type is token
     */
    public boolean isToken() {
        return type == TOKEN;
    }

    /**
     * Return String representation of {@code value} field.
     */
    @Override
    public String toString() {
        if (type == STRING || type == HEX_STRING) {
            byte[] content = (byte[]) value;
            StringBuilder str = new StringBuilder(content.length);
            for (byte b: content) {
                str.append((char)(b & 0xff));
            }
            return str.toString();
        }
        return value.toString();
    }

    /**
     * Returns the byte array of a hexadecimal string object.
     *
     * @return the retained byte array, or {@code null} when this is not a hexadecimal string
     */
    public byte[] toHexByteArray() {
        if (type == HEX_STRING) {
            return (byte[])value;
        } else {
            return null;
        }
    }
}
