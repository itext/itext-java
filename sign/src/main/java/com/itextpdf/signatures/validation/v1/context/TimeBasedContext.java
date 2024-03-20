package com.itextpdf.signatures.validation.v1.context;

/**
 * This enum is used for giving a perspective on a time period at which validation is happening.
 */
public enum TimeBasedContext {
    /**
     * The date used lies in the past.
     */
    HISTORICAL,
    /**
     * The date used lies in the present or there is no date.
     */
    PRESENT
}