package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;

abstract public class PdfColorSpace<T extends PdfObject> extends PdfObjectWrapper<T> {

    public PdfColorSpace(T pdfObject) {
        super(pdfObject);
    }

    abstract public int getNumOfComponents();

    static public PdfColorSpace makeColorSpace(PdfObject pdfObject, PdfDocument document) {
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (PdfName.DeviceGray.equals(pdfObject))
            return new PdfDeviceCs.Gray().makeIndirect(document);
        else if (PdfName.DeviceRGB.equals(pdfObject))
            return new PdfDeviceCs.Rgb().makeIndirect(document); //TODO indirect PdfName?
        else if (PdfName.DeviceCMYK.equals(pdfObject))
            return new PdfDeviceCs.Cmyk().makeIndirect(document);
        else if (PdfName.Pattern.equals(pdfObject))
            return new PdfSpecialCs.Pattern().makeIndirect(document);
        else if (pdfObject.isArray()) {
            PdfArray array = (PdfArray) pdfObject;
            PdfName csType = array.getAsName(0);
            if (PdfName.CalGray.equals(csType))
                return new PdfCieBasedCs.CalGray(array, document);
            else if (PdfName.CalRGB.equals(csType))
                return new PdfCieBasedCs.CalRgb(array, document);
            else if (PdfName.Lab.equals(csType))
                return new PdfCieBasedCs.Lab(array, document);
            else if (PdfName.ICCBased.equals(csType))
                return new PdfCieBasedCs.IccBased(array, document);
            else if (PdfName.Indexed.equals(csType))
                return new PdfSpecialCs.Indexed(array, document);
            else if (PdfName.Separation.equals(csType))
                return new PdfSpecialCs.Separation(array, document);
            else if (PdfName.DeviceN.equals(csType))
                return array.size() == 4 ? new PdfSpecialCs.DeviceN(array, document) : new PdfSpecialCs.NChannel(array, document);
            else if (PdfName.Pattern.equals(csType))
                return new PdfSpecialCs.UncoloredTilingPattern(array, document);
        }
        return null;
    }

}
