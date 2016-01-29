package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.pdf.PdfStream;

/**
 * @author Kevin Day
 * @since iText 5.0.1
 */
public interface XObjectDoHandler {
    public void handleXObject(PdfContentStreamProcessor processor, PdfStream stream);
}
