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
package com.itextpdf.commons.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing json array value.
 */
public final class JsonArray extends JsonValue {
    private final List<JsonValue> values;

    /**
     * Creates a new empty {@link JsonArray}.
     */
    public JsonArray() {
        super();
        this.values = new ArrayList<>();
    }

    /**
     * Creates a new {@link JsonArray} with provided values.
     *
     * @param values values to put into json array
     */
    public JsonArray(List<JsonValue> values) {
        super();
        this.values = new ArrayList<>(values);
    }

    /**
     * Gets a copy of json array values.
     *
     * @return json array values
     */
    public List<JsonValue> getValues() {
        return new ArrayList<>(values);
    }

    /**
     * Adds a new value into json array.
     *
     * @param value a value to put into json array
     */
    public void add(JsonValue value) {
        values.add(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        JsonArray that = (JsonArray) obj;
        return this.values.equals(that.values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
