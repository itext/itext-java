package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

public final class PdfFunctionUtil {
    private PdfFunctionUtil() {
        // do nothing
    }

    public static PdfDictionary createMinimalPdfType0FunctionDict() {
        PdfDictionary type0Func = new PdfDictionary();
        type0Func.put(PdfName.FunctionType, new PdfNumber(0));

        PdfArray domain = new PdfArray(new int[] {0, 1, 0, 1});
        type0Func.put(PdfName.Domain, domain);

        type0Func.put(PdfName.Size, new PdfArray(new double[] {2, 2}));

        return type0Func;
    }

    public static PdfDictionary createMinimalPdfType2FunctionDict() {
        PdfDictionary type2Func = new PdfDictionary();
        type2Func.put(PdfName.FunctionType, new PdfNumber(2));

        PdfArray domain = new PdfArray(new int[] {0, 1});
        type2Func.put(PdfName.Domain, domain);

        type2Func.put(PdfName.N, new PdfNumber(2));

        return type2Func;
    }
}
