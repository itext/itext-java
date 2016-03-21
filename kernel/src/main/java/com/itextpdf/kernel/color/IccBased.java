package com.itextpdf.kernel.color;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

import java.io.InputStream;

public class IccBased extends Color {

    public IccBased(PdfCieBasedCs.IccBased cs, float[] value) {
        super(cs, value);
    }

    /**
     * Creates IccBased color.
     *
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     * @throws PdfException
     */
    public IccBased(InputStream iccStream) {
        this(new PdfCieBasedCs.IccBased(iccStream), null);
        colorValue = new float[getNumberOfComponents()];
        for (int i = 0; i < getNumberOfComponents(); i++)
            colorValue[i] = 0f;
    }

    /**
     * Creates IccBased color.
     *
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     * @param value     color value.
     * @throws PdfException
     */
    public IccBased(InputStream iccStream, float[] value) {
        this(new PdfCieBasedCs.IccBased(iccStream), value);
    }

    public IccBased(InputStream iccStream, float[] range, float[] value) {
        this(new PdfCieBasedCs.IccBased(iccStream, range), value);
        if (getNumberOfComponents() * 2 != range.length)
            throw new PdfException(PdfException.InvalidRangeArray, this);
    }

}
