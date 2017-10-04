package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;

class InteriorColorUtil {

    private InteriorColorUtil() {
    }

    /**
     * The interior color which is used to fill areas specific for different types of annotation. For {@link PdfLineAnnotation}
     * and polyline annotation ({@link PdfPolyGeomAnnotation} - the annotation's line endings, for {@link PdfSquareAnnotation}
     * and {@link PdfCircleAnnotation} - the annotation's rectangle or ellipse, for {@link PdfRedactAnnotation} - the redacted
     * region after the affected content has been removed.
     * @return {@link Color} of either {@link DeviceGray}, {@link DeviceRgb} or {@link DeviceCmyk} type which defines
     * interior color of the annotation, or null if interior color is not specified.
     */
    public static Color parseInteriorColor(PdfArray color) {
        if (color == null) {
            return null;
        }
        switch (color.size()) {
            case 1:
                return new DeviceGray(color.getAsNumber(0).floatValue());
            case 3:
                return new DeviceRgb(color.getAsNumber(0).floatValue(), color.getAsNumber(1).floatValue(), color.getAsNumber(2).floatValue());
            case 4:
                return new DeviceCmyk(color.getAsNumber(0).floatValue(), color.getAsNumber(1).floatValue(), color.getAsNumber(2).floatValue(), color.getAsNumber(3).floatValue());
            default:
                return null;
        }
    }
}
