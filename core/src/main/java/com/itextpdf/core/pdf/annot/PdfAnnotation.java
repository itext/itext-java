package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.layer.PdfOCG;

abstract public class PdfAnnotation extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Annotation flags.
     */
    static public final int Invisible = 1;
    static public final int Hidden = 2;
    static public final int Print = 4;
    static public final int NoZoom = 8;
    static public final int NoRotate = 16;
    static public final int NoView = 32;
    static public final int ReadOnly = 64;
    static public final int Locked = 128;
    static public final int ToggleNoView = 256;
    static public final int LockedContents = 512;


    /**
     * Annotation states.
     */
    static public final PdfString Marked = new PdfString("Marked");
    static public final PdfString Unmarked = new PdfString("Unmarked");
    static public final PdfString Accepted = new PdfString("Accepted");
    static public final PdfString Rejected = new PdfString("Rejected");
    static public final PdfString Canceled = new PdfString("Cancelled");
    static public final PdfString Completed = new PdfString("Completed");
    static public final PdfString None = new PdfString("None");

    /**
     * Annotation state models.
     */
    static public final PdfString MarkedModel = new PdfString("Marked");
    static public final PdfString ReviewModel = new PdfString("Review");

    protected PdfPage page;

    public PdfAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        this(new PdfDictionary(), document);
        put(PdfName.Rect, new PdfArray(rect));
        put(PdfName.Subtype, getSubtype());
    }

    public PdfAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    abstract public PdfName getSubtype() throws PdfException;

    /**
     * Sets the layer this annotation belongs to.
     *
     * @param layer the layer this annotation belongs to
     */
    public void setLayer(final PdfOCG layer) throws PdfException {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

    public <T extends PdfAnnotation> T setAction(PdfAction action) {
        return put(PdfName.A, action);
    }

    public <T extends PdfAnnotation> T setAdditionalAction(PdfName key, PdfAction action) throws PdfException {
        PdfAction.setAdditionalAction(this, key, action);
        return (T) this;
    }

    public PdfString getContents() throws PdfException {
        return getPdfObject().getAsString(PdfName.Contents);
    }

    public <T extends PdfAnnotation> T setContents(PdfString contents) {
        return put(PdfName.Contents, contents);
    }

    public <T extends PdfAnnotation> T setContents(String contents) {
        return setContents(new PdfString(contents));
    }

    public PdfDictionary getPageObject() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.P);
    }

    public PdfPage getPage() {
        return page;
    }

    public <T extends PdfAnnotation> T setPage(PdfPage page) {
        this.page = page;
        return put(PdfName.P, page);
    }

    public PdfString getName() throws PdfException {
        return getPdfObject().getAsString(PdfName.NM);
    }

    public <T extends PdfAnnotation> T setName(PdfString name) {
        return put(PdfName.NM, name);
    }

    public PdfString getDate() throws PdfException {
        return getPdfObject().getAsString(PdfName.M);
    }

    public <T extends PdfAnnotation> T setDate(PdfString date) {
        return put(PdfName.M, date);
    }

    public int getFlags() throws PdfException {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.F);
        if (f != null)
            return f.getIntValue();
        else
            return 0;
    }

    public <T extends PdfAnnotation> T setFlags(int flags) {
        return put(PdfName.F, new PdfNumber(flags));
    }

    public <T extends PdfAnnotation> T setFlag(int flag) throws PdfException {
        int flags = getFlags();
        flags = flags | flag;
        return setFlags(flags);
    }

    public <T extends PdfAnnotation> T resetFlag(int flag) throws PdfException {
        int flags = getFlags();
        flags = flags & (~flag & 0xff);
        return setFlags(flags);
    }

    public boolean hasFlag(int flag) throws PdfException {
        int flags = getFlags();
        return (flags & flag) != 0;
    }

    public PdfDictionary getAppearanceDictionary() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.AP);
    }

    public PdfDictionary getAppearanceObject(PdfName appearanceType) throws PdfException {
        PdfDictionary ap = getAppearanceDictionary();
        if (ap != null) {
            return ap.getAsDictionary(appearanceType);
        }
        return null;
    }

    public PdfDictionary getNormalAppearanceObject() throws PdfException {
        return getAppearanceObject(PdfName.N);
    }

    public PdfDictionary getRolloverAppearanceObject() throws PdfException {
        return getAppearanceObject(PdfName.R);
    }

    public PdfDictionary getDownAppearanceObject() throws PdfException {
        return getAppearanceObject(PdfName.D);
    }

    public <T extends PdfAnnotation> T setAppearance(PdfName appearanceType, PdfDictionary appearance) throws PdfException {
        PdfDictionary ap = getAppearanceDictionary();
        if (ap == null) {
            ap = new PdfDictionary();
            getPdfObject().put(PdfName.AP, ap);
        }
        ap.put(appearanceType, appearance);
        return (T) this;
    }

    public <T extends PdfAnnotation> T setNormalAppearance(PdfDictionary appearance) throws PdfException {
        return setAppearance(PdfName.N, appearance);
    }

    public <T extends PdfAnnotation> T setRolloverAppearance(PdfDictionary appearance) throws PdfException {
        return setAppearance(PdfName.R, appearance);
    }

    public <T extends PdfAnnotation> T setDownAppearance(PdfDictionary appearance) throws PdfException {
        return setAppearance(PdfName.D, appearance);
    }

    public <T extends PdfAnnotation> T setAppearance(PdfName appearanceType, PdfAnnotationAppearance appearance) throws PdfException {
        return setAppearance(appearanceType, appearance.getPdfObject());
    }

    public <T extends PdfAnnotation> T setNormalAppearance(PdfAnnotationAppearance appearance) throws PdfException {
        return setAppearance(PdfName.N, appearance);
    }

    public <T extends PdfAnnotation> T setRolloverAppearance(PdfAnnotationAppearance appearance) throws PdfException {
        return setAppearance(PdfName.R, appearance);
    }

    public <T extends PdfAnnotation> T setDownAppearance(PdfAnnotationAppearance appearance) throws PdfException {
        return setAppearance(PdfName.D, appearance);
    }

    public PdfName getAppearanceState() throws PdfException {
        return getPdfObject().getAsName(PdfName.AS);
    }

    public <T extends PdfAnnotation> T setAppearanceState(PdfName as) {
        return put(PdfName.AS, as);
    }

    public PdfArray getBorder() throws PdfException {
        return getPdfObject().getAsArray(PdfName.Border);
    }

    public <T extends PdfAnnotation> T setBorder(PdfArray border) {
        return put(PdfName.Border, border);
    }

    public PdfArray getColorObject() throws PdfException {
        return getPdfObject().getAsArray(PdfName.C);
    }

    public <T extends PdfAnnotation> T setColor(PdfArray color) {
        return put(PdfName.C, color);
    }

    public <T extends PdfAnnotation> T setColor(float[] color) {
        return setColor(new PdfArray(color));
    }

    public int getStructParentIndex() throws PdfException {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.StructParent);
        if (n == null)
            return -1;
        else
            return n.getIntValue();
    }

    public <T extends PdfAnnotation> T setStructParentIndex(int structParentIndex) {
        return put(PdfName.StructParent, new PdfNumber(structParentIndex));
    }

    public PdfArray getQuadPoints() throws PdfException {
        return getPdfObject().getAsArray(PdfName.QuadPoints);
    }

    public boolean getOpen() throws PdfException {
        PdfBoolean open = getPdfObject().getAsBoolean(PdfName.Open);
        return open == null ? false : open.getValue();
    }

    public <T extends PdfAnnotation> T setOpen(boolean open) {
        return put(PdfName.Open, new PdfBoolean(open));
    }

    public <T extends PdfAnnotation> T setQuadPoints(PdfArray quadPoints) {
        return put(PdfName.QuadPoints, quadPoints);
    }

    public <T extends PdfAnnotation> T setBorderStyle(PdfDictionary borderStyle) {
        return put(PdfName.BS, borderStyle);
    }

    public PdfDictionary getBorderStyle() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.BS);
    }

    /**
     * Marks annotation to be tagged.
     * Normally it shall be done for link annotations.
     *
     * @param <T>
     * @return annotation itself.
     */
    public <T extends PdfAnnotation> T tag() {
        return put(PdfName.StructParent, new PdfNumber(getDocument().getNextStructParentIndex()));
    }

    static public <T extends PdfAnnotation> T makeAnnotation(PdfObject pdfObject, PdfDocument document) throws PdfException {
        return makeAnnotation(pdfObject, document, null);
    }

    public <T extends PdfAnnotation> T setTitle(PdfString title) {
        return put(PdfName.T, title);
    }

    public PdfString getTitle() throws PdfException {
        return getPdfObject().getAsString(PdfName.T);
    }

    public <T extends PdfAnnotation> T setAppearanceCharacteristics(PdfDictionary characteristics) {
        return put(PdfName.MK, characteristics);
    }

    public PdfDictionary getAppearanceCharacteristics() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.MK);
    }

    public PdfDictionary getAction() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.A);
    }

    public PdfDictionary getAdditionalAction() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }


    static public <T extends PdfAnnotation> T makeAnnotation(PdfObject pdfObject, PdfDocument document, PdfAnnotation parent) throws PdfException {
        T annotation = null;
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName subtype = dictionary.getAsName(PdfName.Subtype);
            if (PdfName.Link.equals(subtype))
                annotation = (T) new PdfLinkAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Popup.equals(subtype))
                annotation = (T) new PdfPopupAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Widget.equals(subtype))
                throw new UnsupportedOperationException();
            else if (PdfName.Screen.equals(subtype))
                annotation = (T) new PdfScreenAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName._3D.equals(subtype))
                throw new UnsupportedOperationException();
            else if (PdfName.Highlight.equals(subtype) || PdfName.Underline.equals(subtype) || PdfName.Squiggly.equals(subtype) || PdfName.StrikeOut.equals(subtype))
                annotation = (T) new PdfTextMarkupAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Caret.equals(subtype))
                annotation = (T) new PdfCaretAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Text.equals(subtype))
                annotation = (T) new PdfTextAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Sound.equals(subtype))
                annotation = (T) new PdfSoundAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Stamp.equals(subtype))
                annotation = (T) new PdfStampAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.FileAttachment.equals(subtype))
                annotation = (T) new PdfFileAttachmentAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Ink.equals(subtype))
                annotation = (T) new PdfInkAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.PrinterMark.equals(subtype))
                annotation = (T) new PdfPrinterMarkAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.TrapNet.equals(subtype))
                annotation = (T) new PdfTrapNetworkAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.FreeText.equals(subtype))
                annotation = (T) new PdfFreeTextAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Square.equals(subtype))
                annotation = (T) new PdfSquareAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Circle.equals(subtype))
                annotation = (T) new PdfCircleAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Line.equals(subtype))
                annotation = (T) new PdfLineAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Polygon.equals(subtype) || PdfName.PolyLine.equals(subtype))
                annotation = (T) new PdfPolyGeomAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Redact.equals(subtype))
                annotation = (T) new PdfRedactAnnotation((PdfDictionary) pdfObject, document);
            else if (PdfName.Watermark.equals(subtype))
                annotation = (T) new PdfWatermarkAnnotation((PdfDictionary) pdfObject, document);
        }
        if (annotation instanceof PdfMarkupAnnotation) {
            PdfMarkupAnnotation markup = (PdfMarkupAnnotation) annotation;
            PdfDictionary inReplyTo = markup.getInReplyToObject();
            if (inReplyTo != null)
                markup.setInReplyTo(makeAnnotation(inReplyTo, document));
            PdfDictionary popup = markup.getPopupObject();
            if (popup != null)
                markup.setPopup((PdfPopupAnnotation) makeAnnotation(popup, document, markup));
        }
        if (annotation instanceof PdfPopupAnnotation) {
            PdfPopupAnnotation popup = (PdfPopupAnnotation) annotation;
            if (parent != null)
                popup.setParent(parent);
        }

        return annotation;
    }


}
