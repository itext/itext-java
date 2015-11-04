package com.itextpdf.core.parser;

import com.itextpdf.core.pdf.PdfStream;

/**
 * @author Kevin Day
 * @since iText 5.0.1
 */
public interface XObjectDoHandler {
    public void handleXObject(PdfContentStreamProcessor processor, PdfStream stream);
}
