package com.itextpdf.pdfa;

import com.itextpdf.basics.PdfException;

public class PdfAConformanceException extends PdfException {

    public PdfAConformanceException(String message) {
        super(message);
    }

    public PdfAConformanceException(String message, Object object) {
        super(message, object);
    }
}

