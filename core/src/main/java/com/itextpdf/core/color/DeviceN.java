package com.itextpdf.core.color;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.core.pdf.function.PdfFunction;

import java.util.List;

public class DeviceN extends Color {

    public DeviceN(PdfSpecialCs.DeviceN cs, float[] value) {
        super(cs, value);
    }

    public DeviceN(PdfDocument document, List<String> names, PdfColorSpace alternateCs, PdfFunction tintTransform, float[] value) {
        this(new PdfSpecialCs.DeviceN(document, names, alternateCs, tintTransform), value);
    }

}
