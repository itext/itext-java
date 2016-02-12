package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.pdf.PdfStream;

/**
 * @author Kevin Day
 */
public interface XObjectDoHandler {
    public void handleXObject(PdfContentStreamProcessor processor, PdfStream stream);
}
