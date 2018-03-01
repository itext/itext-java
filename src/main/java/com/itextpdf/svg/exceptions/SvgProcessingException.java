package com.itextpdf.svg.exceptions;

import com.itextpdf.kernel.PdfException;

/**
 * Exception thrown by {@link com.itextpdf.svg.processors.ISvgProcessor} when it cannot process an SVG
 */
public class SvgProcessingException extends PdfException {

    /**
     * Creates a new {@link SvgProcessingException} instance.
     *
     * @param message the message
     */
    public SvgProcessingException(String message) {
        super(message);
    }
    
    /**
     * Creates a new {@link SvgProcessingException} instance.
     *
     * @param message the message
     * @param cause the nested exception
     */
    public SvgProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates a new {@link SvgProcessingException} instance.
     *
     * @param cause the nested exception
     */
    public SvgProcessingException(Throwable cause) {
        super(cause);
    }
}
