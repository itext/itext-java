package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.color.IccProfile;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.InputStream;
import java.util.ArrayList;

abstract public class PdfCieBasedCs extends PdfColorSpace<PdfArray> {

    private static final long serialVersionUID = 7803780450619297557L;

	@Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    public PdfCieBasedCs(PdfArray pdfObject) {
        super(pdfObject);
    }

    static public class CalGray extends PdfCieBasedCs {
        
    	private static final long serialVersionUID = -3974274460820215173L;

		public CalGray(PdfArray pdfObject) {
            super(pdfObject);
        }

        public CalGray(float[] whitePoint) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.CalGray);
                add(new PdfDictionary());
            }}));
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = (getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalGray(float[] whitePoint, float[] blackPoint, float gamma) {
            this(whitePoint);
            PdfDictionary d = (getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (gamma != Float.NaN)
                d.put(PdfName.Gamma, new PdfNumber(gamma));
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
        }
    }

    static public class CalRgb extends PdfCieBasedCs {
        
    	private static final long serialVersionUID = -2926074370411556426L;

		public CalRgb(PdfArray pdfObject) {
            super(pdfObject);
        }

        public CalRgb(float[] whitePoint) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.CalRGB);
                add(new PdfDictionary());
            }}));
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = (getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalRgb(float[] whitePoint, float[] blackPoint, float[] gamma, float[] matrix) {
            this(whitePoint);
            PdfDictionary d = (getPdfObject()).getAsDictionary(1);
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

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
        }
    }

    static public class Lab extends PdfCieBasedCs {
        
    	private static final long serialVersionUID = 7067722970343880433L;

		public Lab(PdfArray pdfObject) {
            super(pdfObject);
        }

        public Lab(float[] whitePoint) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.Lab);
                add(new PdfDictionary());
            }}));
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
            PdfDictionary d = (getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public Lab(float[] whitePoint, float[] blackPoint, float[] range) {
            this(whitePoint);
            PdfDictionary d = (getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (range != null)
                d.put(PdfName.Range, new PdfArray(range));
        }

        @Override
        public int getNumberOfComponents() {
            return 3;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
        }
    }

    static public class IccBased extends PdfCieBasedCs {
        
    	private static final long serialVersionUID = 3265273715107224067L;

		public IccBased(PdfArray pdfObject) {
            super(pdfObject);
        }

        public IccBased(final InputStream iccStream) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.ICCBased);
                add(getIccProfileStream(iccStream));
            }}));
        }

        public IccBased(final InputStream iccStream, final float[] range) {
            this(new PdfArray(new ArrayList<PdfObject>() {{
                add(PdfName.ICCBased);
                add(getIccProfileStream(iccStream, range));
            }}));
        }

        @Override
        public int getNumberOfComponents() {
            return (getPdfObject()).getAsStream(1).getAsInt(PdfName.Action.N);
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
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
