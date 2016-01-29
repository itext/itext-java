package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.PdfException;
import com.itextpdf.io.color.IccProfile;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfStream;

import java.io.InputStream;
import java.util.ArrayList;

abstract public class PdfCieBasedCs extends PdfColorSpace<PdfArray> {

    public PdfCieBasedCs(PdfArray pdfObject, PdfDocument document) {
        super(pdfObject);
        makeIndirect(document);
    }

    static public class CalGray extends PdfCieBasedCs {
        public CalGray(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
        }

        public CalGray(PdfDocument document, float[] whitePoint) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.CalGray);
                add(new PdfDictionary());
            }}), document);
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalGray(PdfDocument document, float[] whitePoint, float[] blackPoint, float gamma) {
            this(document, whitePoint);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (gamma != Float.NaN)
                d.put(PdfName.Gamma, new PdfNumber(gamma));
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }
    }

    static public class CalRgb extends PdfCieBasedCs {
        public CalRgb(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
        }

        public CalRgb(PdfDocument document, float[] whitePoint) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.CalRGB);
                add(new PdfDictionary());
            }}), document);
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalRgb(PdfDocument document, float[] whitePoint, float[] blackPoint, float[] gamma, float[] matrix) {
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
        public int getNumberOfComponents() {
            return 3;
        }
    }

    static public class Lab extends PdfCieBasedCs {
        public Lab(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
        }

        public Lab(PdfDocument document, float[] whitePoint) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.Lab);
                add(new PdfDictionary());
            }}), document);
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public Lab(PdfDocument document, float[] whitePoint, float[] blackPoint, float[] range) {
            this(document, whitePoint);
            PdfDictionary d = ((PdfArray) getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (range != null)
                d.put(PdfName.Range, new PdfArray(range));
        }

        @Override
        public int getNumberOfComponents() {
            return 3;
        }
    }

    static public class IccBased extends PdfCieBasedCs {
        public IccBased(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
        }

        public IccBased(PdfDocument document, final InputStream iccStream) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.ICCBased);
                add(getIccProfileStream(iccStream));
            }}), document);
        }

        public IccBased(PdfDocument document, final InputStream iccStream, final float[] range) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.ICCBased);
                add(getIccProfileStream(iccStream, range));
            }}), document);
        }

        @Override
        public int getNumberOfComponents() {
            return ((PdfArray) getPdfObject()).getAsStream(1).getAsInt(PdfName.Action.N);
        }


        static public PdfStream getIccProfileStream(InputStream iccStream) {
            IccProfile iccProfile = IccProfile.getInstance(iccStream);
            PdfStream stream = new PdfStream(iccProfile.getData());
            stream.put(PdfName.N, new PdfNumber(iccProfile.getNumComponents()));
            switch (iccProfile.getNumComponents()) {
                case 1:
                    stream.put(PdfName.Alternate, PdfName.DeviceGray);
                    break;
                case 3:
                    stream.put(PdfName.Alternate, PdfName.DeviceRGB);
                    break;
                case 4:
                    stream.put(PdfName.Alternate, PdfName.DeviceCMYK);
                    break;
                default:
                    break;
            }
            return stream;
        }

        static public PdfStream getIccProfileStream(InputStream iccStream, float[] range) {
            PdfStream stream = getIccProfileStream(iccStream);
            stream.put(PdfName.Range, new PdfArray(range));
            return stream;
        }
    }


}
