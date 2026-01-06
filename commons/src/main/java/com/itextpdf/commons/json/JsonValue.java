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
package com.itextpdf.commons.json;

/**
 * Class representing generic json value.
 */
public abstract class JsonValue {

    /**
     * Creates a new {@link JsonValue}.
     */
    protected JsonValue() {
        // Empty constructor
    }

    /**
     * Parses string and creates {@link JsonValue} out of it.
     *
     * @param json string to parse
     *
     * @return {@link JsonValue} representing the input string
     */
    public static JsonValue fromJson(String json) {
        return JsonValueConverter.fromJson(json);
    }

    /**
     * Converts this {@link JsonValue} into a string.
     *
     * @return string representing this {@link JsonValue}
     */
    public final String toJson() {
        return JsonValueConverter.toJson(this);
    }
}
