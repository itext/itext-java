package com.itextpdf.commons.utils;


/**
 * Functional interface which takes 0 parameters and returns nothing and can throw a checked exception.
 */
@FunctionalInterface
public interface ThrowingAction {
    /**
     * Execute action.
     *
     * @throws Exception any exception thrown by the encapsulated code
     */
    void execute() throws Exception;
}
