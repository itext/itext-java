package com.itextpdf.core.color;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.colorspace.PdfCieBasedCs;

import java.io.InputStream;

public class IccBased extends Color {

    public IccBased(PdfCieBasedCs.IccBased cs, float[] value) {
        super(cs, value);
    }

    /**
     * Creates IccBased color.
     *
     * @param document
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     * @throws PdfException
     */
    public IccBased(PdfDocument document, InputStream iccStream) {
        this(new PdfCieBasedCs.IccBased(document, iccStream), null);
        colorValue = new float[getNumberOfComponents()];
        for (int i = 0; i < getNumberOfComponents(); i++)
            colorValue[i] = 0f;
    }

    /**
     * Creates IccBased color.
     *
     * @param document
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     * @param value     color value.
     * @throws PdfException
     */
    public IccBased(PdfDocument document, InputStream iccStream, float[] value) {
        this(new PdfCieBasedCs.IccBased(document, iccStream), value);
    }

    public IccBased(PdfDocument document, InputStream iccStream, float[] range, float[] value) {
        this(new PdfCieBasedCs.IccBased(document, iccStream, range), value);
        if (getNumberOfComponents() * 2 != range.length)
            throw new PdfException(PdfException.InvalidRangeArray, this);
    }

}
