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
package com.itextpdf.commons.utils;

import com.itextpdf.commons.logs.CommonsLogMessageConstant;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JSON serialization and deserialization operations. Not for public use.
 */
public final class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private JsonUtil() {
        // empty constructor
    }

    /**
     * Compares two json strings without considering the order of the elements.
     *
     * @param expectedString expected json string
     * @param toCompare      string for comparison
     *
     * @return true if two json string are equals, false otherwise
     *
     * @throws IOException if an I/O error occurs
     */
    public static boolean areTwoJsonObjectEquals(String expectedString, String toCompare) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        JsonNode expectedObject = mapper.readTree(expectedString);
        JsonNode actualObject = mapper.readTree(toCompare);

        return actualObject.equals(expectedObject);
    }

    /**
     * Serializes passed object to provided JSON output stream.
     *
     * @param outputStream stream to which the object will be serialized
     * @param value the object which will be serialized
     */
    public static void serializeToStream(OutputStream outputStream, Object value) {
        serializeToStream(outputStream, value, new CustomPrettyPrinter());
    }

    /**
     * Serializes passed object to JSON string.
     *
     * @param value the object which will be serialized
     *
     * @return the JSON string representation of passed object or {@code null} if it is impossible to serialize to JSON
     */
    public static String serializeToString(Object value) {
        return serializeToString(value, new CustomPrettyPrinter());
    }

    /**
     * Serializes passed object to minimal JSON without spaces and line breaks and writes it into provided stream.
     *
     * @param outputStream stream to which the object will be serialized
     * @param value the object which will be serialized
     */
    public static void serializeToMinimalStream(OutputStream outputStream, Object value) {
        serializeToStream(outputStream, value, new MinimalPrinter());
    }

    /**
     * Serializes passed object to minimal JSON string without spaces and line breaks.
     *
     * @param value the object which will be serialized
     *
     * @return the minimal JSON string representation of passed object or {@code null} if it is impossible to
     * serialize to JSON
     */
    public static String serializeToMinimalString(Object value) {
        return serializeToString(value, new MinimalPrinter());
    }

    /**
     * Deserializes passed JSON stream to object with passed type.
     *
     * @param content the JSON stream which represent object
     * @param objectType the class of object as {@link Class} which will be deserialized
     * @param <T> the type of object which will be deserialized
     *
     * @return the deserialized object or {@code null} if operation of deserialization is impossible
     */
    public static <T> T deserializeFromStream(InputStream content, Class<T> objectType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return deserializeFromStream(content, objectMapper.constructType(objectType));
    }

    /**
     * Deserializes passed JSON stream to object with passed type.
     *
     * @param content the JSON stream which represent object
     * @param objectType the class of object as {@link TypeReference} which will be deserialized
     * @param <T> the type of object which will be deserialized
     *
     * @return the deserialized object or {@code null} if operation of deserialization is impossible
     */
    public static <T> T deserializeFromStream(InputStream content, TypeReference<T> objectType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return deserializeFromStream(content, objectMapper.constructType(objectType));
    }

    /**
     * Deserializes passed JSON stream to object with passed type.
     *
     * @param content the JSON stream which represent object
     * @param objectType the class of object as {@link JavaType} which will be deserialized
     * @param <T> the type of object which will be deserialized
     *
     * @return the deserialized object or {@code null} if operation of deserialization is impossible
     */
    public static <T> T deserializeFromStream(InputStream content, JavaType objectType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(content, objectType);
        } catch (IOException ex) {
            LOGGER.warn(MessageFormatUtil.format(
                    CommonsLogMessageConstant.UNABLE_TO_DESERIALIZE_JSON, ex.getClass(), ex.getMessage()));
            return null;
        }
    }

    /**
     * Deserializes passed JSON string to object with passed type.
     *
     * @param content the JSON string which represent object
     * @param objectType the class of object as {@link Class} which will be deserialized
     * @param <T> the type of object which will be deserialized
     *
     * @return the deserialized object or {@code null} if operation of deserialization is impossible
     */
    public static <T> T deserializeFromString(String content, Class<T> objectType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return deserializeFromString(content, objectMapper.constructType(objectType));
    }

    /**
     * Deserializes passed JSON string to object with passed type.
     *
     * @param content the JSON string which represent object
     * @param objectType the class of object as {@link TypeReference} which will be deserialized
     * @param <T> the type of object which will be deserialized
     *
     * @return the deserialized object or {@code null} if operation of deserialization is impossible
     */
    public static <T> T deserializeFromString(String content, TypeReference<T> objectType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return deserializeFromString(content, objectMapper.constructType(objectType));
    }

    /**
     * Deserializes passed JSON string to object with passed type.
     *
     * @param content the JSON string which represent object
     * @param objectType the class of object as {@link JavaType} which will be deserialized
     * @param <T> the type of object which will be deserialized
     *
     * @return the deserialized object or {@code null} if operation of deserialization is impossible
     */
    public static <T> T deserializeFromString(String content, JavaType objectType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(content, objectType);
        } catch (JsonProcessingException ex) {
            LOGGER.warn(MessageFormatUtil.format(
                    CommonsLogMessageConstant.UNABLE_TO_DESERIALIZE_JSON, ex.getClass(), ex.getMessage()));
            return null;
        }
    }

    /**
     * Creates and configure object writer with given {@link DefaultPrettyPrinter}.
     *
     * @param prettyPrinter specified pretty printer for indentation
     *
     * @return configured object writer
     */
    private static ObjectWriter createAndConfigureObjectWriter(DefaultPrettyPrinter prettyPrinter) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Don't serialize null fields
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.disable(Feature.AUTO_CLOSE_TARGET);
        return objectMapper.writer(prettyPrinter);
    }

    /**
     * Serializes passed object to provided JSON output stream.
     *
     * @param outputStream  stream to which the object will be serialized
     * @param value the object which will be serialized
     * @param prettyPrinter specified pretty printer for indentation
     */
    private static void serializeToStream(OutputStream outputStream, Object value,
            DefaultPrettyPrinter prettyPrinter) {
        try {
            createAndConfigureObjectWriter(prettyPrinter).writeValue(outputStream, value);
        } catch (IOException ex) {
            LOGGER.warn(MessageFormatUtil.format(
                    CommonsLogMessageConstant.UNABLE_TO_SERIALIZE_OBJECT, ex.getClass(), ex.getMessage()));
        }
    }

    /**
     * Serializes passed object to JSON string.
     *
     * @param value the object which will be serialized
     * @param prettyPrinter specified pretty printer for indentation
     *
     * @return the JSON string representation of passed object or {@code null} if it is impossible to serialize to JSON
     */
    private static String serializeToString(Object value, DefaultPrettyPrinter prettyPrinter) {
        try {
            return createAndConfigureObjectWriter(prettyPrinter).writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            LOGGER.warn(MessageFormatUtil.format(
                    CommonsLogMessageConstant.UNABLE_TO_SERIALIZE_OBJECT, ex.getClass(), ex.getMessage()));
            return null;
        }
    }

    /**
     * This class is used to define a custom separator and array
     * indent to achieve the same serialization in Java and .NET.
     */
    private static class CustomPrettyPrinter extends DefaultPrettyPrinter {
        public CustomPrettyPrinter() {
            _objectFieldValueSeparatorWithSpaces = ": ";
            indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withLinefeed("\n"));
            indentObjectsWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withLinefeed("\n"));
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CustomPrettyPrinter();
        }
    }

    /**
     * This class is used to define a printer which serialize to
     * minimal string, without extra spaces and line breaks.
     */
    private static class MinimalPrinter extends DefaultPrettyPrinter {
        public MinimalPrinter() {
            _objectFieldValueSeparatorWithSpaces = ":";
            indentArraysWith(new DefaultIndenter("", ""));
            indentObjectsWith(new DefaultIndenter("", ""));
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new MinimalPrinter();
        }
    }
}
