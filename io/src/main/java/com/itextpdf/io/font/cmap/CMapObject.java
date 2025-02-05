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
package com.itextpdf.io.font.cmap;

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

    public CMapObject(int objectType, Object value) {
        this.type = objectType;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isString() {
        return type == STRING || type == HEX_STRING;
    }

    public boolean isHexString() {
        return type == HEX_STRING;
    }

    public boolean isName() {
        return type == NAME;
    }

    public boolean isNumber() {
        return type == NUMBER;
    }

    public boolean isLiteral() {
        return type == LITERAL;
    }

    public boolean isArray() {
        return type == ARRAY;
    }

    public boolean isDictionary() {
        return type == DICTIONARY;
    }

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

    public byte[] toHexByteArray() {
        if (type == HEX_STRING) {
            return (byte[])value;
        } else {
            return null;
        }
    }
}
