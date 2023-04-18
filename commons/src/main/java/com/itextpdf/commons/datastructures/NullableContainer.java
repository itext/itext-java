package com.itextpdf.commons.datastructures;

/**
 * A simple container that can hold a value.
 * This is class is used to make the autoporting of primitive types easier.
 * For example autoporting enums will convert them to non nullable types.
 * But if you embed them in a NullableContainer, the autoporting will convert them to nullable types.
 */
public class NullableContainer<T> {

    private final T value;

    /**
     * Creates a new {@link NullableContainer} instance.
     *
     * @param value the value
     */
    public NullableContainer(T value) {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public T getValue() {
        return value;
    }
}
