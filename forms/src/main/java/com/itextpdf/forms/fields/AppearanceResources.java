package com.itextpdf.forms.fields;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfResources;

import java.util.HashMap;
import java.util.Map;

/**
 * AppearanceResources allows to register font names that will be used as resource name.
 * Preserving existed font names in default resources of AcroForm is the only goal of this class.
 * <p>
 * Shall be used only in {@link PdfFormField}.
 *
 * @see AppearanceXObject
 */
class AppearanceResources extends PdfResources {

    private static final long serialVersionUID = -1991503804376023468L;

    private Map<PdfIndirectReference, PdfName> drFonts = new HashMap<>();

    AppearanceResources(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    void addFontFromDefaultResources(PdfName name, PdfFont font) {
        if (name != null && font != null && font.getPdfObject().getIndirectReference() != null) {
            //So, most likely it's a document PdfFont
            drFonts.put(font.getPdfObject().getIndirectReference(), name);
        }
    }

    @Override
    public PdfName addFont(PdfDocument pdfDocument, PdfFont font) {
        PdfName fontName = null;
        if (font != null && font.getPdfObject().getIndirectReference() != null) {
            fontName = drFonts.get(font.getPdfObject().getIndirectReference());
        }

        if (fontName != null) {
            addResource(font.getPdfObject(), PdfName.Font, fontName);
            return fontName;
        } else {
            return super.addFont(pdfDocument, font);
        }
    }
}
