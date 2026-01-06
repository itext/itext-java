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

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.commons.utils.MessageFormatUtil;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

final class JsonValueConverter {

    private JsonValueConverter() {
        // Empty constructor
    }

    static String toJson(JsonValue value) {
        try {
            return createObjectMapper().writer().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            // Should never be here
            throw new ITextException(MessageFormatUtil.format(
                    CommonsExceptionMessageConstant.JSON_SERIALIZATION_FAILED, e.getMessage()));
        }
    }

    static JsonValue fromJson(String json) {
        try {
            return createObjectMapper().readValue(json, JsonValue.class);
        } catch (JsonProcessingException e) {
            throw new ITextException(MessageFormatUtil.format(
                    CommonsExceptionMessageConstant.JSON_PARSE_FAILED, e.getMessage()));
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        SimpleModule module = new SimpleModule();
        module.addSerializer(JsonValue.class, new JsonValueSerializer());
        module.addDeserializer(JsonValue.class, new JsonValueDeserializer());
        mapper.registerModule(module);

        return mapper;
    }
}
