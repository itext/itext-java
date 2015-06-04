package com.itextpdf.core.crypto;

import com.itextpdf.basics.PdfRuntimeException;

public class BadPasswordException extends PdfRuntimeException {

    public BadPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadPasswordException(String message) {
        super(message);
    }
}
