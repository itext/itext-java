package com.itextpdf.commons.utils;

/**
 * Functional interface which takes 0 parameters and returns T and can throw a checked exception.
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws Exception any exception thrown by the encapsulated code
     */
    T get() throws Exception;
}
