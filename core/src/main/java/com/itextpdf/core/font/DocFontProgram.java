package com.itextpdf.core.font;

import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfStream;

interface DocFontProgram {
    PdfStream getFontFile();
    PdfName getFontFileName();
    PdfName getSubtype();
}
