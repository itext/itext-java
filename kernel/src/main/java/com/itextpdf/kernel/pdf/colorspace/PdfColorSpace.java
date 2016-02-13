package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

abstract public class PdfColorSpace<T extends PdfObject> extends PdfObjectWrapper<T> {

    public PdfColorSpace(T pdfObject) {
        super(pdfObject);
    }

    abstract public int getNumberOfComponents();

    static public PdfColorSpace makeColorSpace(PdfObject pdfObject) {
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (PdfName.DeviceGray.equals(pdfObject))
            return new PdfDeviceCs.Gray();
        else if (PdfName.DeviceRGB.equals(pdfObject))
            return new PdfDeviceCs.Rgb();
        else if (PdfName.DeviceCMYK.equals(pdfObject))
            return new PdfDeviceCs.Cmyk();
        else if (PdfName.Pattern.equals(pdfObject))
            return new PdfSpecialCs.Pattern();
        else if (pdfObject.isArray()) {
            PdfArray array = (PdfArray) pdfObject;
            PdfName csType = array.getAsName(0);
            if (PdfName.CalGray.equals(csType))
                return new PdfCieBasedCs.CalGray(array);
            else if (PdfName.CalRGB.equals(csType))
                return new PdfCieBasedCs.CalRgb(array);
            else if (PdfName.Lab.equals(csType))
                return new PdfCieBasedCs.Lab(array);
            else if (PdfName.ICCBased.equals(csType))
                return new PdfCieBasedCs.IccBased(array);
            else if (PdfName.Indexed.equals(csType))
                return new PdfSpecialCs.Indexed(array);
            else if (PdfName.Separation.equals(csType))
                return new PdfSpecialCs.Separation(array);
            else if (PdfName.DeviceN.equals(csType))
                return array.size() == 4 ? new PdfSpecialCs.DeviceN(array) : new PdfSpecialCs.NChannel(array);
            else if (PdfName.Pattern.equals(csType))
                return new PdfSpecialCs.UncoloredTilingPattern(array);
        }
        return null;
    }

}
