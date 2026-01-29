package com.itextpdf.commons.json;

/**
 * Interface which marks classes serializable to JSON AST.
 */
public interface IJsonSerializable {
    /**
     * Serializes object to JSON AST.
     *
     * @return {@link JsonValue} serialized object
     */
    JsonValue toJson();
}
