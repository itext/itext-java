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
package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

import java.util.List;

public class PdfFunction extends PdfObjectWrapper<PdfObject> {

    private static final long serialVersionUID = -4689848231547125520L;

	public PdfFunction(PdfObject pdfObject) {
        super(pdfObject);
    }

    public int getType() {
        return (int) ((PdfDictionary)getPdfObject()).getAsInt(PdfName.FunctionType);
    }

    public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
        return true;
    }

    public int getInputSize() {
        return ((PdfDictionary)getPdfObject()).getAsArray(PdfName.Domain).size() / 2;
    }

    public int getOutputSize() {
        PdfArray range = ((PdfDictionary)getPdfObject()).getAsArray(PdfName.Range);
        return range == null ? 0 : range.size() / 2;
    }

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

    public static class Type0 extends PdfFunction {

        private static final long serialVersionUID = 72188160295017639L;

		public Type0(PdfStream pdfObject) {
            super(pdfObject);
        }

        public Type0(PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, byte[] samples) {
            this(domain, range, size, bitsPerSample, null, null, null, samples);
        }

        public Type0(PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, PdfNumber order, PdfArray encode, PdfArray decode, byte[] samples) {
            this(makeType0(domain, range, size, bitsPerSample, order, encode, decode, samples));
        }

        @Override
        public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
            return getInputSize() == 1 && getOutputSize() == alternateSpace.getNumberOfComponents();
        }

        private static PdfStream makeType0(PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, PdfNumber order, PdfArray encode, PdfArray decode, byte[] samples) {
            PdfStream stream = new PdfStream(samples);
            stream.put(PdfName.FunctionType, new PdfNumber(0));
            stream.put(PdfName.Domain, domain);
            stream.put(PdfName.Range, range);
            stream.put(PdfName.Size, size);
            stream.put(PdfName.BitsPerSample, bitsPerSample);
            if (order != null)
                stream.put(PdfName.Order, order);
            if (encode != null)
                stream.put(PdfName.Encode, encode);
            if (decode != null)
                stream.put(PdfName.Decode, decode);
            return stream;
        }
    }

    public static class Type2 extends PdfFunction {

       private static final long serialVersionUID = -4680660755798263091L;

		public Type2(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public Type2(PdfArray domain, PdfArray range, PdfNumber n) {
            this(domain, range, null, null, n);
        }

        public Type2(PdfArray domain, PdfArray range, PdfArray c0, PdfArray c1, PdfNumber n) {
            this(makeType2(domain, range, c0, c1, n));
        }

        private static PdfDictionary makeType2(PdfArray domain, PdfArray range, PdfArray c0, PdfArray c1, PdfNumber n) {
            PdfDictionary dictionary = new PdfDictionary();
            dictionary.put(PdfName.FunctionType, new PdfNumber(2));
            dictionary.put(PdfName.Domain, domain);
            if (range != null)
                dictionary.put(PdfName.Range, range);
            if (c0 != null)
                dictionary.put(PdfName.C0, c0);
            if (c1 != null)
                dictionary.put(PdfName.C1, c1);
            dictionary.put(PdfName.N, n);
            return dictionary;
        }
    }

    public static class Type3 extends PdfFunction {

		private static final long serialVersionUID = 3257795209767645155L;

		public Type3(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public Type3(PdfArray domain, PdfArray range, PdfArray functions, PdfArray bounds, PdfArray encode) {
            this(makeType3(domain, range, functions, bounds, encode));
        }

        public Type3(PdfArray domain, PdfArray range, List<PdfFunction> functions, PdfArray bounds, PdfArray encode) {
            this(domain, range, getFunctionsArray(functions), bounds, encode);
        }

        private static PdfDictionary makeType3(PdfArray domain, PdfArray range, PdfArray functions, PdfArray bounds, PdfArray encode) {
            PdfDictionary dictionary = new PdfDictionary();
            dictionary.put(PdfName.FunctionType, new PdfNumber(3));
            dictionary.put(PdfName.Domain, domain);
            dictionary.put(PdfName.Range, range);
            dictionary.put(PdfName.Functions, functions);
            dictionary.put(PdfName.Bounds, bounds);
            dictionary.put(PdfName.Encode, encode);
            return dictionary;
        }

        private static PdfArray getFunctionsArray(List<PdfFunction> functions) {
            PdfArray array = new PdfArray();
            for (PdfFunction function : functions)
                array.add(function.getPdfObject());
            return array;
        }
    }

    public static class Type4 extends PdfFunction {

        private static final long serialVersionUID = -5415624427845744618L;
		
		public Type4(PdfStream pdfObject) {
            super(pdfObject);
        }

        public Type4(PdfArray domain, PdfArray range, byte[] ps) {
            this(makeType4(domain, range, ps));
        }

        @Override
        public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
            return getInputSize() == 1 && getOutputSize() == alternateSpace.getNumberOfComponents();
        }

        private static PdfStream makeType4(PdfArray domain, PdfArray range, byte[] ps) {
            PdfStream stream = new PdfStream(ps);
            stream.put(PdfName.FunctionType, new PdfNumber(4));
            stream.put(PdfName.Domain, domain);
            stream.put(PdfName.Range, range);
            return stream;
        }
    }

    public static PdfFunction makeFunction(PdfDictionary pdfObject) {
        switch (pdfObject.getType()) {
            case 0:
                return new Type0((PdfStream)pdfObject);
            case 2:
                return new Type2(pdfObject);
            case 3:
                return new Type3(pdfObject);
            case 4:
                return new Type4((PdfStream)pdfObject);
        }
        return null;
    }

}
