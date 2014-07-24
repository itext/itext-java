package com.itextpdf.core.pdf.navigation;

import com.itextpdf.core.pdf.PdfName;

public class PdfNamedDestination extends PdfName implements IPdfDestination {

    public PdfNamedDestination(String name) {
        super(name);
    }
}
