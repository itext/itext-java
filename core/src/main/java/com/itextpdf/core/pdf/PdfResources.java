package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.fonts.PdfFont;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PdfResources extends PdfObjectWrapper<PdfDictionary> {

    private static final String F = "F";
    private static final String Im = "Im";

    /**
     * Fonts of the resources dictionary.
     */
    private HashMap<PdfFont, PdfName> fonts = new LinkedHashMap<PdfFont, PdfName>();

    /**
     * Images of the resources dictionary.
     */
    private HashMap<PdfImageXObject, PdfName> images = new LinkedHashMap<PdfImageXObject, PdfName>();


    /**
     * The font number counter for the fonts in the document.
     */
    private int fontNumber = 1;
    private int imageNumber = 1;

    public PdfResources(PdfDictionary pdfObject) {
        super(pdfObject);
        buildResources(pdfObject);
    }

    public PdfResources() {
        this(new PdfDictionary());
    }

    public PdfName addFont(PdfFont font) throws PdfException {
        PdfName fontName = fonts.get(font);
        if (fontName == null) {
            fontName = new PdfName(F + fontNumber++);
            fonts.put(font, fontName);
            PdfDictionary fontDictionary = (PdfDictionary)pdfObject.get(PdfName.Font);
            if (fontDictionary == null) {
                pdfObject.put(PdfName.Font, fontDictionary = new PdfDictionary());
            }
            fontDictionary.put(fontName, font.getPdfObject());
        }
        return fontName;
    }

    public PdfName addImage(PdfImageXObject image) throws PdfException {
        PdfName imageName = images.get(image);
        if (imageName == null) {
            imageName = new PdfName(Im + imageNumber++);
            images.put(image, imageName);
            PdfDictionary xObjDictionary = (PdfDictionary)pdfObject.get(PdfName.XObject);
            if (xObjDictionary == null) {
                pdfObject.put(PdfName.XObject, xObjDictionary = new PdfDictionary());
            }
            xObjDictionary.put(imageName, image.getPdfObject());
        }
        return imageName;
    }

    protected void buildResources(PdfDictionary dictionary) {
        //TODO: Implement populating PdfResources internals from PdfDictionary.
    }

}
