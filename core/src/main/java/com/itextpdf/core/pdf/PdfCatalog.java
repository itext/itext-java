package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.objects.*;

import java.io.IOException;
import java.util.List;

public class PdfCatalog extends PdfDictionary {

    private PdfCatalog() {
        super();
    }

    public PdfCatalog(PdfDocument doc) {
        super(doc);
        put(PdfName.Type, PdfName.Catalog);
    }

    public void addPages(List<PdfPage> pages) {
        put(PdfName.Pages, new PdfPages(pdfDocument, pages));
    }

    class PdfPages extends PdfDictionary {
        private PdfPages() {
            super();
        }

        public PdfPages(PdfDocument doc, List<PdfPage> pages) {
            super(doc);
            put(PdfName.Type, PdfName.Pages);
            put(PdfName.Count, new PdfNumber(pages.size()));
            PdfArray kids = new PdfArray(doc);
            for (PdfPage page : pages) {
                page.put(PdfName.Parent, this);
                kids.add(page);
            }
            put(PdfName.Kids, kids);
        }
    }

}
