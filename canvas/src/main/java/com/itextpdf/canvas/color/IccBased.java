package com.itextpdf.canvas.color;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.core.pdf.*;
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
        this(new PdfCieBasedCs.IccBased(document, getIccProfileStream(iccStream)), null);
        colorValue = new float[getNumOfComponents()];
        for (int i = 0; i < getNumOfComponents(); i++)
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
        this(new PdfCieBasedCs.IccBased(document, getIccProfileStream(iccStream)), value);
    }

    public IccBased(PdfDocument document, InputStream iccStream, float[] range, float[] value) {
        this(new PdfCieBasedCs.IccBased(document, getIccProfileStream(iccStream, range)), value);
        if (getNumOfComponents() * 2 != range.length)
            throw new PdfException(PdfException.InvalidRangeArray, this);
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
