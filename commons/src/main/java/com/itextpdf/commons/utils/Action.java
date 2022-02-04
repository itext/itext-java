package com.itextpdf.commons.utils;

/**
 * Functional interface which takes 0 parameters and returns nothing.
 */
@FunctionalInterface
public interface Action {
    /**
     * Execute action.
     */
    void execute();
}
