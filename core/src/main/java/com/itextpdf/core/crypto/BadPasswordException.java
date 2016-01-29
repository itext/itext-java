package com.itextpdf.core.crypto;

import com.itextpdf.core.PdfException;

public class BadPasswordException extends PdfException {

    public BadPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadPasswordException(String message) {
        super(message);
    }
}
