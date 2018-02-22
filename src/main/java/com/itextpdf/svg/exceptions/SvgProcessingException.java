package com.itextpdf.svg.exceptions;

/**
 * Exception thrown by {@link com.itextpdf.svg.processors.ISvgProcessor} when it cannot process an SVG
 */
public class SvgProcessingException extends RuntimeException {

    /**
     * Creates a new {@link SvgProcessingException} instance.
     *
     * @param message the message
     */
    public SvgProcessingException(String message) {
        super(message);
    }
}
