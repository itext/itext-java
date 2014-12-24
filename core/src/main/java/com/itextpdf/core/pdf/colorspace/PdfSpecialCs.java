package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

abstract public class PdfSpecialCs extends PdfColorSpace<PdfArray> {

    public PdfSpecialCs(PdfArray pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    static public class Indexed extends PdfSpecialCs {
        public Indexed(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public Indexed(PdfDocument document, PdfObject base, int hival, PdfString lookup) throws PdfException {
            this(getIndexedCsArray(base, hival, lookup), document);
        }

        @Override
        public int getNumOfComponents() throws PdfException {
            return 1;
        }

        public PdfColorSpace getBaseCs(boolean create) throws PdfException {
            return makeColorSpace(((PdfArray) pdfObject).get(0), getDocument());
        }

        static private PdfArray getIndexedCsArray(PdfObject base, int hival, PdfString lookup) {
            PdfArray indexed = new PdfArray();
            indexed.add(PdfName.Indexed);
            indexed.add(base);
            indexed.add(new PdfNumber(hival));
            indexed.add(lookup.setHexWriting(true));
            return indexed;
        }

    }

}
