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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class representing JSON object value.
 */
public final class JsonObject extends JsonValue {
    private final Map<String, JsonValue> fields;

    /**
     * Creates a new empty {@link JsonObject}.
     */
    public JsonObject() {
        super();
        this.fields = new LinkedHashMap<>();
    }

    /**
     * Creates a new {@link JsonObject} with provided fields.
     *
     * @param fields fields to put into JSON object
     */
    public JsonObject(Map<String, JsonValue> fields) {
        super();
        this.fields = new LinkedHashMap<>(fields);
    }

    /**
     * Gets a copy of the JSON object fields.
     *
     * @return JSON object fields
     */
    public Map<String, JsonValue> getFields() {
        return new LinkedHashMap<>(fields);
    }

    /**
     * Gets particular JSON object field.
     *
     * @param fieldName {@link String} name of the field
     *
     * @return field as {@link JsonValue}
     */
    public JsonValue getField(String fieldName) {
        return fields.get(fieldName);
    }

    /**
     * Adds a new field into JSON object.
     *
     * @param key a key to add into JSON object fields
     * @param value a value to add into JSON object fields under the {@code key}
     */
    public void add(String key, JsonValue value) {
        fields.put(key, value);
    }

    /**
     * Removes a field from JSON object.
     *
     * @param key a key to remove a value for
     */
    public void remove(String key) {
        fields.remove(key);
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

        JsonObject that = (JsonObject) obj;
        return this.fields.equals(that.fields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
}
