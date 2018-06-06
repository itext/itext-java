package com.itextpdf.forms.fields;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * AppearanceXObject allows font names registration. Those names will be used as resource name
 * for a particular {@link PdfFont}.
 * <p>
 * Preserving existed font names in default resources of AcroForm is the only goal of this class.
 * <p>
 * Shall be used only in {@link PdfFormField}.
 */
class AppearanceXObject extends PdfFormXObject {

    private static final long serialVersionUID = 6098843657444897565L;

    AppearanceXObject(PdfStream pdfStream) {
        super(pdfStream);
    }

    AppearanceXObject(Rectangle bBox) {
        super(bBox);
    }

    void addFontFromDR(PdfName fontName, PdfFont font) {
        if (fontName != null && font != null) {
            ((AppearanceResources) getResources()).addFontFromDefaultResources(fontName, font);
        }
    }

    @Override
    public PdfResources getResources() {
        if (this.resources == null) {
            PdfDictionary resourcesDict = getPdfObject().getAsDictionary(PdfName.Resources);
            if (resourcesDict == null) {
                resourcesDict = new PdfDictionary();
                getPdfObject().put(PdfName.Resources, resourcesDict);
            }
            this.resources = new AppearanceResources(resourcesDict);
        }
        return resources;
    }
}
