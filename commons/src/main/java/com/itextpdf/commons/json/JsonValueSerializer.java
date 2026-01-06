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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

class JsonValueSerializer extends JsonSerializer<JsonValue> {

    JsonValueSerializer() {
        super();
    }

    @Override
    public void serialize(JsonValue value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value instanceof JsonNull) {
            gen.writeNull();
        } else if (value instanceof JsonBoolean) {
            gen.writeBoolean(((JsonBoolean) value).getValue());
        } else if (value instanceof JsonNumber) {
            final double doubleValue = ((JsonNumber) value).getValue();
            if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                throw new IOException("NAN and INFINITE are not supported");
            }
            if ((long) doubleValue == doubleValue) {
                gen.writeNumber((long) doubleValue);
            } else {
                gen.writeNumber(doubleValue);
            }
        } else if (value instanceof JsonString) {
            String stringValue = ((JsonString) value).getValue();
            if (stringValue == null) {
                gen.writeNull();
            } else {
                gen.writeString(stringValue);
            }
        } else if (value instanceof JsonArray) {
            gen.writeStartArray();
            for (JsonValue item : ((JsonArray) value).getValues()) {
                gen.writeObject(item);
            }
            gen.writeEndArray();
        } else if (value instanceof JsonObject) {
            gen.writeStartObject();
            for (Map.Entry<String, JsonValue> entry : ((JsonObject) value).getFields().entrySet()) {
                gen.writeFieldName(entry.getKey());
                gen.writeObject(entry.getValue());
            }
            gen.writeEndObject();
        } else {
            // Should never be here
            throw new IllegalStateException("Unknown JsonValue subclass: " + value.getClass());
        }
    }
}
