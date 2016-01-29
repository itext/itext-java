package com.itextpdf.kernel.crypto;

import com.itextpdf.kernel.PdfException;

public class BadPasswordException extends PdfException {

    public BadPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadPasswordException(String message) {
        super(message);
    }
}
