package com.itextpdf.core.pdf.filespec;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;

public class PdfDictionaryFS extends PdfDictionary implements IPdfFileSpec {

    public PdfDictionaryFS() {
        super();
    }

    public PdfDictionaryFS(PdfDocument doc) {
        super(doc);
    }


}
