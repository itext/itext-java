package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.function.PdfFunction;

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

        public PdfColorSpace getBaseCs() throws PdfException {
            return makeColorSpace(((PdfArray) pdfObject).get(1), getDocument());
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

    static public class Separation extends PdfSpecialCs {
        public Separation(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public Separation(PdfDocument document, PdfName name, PdfObject alternateSpace, PdfObject tintTransform) throws PdfException {
            this(getSeparationCsArray(name, alternateSpace, tintTransform), document);
        }

        public Separation(PdfDocument document, PdfName name, PdfColorSpace alternateSpace, PdfFunction tintTransform) throws PdfException {
            this(getSeparationCsArray(name, alternateSpace.getPdfObject(), tintTransform.getPdfObject()), document);
            if (tintTransform.getInputSize() != 1 || tintTransform.getOutputSize() != alternateSpace.getNumOfComponents()) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        @Override
        public int getNumOfComponents() throws PdfException {
            return 1;
        }

        public PdfColorSpace getBaseCs() throws PdfException {
            return makeColorSpace(((PdfArray) pdfObject).get(2), getDocument());
        }

        public PdfName getName() throws PdfException {
            return ((PdfArray) pdfObject).getAsName(1);
        }

        static private PdfArray getSeparationCsArray(PdfName name, PdfObject alternateSpace, PdfObject tintTransform) {
            PdfArray separation = new PdfArray();
            separation.add(PdfName.Separation);
            separation.add(name);
            separation.add(alternateSpace);
            separation.add(tintTransform);
            return separation;
        }

    }


}
