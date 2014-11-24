package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.fonts.PdfFont;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PdfResources extends PdfObjectWrapper<PdfDictionary> {

    private static final String F = "F";
    private static final String Im = "Im";
    private static final String Fm = "Fm";

    private HashMap<PdfObjectWrapper, PdfName> resources = new LinkedHashMap<PdfObjectWrapper, PdfName>();

    /**
     * The font value counter for the fonts in the document.
     */
    private ResourceNumber fontNumber = new ResourceNumber();
    private ResourceNumber imageNumber = new ResourceNumber();
    private ResourceNumber formNumber = new ResourceNumber();

    public PdfResources(PdfDictionary pdfObject) {
        super(pdfObject);
        buildResources(pdfObject);
    }

    public PdfResources() {
        this(new PdfDictionary());
    }

    public PdfName addFont(PdfFont font) throws PdfException {
        return addResource(font, PdfName.Font, F, fontNumber);
    }

    public PdfName addImage(PdfImageXObject image) throws PdfException {
        return addResource(image, PdfName.XObject, Im, imageNumber);
    }

    public PdfName addForm(PdfFormXObject form) throws PdfException {
        return addResource(form, PdfName.XObject, Fm, formNumber);
    }

    public PdfName getResourceName(PdfObjectWrapper resource) {
        return resources.get(resource);
    }

    protected PdfName addResource(PdfObjectWrapper resource, PdfName resType, String resPrefix, ResourceNumber resNumber) throws PdfException {
        PdfName resName = resources.get(resource);
        if (resName == null) {
            resName = new PdfName(resPrefix + resNumber.increment());
            resources.put(resource, resName);
            PdfDictionary resDictionary = (PdfDictionary) pdfObject.get(resType);
            if (resDictionary == null) {
                pdfObject.put(resType, resDictionary = new PdfDictionary());
            }
            resDictionary.put(resName, resource.getPdfObject());
        }
        return resName;
    }

    protected void buildResources(PdfDictionary dictionary) {
        //TODO: Implement populating PdfResources internals from PdfDictionary.
    }

    static private class ResourceNumber {
        private int value;

        public ResourceNumber(int value) {
            this.value = value;
        }

        public ResourceNumber() {
            this(0);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int increment() {
            return ++value;
        }
    }

}
