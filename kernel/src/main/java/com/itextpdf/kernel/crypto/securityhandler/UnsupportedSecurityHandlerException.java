package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.PdfException;

public class UnsupportedSecurityHandlerException extends PdfException {

    public static final String UnsupportedSecurityHandler = "Failed to open the document. Security handler {0} is not supported";

    public UnsupportedSecurityHandlerException(String message) {
        super(message);
    }

}
