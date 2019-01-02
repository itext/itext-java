/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.colors.IccProfile;
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

    private static final long serialVersionUID = 7803780450619297557L;

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
        
    	private static final long serialVersionUID = -3974274460820215173L;

		public CalGray(PdfArray pdfObject) {
            super(pdfObject);
        }

        public CalGray(float[] whitePoint) {
            this(getInitialPdfArray());
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
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
        
    	private static final long serialVersionUID = -2926074370411556426L;

		public CalRgb(PdfArray pdfObject) {
            super(pdfObject);
        }

        public CalRgb(float[] whitePoint) {
            this(getInitialPdfArray());
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
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
        
    	private static final long serialVersionUID = 7067722970343880433L;

		public Lab(PdfArray pdfObject) {
            super(pdfObject);
        }

        public Lab(float[] whitePoint) {
            this(getInitialPdfArray());
            if (whitePoint == null || whitePoint.length != 3)
                throw new PdfException(PdfException.WhitePointIsIncorrectlySpecified, this);
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
        
    	private static final long serialVersionUID = 3265273715107224067L;

		public IccBased(PdfArray pdfObject) {
            super(pdfObject);
        }

        public IccBased(final InputStream iccStream) {
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
