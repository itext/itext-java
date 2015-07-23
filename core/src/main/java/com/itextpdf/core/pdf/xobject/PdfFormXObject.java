package com.itextpdf.core.pdf.xobject;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfFormXObject extends PdfXObject {

    private PdfResources resources = null;

    public PdfFormXObject(Rectangle bBox) {
        super(new PdfStream());
        getPdfObject().put(PdfName.Type, PdfName.XObject);
        getPdfObject().put(PdfName.Subtype, PdfName.Form);
        if (bBox != null) {
            getPdfObject().put(PdfName.BBox, new PdfArray(bBox));
        }
    }

    public PdfFormXObject(PdfStream pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates form XObject from page content.
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getCropBox());
        getPdfObject().getOutputStream().writeBytes(page.getContentBytes());
        resources = new PdfResources((PdfDictionary)page.getResources().getPdfObject().copy(page.getDocument()));
        getPdfObject().put(PdfName.Resources, resources.getPdfObject());

    }

    public PdfResources getResources() {
        if (this.resources == null) {
            PdfDictionary resources = getPdfObject().getAsDictionary(PdfName.Resources);
            if (resources == null) {
                resources = new PdfDictionary();
                getPdfObject().put(PdfName.Resources, resources);
            }
            this.resources = new PdfResources(resources);
        }
        return resources;
    }

    @Override
    public PdfFormXObject copy(PdfDocument document) {
        return new PdfFormXObject((PdfStream)getPdfObject().copy(document));
    }

    @Override
    public void flush() {
        resources = null;
        if (getPdfObject().get(PdfName.BBox) == null) {
            throw new PdfException(PdfException.FormXObjectMustHaveBbox);
        }
        super.flush();
    }

    //Additional entries in form dictionary for Trap Network annotation
    public PdfFormXObject setProcessColorModel(PdfName model){
        return put(PdfName.PCM, model);
    }

    public PdfName getProcessColorModel() {
        return getPdfObject().getAsName(PdfName.PCM);
    }

    public PdfFormXObject setSeparationColorNames(PdfArray colorNames){
        return put(PdfName.SeparationColorNames, colorNames);
    }

    public PdfArray getSeparationColorNames() {
        return getPdfObject().getAsArray(PdfName.SeparationColorNames);
    }

    public PdfFormXObject setTrapRegions(PdfArray regions){
        return put(PdfName.TrapRegions, regions);
    }

    public PdfArray getTrapRegions() {
        return getPdfObject().getAsArray(PdfName.TrapRegions);
    }

    public PdfFormXObject setTrapStyles(PdfString trapStyles){
        return put(PdfName.TrapStyles, trapStyles);
    }

    public PdfString getTrapStyles() {
        return getPdfObject().getAsString(PdfName.TrapStyles);
    }

    //Additional entries in form dictionary for Printer Mark annotation
    public PdfFormXObject setMarkStyle(PdfString markStyle) {
        return put(PdfName.MarkStyle, markStyle);
    }

    public PdfString getMarkStyle() {
        return getPdfObject().getAsString(PdfName.MarkStyle);
    }

    public PdfArray getBBox() {
        return getPdfObject().getAsArray(PdfName.BBox);
    }

    public void setBBox(PdfArray bBox) {
        getPdfObject().put(PdfName.BBox, bBox);
    }
}
