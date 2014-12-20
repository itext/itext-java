package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

abstract public class PdfColorSpace<T extends PdfObject> extends PdfObjectWrapper {

    public PdfColorSpace(T pdfObject) {
        super(pdfObject);
    }

    public PdfColorSpace(T pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    abstract public int getNumOfComponents() throws PdfException;

    static public PdfColorSpace makeColorSpace(PdfObject pdfObject, PdfDocument document) throws PdfException {
        if (pdfObject instanceof PdfIndirectReference)
            pdfObject = ((PdfIndirectReference)pdfObject).getRefersTo();
        if (PdfName.DeviceGray.equals(pdfObject))
            return new PdfDeviceCs.Gray(document);
        else if (PdfName.DeviceRGB.equals(pdfObject))
            return new PdfDeviceCs.Rgb(document);
        else if (PdfName.DeviceCMYK.equals(pdfObject))
            return new PdfDeviceCs.Cmyk(document);
        return null;
    }

}
