/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.InputStream;
import java.util.ArrayList;

public abstract class PdfCieBasedCs extends PdfColorSpace {


    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

	@Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    protected PdfCieBasedCs(PdfArray pdfObject) {
        super(pdfObject);
    }

    public static class CalGray extends PdfCieBasedCs {
        

		public CalGray(PdfArray pdfObject) {
            super(pdfObject);
        }

        public CalGray(float[] whitePoint) {
            this(getInitialPdfArray());
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED, this);
            PdfDictionary d = ((PdfArray)getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalGray(float[] whitePoint, float[] blackPoint, float gamma) {
            this(whitePoint);
            PdfDictionary d = ((PdfArray)getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (gamma != Float.NaN)
                d.put(PdfName.Gamma, new PdfNumber(gamma));
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }

        private static PdfArray getInitialPdfArray() {
            ArrayList<PdfObject> tempArray = new ArrayList<PdfObject>(2);
            tempArray.add(PdfName.CalGray);
            tempArray.add(new PdfDictionary());
            return new PdfArray(tempArray);
        }
    }

    public static class CalRgb extends PdfCieBasedCs {
        

		public CalRgb(PdfArray pdfObject) {
            super(pdfObject);
        }

        public CalRgb(float[] whitePoint) {
            this(getInitialPdfArray());
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED, this);
            PdfDictionary d = ((PdfArray)getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public CalRgb(float[] whitePoint, float[] blackPoint, float[] gamma, float[] matrix) {
            this(whitePoint);
            PdfDictionary d = ((PdfArray)getPdfObject()).getAsDictionary(1);
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

        private static PdfArray getInitialPdfArray() {
            ArrayList<PdfObject> tempArray = new ArrayList<PdfObject>(2);
            tempArray.add(PdfName.CalRGB);
            tempArray.add(new PdfDictionary());
            return new PdfArray(tempArray);
        }
    }

    public static class Lab extends PdfCieBasedCs {
        

		public Lab(PdfArray pdfObject) {
            super(pdfObject);
        }

        public Lab(float[] whitePoint) {
            this(getInitialPdfArray());
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED, this);
            PdfDictionary d = ((PdfArray)getPdfObject()).getAsDictionary(1);
            d.put(PdfName.WhitePoint, new PdfArray(whitePoint));
        }

        public Lab(float[] whitePoint, float[] blackPoint, float[] range) {
            this(whitePoint);
            PdfDictionary d = ((PdfArray)getPdfObject()).getAsDictionary(1);
            if (blackPoint != null)
                d.put(PdfName.BlackPoint, new PdfArray(blackPoint));
            if (range != null)
                d.put(PdfName.Range, new PdfArray(range));
        }

        @Override
        public int getNumberOfComponents() {
            return 3;
        }

        private static PdfArray getInitialPdfArray() {
            ArrayList<PdfObject> tempArray = new ArrayList<PdfObject>(2);
            tempArray.add(PdfName.Lab);
            tempArray.add(new PdfDictionary());
            return new PdfArray(tempArray);
        }
    }

    public static class IccBased extends PdfCieBasedCs {
        

		public IccBased(PdfArray pdfObject) {
            super(pdfObject);
        }

        public IccBased(final InputStream iccStream) {
		    // TODO DEVSIX-4217 add parsing of the Range
            this(getInitialPdfArray(iccStream, null));
        }

        public IccBased(final InputStream iccStream, final float[] range) {
            this(getInitialPdfArray(iccStream, range));
        }

        @Override
        public int getNumberOfComponents() {
            return (int) ((PdfArray)getPdfObject()).getAsStream(1).getAsInt(PdfName.N);
        }

        public static PdfStream getIccProfileStream(InputStream iccStream) {
            IccProfile iccProfile = IccProfile.getInstance(iccStream);
            return getIccProfileStream(iccProfile);
        }

        public static PdfStream getIccProfileStream(InputStream iccStream, float[] range) {
            IccProfile iccProfile = IccProfile.getInstance(iccStream);
            return getIccProfileStream(iccProfile, range);
        }

        public static PdfStream getIccProfileStream(IccProfile iccProfile) {
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

        public static PdfStream getIccProfileStream(IccProfile iccProfile, float[] range) {
            PdfStream stream = getIccProfileStream(iccProfile);
            stream.put(PdfName.Range, new PdfArray(range));
            return stream;
        }

        private static PdfArray getInitialPdfArray(final InputStream iccStream, final float[] range) {
            ArrayList<PdfObject> tempArray = new ArrayList<PdfObject>(2);
            tempArray.add(PdfName.ICCBased);
            tempArray.add(range == null ? getIccProfileStream(iccStream) : getIccProfileStream(iccStream, range));
            return new PdfArray(tempArray);
        }
    }
}
