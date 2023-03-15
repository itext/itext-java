/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

import java.util.List;

/**
 * The class that represents the Pdf Function.
 *
 *  @deprecated Will be removed is future releases, use {@link AbstractPdfFunction} instead
 */
@Deprecated
public class PdfFunction extends PdfObjectWrapper<PdfObject> {

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
        return range == null ? 0 : (range.size() / 2);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     *  Represents a type 0 pdf function.
     *
     *  @deprecated Will be removed is future releases, use {@link PdfType0Function} instead
     */
    @Deprecated
    public static class Type0 extends PdfFunction {
		public Type0(PdfStream pdfObject) {
            super(pdfObject);
        }

        public Type0(PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, byte[] samples) {
            this(domain, range, size, bitsPerSample, null, null, null, samples);
        }

        public Type0(PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, PdfNumber order,
                PdfArray encode, PdfArray decode, byte[] samples) {
            this(makeType0(domain, range, size, bitsPerSample, order, encode, decode, samples));
        }

        @Override
        public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
            return getInputSize() == 1 && getOutputSize() == alternateSpace.getNumberOfComponents();
        }

        private static PdfStream makeType0(PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample,
                PdfNumber order, PdfArray encode, PdfArray decode, byte[] samples) {
            PdfStream stream = new PdfStream(samples);
            stream.put(PdfName.FunctionType, new PdfNumber(0));
            stream.put(PdfName.Domain, domain);
            stream.put(PdfName.Range, range);
            stream.put(PdfName.Size, size);
            stream.put(PdfName.BitsPerSample, bitsPerSample);
            if (order != null) {
                stream.put(PdfName.Order, order);
            }
            if (encode != null) {
                stream.put(PdfName.Encode, encode);
            }
            if (decode != null) {
                stream.put(PdfName.Decode, decode);
            }
            return stream;
        }
    }

    /**
     * Represents a type 2 pdf function.
     *
     *  @deprecated Will be removed is future releases, use {@link PdfType2Function} instead
     */
    @Deprecated
    public static class Type2 extends PdfFunction {

        public Type2(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public Type2(PdfArray domain, PdfArray range, PdfNumber n) {
            this(domain, range, null, null, n);
        }

        public Type2(PdfArray domain, PdfArray range, PdfArray c0, PdfArray c1, PdfNumber n) {
            this(makeType2(domain, range, c0, c1, n));
        }

        @Override
        public int getOutputSize() {
            final PdfArray range = ((PdfDictionary)getPdfObject()).getAsArray(PdfName.C1);
            return range == null ? 0 : range.size();
        }

        private static PdfDictionary makeType2(PdfArray domain, PdfArray range, PdfArray c0, PdfArray c1, PdfNumber n) {
            PdfDictionary dictionary = new PdfDictionary();
            dictionary.put(PdfName.FunctionType, new PdfNumber(2));
            dictionary.put(PdfName.Domain, domain);
            if (range != null) {
                dictionary.put(PdfName.Range, range);
            }
            if (c0 != null) {
                dictionary.put(PdfName.C0, c0);
            }
            if (c1 != null) {
                dictionary.put(PdfName.C1, c1);
            }
            dictionary.put(PdfName.N, n);
            return dictionary;
        }
    }

    /**
     *  Represents a type 3 pdf function.
     *
     *  @deprecated Will be removed is future releases, use {@link PdfType3Function} instead
     */
    @Deprecated
    public static class Type3 extends PdfFunction {
		public Type3(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public Type3(PdfArray domain, PdfArray range, PdfArray functions, PdfArray bounds, PdfArray encode) {
            this(makeType3(domain, range, functions, bounds, encode));
        }

        public Type3(PdfArray domain, PdfArray range, List<PdfFunction> functions, PdfArray bounds, PdfArray encode) {
            this(domain, range, getFunctionsArray(functions), bounds, encode);
        }

        private static PdfDictionary makeType3(PdfArray domain, PdfArray range, PdfArray functions, PdfArray bounds,
                PdfArray encode) {
            PdfDictionary dictionary = new PdfDictionary();
            dictionary.put(PdfName.FunctionType, new PdfNumber(3));
            dictionary.put(PdfName.Domain, domain);
            if (range != null) {
                dictionary.put(PdfName.Range, range);
            }
            dictionary.put(PdfName.Functions, functions);
            dictionary.put(PdfName.Bounds, bounds);
            dictionary.put(PdfName.Encode, encode);
            return dictionary;
        }

        private static PdfArray getFunctionsArray(List<PdfFunction> functions) {
            PdfArray array = new PdfArray();
            for (final PdfFunction function : functions) {
                array.add(function.getPdfObject());
            }
            return array;
        }
    }

    /**
     *  Represents a type 4 pdf function.
     *  @deprecated Will be removed is future releases, use {@link PdfType4Function} instead
     */
    @Deprecated
    public static class Type4 extends PdfFunction {

		
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
        switch (pdfObject.getAsNumber(PdfName.FunctionType).intValue()) {
            case 0:
                return new Type0((PdfStream)pdfObject);
            case 2:
                return new Type2(pdfObject);
            case 3:
                return new Type3(pdfObject);
            case 4:
                return new Type4((PdfStream)pdfObject);
            default:
                return null;
        }
    }
}
