package com.itextpdf.core.pdf.xobject;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfFormXObject extends PdfXObject {

    private PdfResources resources = null;

    public PdfFormXObject(PdfDocument document, Rectangle bBox) throws PdfException {
        super(new PdfStream(document), document);
        getPdfObject().put(PdfName.Type, PdfName.XObject);
        getPdfObject().put(PdfName.Subtype, PdfName.Form);
        getPdfObject().put(PdfName.BBox, new PdfArray(bBox));
    }

    public PdfFormXObject(PdfStream pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    /**
     * Creates form XObject from page content.
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) throws PdfException {
        this(page.getDocument(), page.getCropBox());
        getPdfObject().getOutputStream().writeBytes(page.getContentBytes());
        resources = new PdfResources((PdfDictionary)page.getResources().getPdfObject().copy());
        getPdfObject().put(PdfName.Resources, resources.getPdfObject());

    }

    public PdfResources getResources() throws PdfException {
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
    public PdfFormXObject copy(PdfDocument document) throws PdfException {
        return new PdfFormXObject((PdfStream)getPdfObject().copy(document), document);
    }

    @Override
    public void flush() throws PdfException {
        resources = null;
        super.flush();
    }

    //Additional entries in form dictionary for Trap Network annotation
    public PdfFormXObject setProcessColorModel(PdfName model){
        return put(PdfName.PCM, model);
    }

    public PdfName getProcessColorModel() throws PdfException {
        return getPdfObject().getAsName(PdfName.PCM);
    }

    public PdfFormXObject setSeparationColorNames(PdfArray colorNames){
        return put(PdfName.SeparationColorNames, colorNames);
    }

    public PdfArray getSeparationColorNames() throws PdfException {
        return getPdfObject().getAsArray(PdfName.SeparationColorNames);
    }

    public PdfFormXObject setTrapRegions(PdfArray regions){
        return put(PdfName.TrapRegions, regions);
    }

    public PdfArray getTrapRegions() throws PdfException {
        return getPdfObject().getAsArray(PdfName.TrapRegions);
    }

    public PdfFormXObject setTrapStyles(PdfString trapStyles){
        return put(PdfName.TrapStyles, trapStyles);
    }

    public PdfString getTrapStyles() throws PdfException {
        return getPdfObject().getAsString(PdfName.TrapStyles);
    }

    //Additional entries in form dictionary for Printer Mark annotation
    public PdfFormXObject setMarkStyle(PdfString markStyle) {
        return put(PdfName.MarkStyle, markStyle);
    }

    public PdfString getMarkStyle() throws PdfException {
        return getPdfObject().getAsString(PdfName.MarkStyle);
    }
}
