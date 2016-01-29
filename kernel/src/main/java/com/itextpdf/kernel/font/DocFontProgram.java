package com.itextpdf.kernel.font;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;

interface DocFontProgram {
    PdfStream getFontFile();
    PdfName getFontFileName();
    PdfName getSubtype();
}
