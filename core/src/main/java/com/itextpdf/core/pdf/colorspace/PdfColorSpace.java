package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.sun.corba.se.spi.orb.ParserDataFactory;

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
        else if (pdfObject instanceof PdfArray) {
            PdfArray array = (PdfArray)pdfObject;
            PdfName csType = array.getAsName(0);
            if (PdfName.CalGray.equals(csType))
                return new PdfCieBasedCs.CalGray(array, document);
            else if (PdfName.CalRGB.equals(csType))
                return new PdfCieBasedCs.CalRgb(array, document);
            else if (PdfName.Lab.equals(csType))
                return new PdfCieBasedCs.Lab(array, document);
            else if (PdfName.ICCBased.equals(csType))
                return new PdfCieBasedCs.IccBased(array, document);
        }
        return null;
    }

}
