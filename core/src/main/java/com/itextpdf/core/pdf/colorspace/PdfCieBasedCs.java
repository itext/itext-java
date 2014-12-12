package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

import java.util.ArrayList;

abstract public class PdfCieBasedCs extends PdfColorSpace<PdfArray> {

    public PdfCieBasedCs(PdfArray pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    static public class CalGray extends PdfCieBasedCs {
        public CalGray(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public CalGray(PdfDocument document, float[] whitePoint) throws PdfException {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.CalGray);
                add(new PdfDictionary());
            }}), document);
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalGray(PdfDocument document, float[] whitePoint, float[] blackPoint, float gamma) throws PdfException {
            this(document, whitePoint);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (gamma != Float.NaN)
                d.put(PdfName.Gamma, new PdfNumber(gamma));
        }

        @Override
        public int getNumOfComponents() {
            return 1;
        }
    }

    static public class CalRgb extends PdfCieBasedCs {
        public CalRgb(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public CalRgb(PdfDocument document, float[] whitePoint) throws PdfException {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.CalRGB);
                add(new PdfDictionary());
            }}), document);
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalRgb(PdfDocument document, float[] whitePoint, float[] blackPoint, float[] gamma, float[] matrix) throws PdfException {
            this(document, whitePoint);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (gamma != null)
                d.put(PdfName.Gamma, new PdfArray(gamma));
            if (matrix != null)
                d.put(PdfName.Matrix, new PdfArray(matrix));
        }

        @Override
        public int getNumOfComponents() {
            return 3;
        }
    }

    static public class Lab extends PdfCieBasedCs {
        public Lab(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public Lab(PdfDocument document, float[] whitePoint) throws PdfException {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.Lab);
                add(new PdfDictionary());
            }}), document);
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public Lab(PdfDocument document, float[] whitePoint, float[] blackPoint, float[] range) throws PdfException {
            this(document, whitePoint);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (range != null)
                d.put(PdfName.Range, new PdfArray(range));
        }

        @Override
        public int getNumOfComponents() {
            return 3;
        }
    }




}
