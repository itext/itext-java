package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImage;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageHelper;

public class PdfFormXObject extends PdfXObject {

    private static final long serialVersionUID = 467500482711722178L;
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
     * The page shall be from the document, to which FormXObject will be added.
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getCropBox());
        getPdfObject().getOutputStream().writeBytes(page.getContentBytes());
        resources = new PdfResources((PdfDictionary)page.getResources().getPdfObject().clone());
        getPdfObject().put(PdfName.Resources, resources.getPdfObject());
    }

    /**
     * Creates a form XObject from {@link WmfImage}.
     * Unlike other images, {@link WmfImage} images are represented as {@link PdfFormXObject}, not as
     * {@link PdfImageXObject}.
     * @param image image to create form object from
     * @param pdfDocument document instance which is needed for writing form stream contents
     */
    public PdfFormXObject(WmfImage image, PdfDocument pdfDocument) {
        this(new WmfImageHelper(image).createPdfForm(pdfDocument).getPdfObject());
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

    public PdfFormXObject setBBox(PdfArray bBox) {
        return put(PdfName.BBox, bBox);
    }

    public PdfFormXObject setGroup(PdfTransparencyGroup transparency) {
        return put(PdfName.Group, transparency.getPdfObject());
    }

    @Override
    public Float getWidth() { return getBBox() == null ? null : getBBox().getAsFloat(2);}

    @Override
    public Float getHeight() { return getBBox() == null ? null : getBBox().getAsFloat(3); }
}
