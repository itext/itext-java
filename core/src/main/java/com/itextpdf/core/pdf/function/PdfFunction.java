package com.itextpdf.core.pdf.function;

import com.itextpdf.core.pdf.*;

import java.util.List;

public class PdfFunction<T extends PdfObject> extends PdfObjectWrapper {

    public PdfFunction(PdfObject pdfObject, PdfDocument pdfDocument) {
        super(pdfObject);
        makeIndirect(pdfDocument);
    }

    public int getType() {
        return ((PdfDictionary)getPdfObject()).getAsInt(PdfName.FunctionType);
    }

    public int getInputSize() {
        return ((PdfDictionary)getPdfObject()).getAsArray(PdfName.Domain).size() / 2;
    }

    public int getOutputSize() {
        return ((PdfDictionary)getPdfObject()).getAsArray(PdfName.Range).size() / 2;
    }

    static public class Type0 extends PdfFunction<PdfStream> {

        public Type0(PdfStream pdfObject, PdfDocument pdfDocument) {
            super(pdfObject, pdfDocument);
        }

        public Type0(PdfDocument document, PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, byte[] samples) {
            this(document, domain, range, size, bitsPerSample, null, null, null, samples);
        }

        public Type0(PdfDocument document, PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, PdfNumber order, PdfArray encode, PdfArray decode, byte[] samples) {
            this(makeType0(document, domain, range, size, bitsPerSample, order, encode, decode, samples), document);
        }

        private static PdfStream makeType0(PdfDocument document, PdfArray domain, PdfArray range, PdfArray size, PdfNumber bitsPerSample, PdfNumber order, PdfArray encode, PdfArray decode, byte[] samples) {
            PdfStream stream = new PdfStream(samples).makeIndirect(document);
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

    static public class Type2 extends PdfFunction<PdfDictionary> {

        public Type2(PdfDictionary pdfObject, PdfDocument pdfDocument) {
            super(pdfObject, pdfDocument);
        }

        public Type2(PdfDocument document, PdfArray domain, PdfArray range, PdfNumber n) {
            this(document, domain, range, null, null, n);
        }

        public Type2(PdfDocument document, PdfArray domain, PdfArray range, PdfArray c0, PdfArray c1, PdfNumber n) {
            this(makeType2(domain, range, c0, c1, n), document);
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

    static public class Type3 extends PdfFunction<PdfDictionary> {

        public Type3(PdfDictionary pdfObject, PdfDocument pdfDocument) {
            super(pdfObject, pdfDocument);
        }

        public Type3(PdfDocument document, PdfArray domain, PdfArray range, PdfArray functions, PdfArray bounds, PdfArray encode) {
            this(makeType3(domain, range, functions, bounds, encode), document);
        }

        public Type3(PdfDocument document, PdfArray domain, PdfArray range, List<PdfFunction> functions, PdfArray bounds, PdfArray encode) {
            this(document, domain, range, getFunctionsArray(functions), bounds, encode);
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

    static public class Type4 extends PdfFunction<PdfStream> {

        public Type4(PdfStream pdfObject, PdfDocument pdfDocument) {
            super(pdfObject, pdfDocument);
        }

        public Type4(PdfDocument document, PdfArray domain, PdfArray range, byte[] ps) {
            this(makeType4(document, domain, range, ps), document);
        }

        private static PdfStream makeType4(PdfDocument document, PdfArray domain, PdfArray range, byte[] ps) {
            PdfStream stream = new PdfStream(ps).makeIndirect(document);
            stream.put(PdfName.FunctionType, new PdfNumber(4));
            stream.put(PdfName.Domain, domain);
            stream.put(PdfName.Range, range);
            return stream;
        }
    }

    public static PdfFunction makeFunction(PdfDictionary pdfObject, PdfDocument document) {
        switch (pdfObject.getType()) {
            case 0:
                return new Type0((PdfStream)pdfObject, document);
            case 2:
                return new Type2(pdfObject, document);
            case 3:
                return new Type3(pdfObject, document);
            case 4:
                return new Type4((PdfStream)pdfObject, document);
        }
        return null;
    }

}
