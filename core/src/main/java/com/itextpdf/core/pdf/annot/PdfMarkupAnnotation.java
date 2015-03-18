package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

abstract public class PdfMarkupAnnotation extends PdfAnnotation {

    protected PdfAnnotation inReplyTo = null;
    protected PdfPopupAnnotation popup = null;

    public PdfMarkupAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfMarkupAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    public PdfString getText() throws PdfException {
        return getPdfObject().getAsString(PdfName.T);
    }

    public <T extends PdfMarkupAnnotation> T setText(PdfString text) {
        return put(PdfName.T, text);
    }

    public PdfNumber getOpacity() throws PdfException {
        return getPdfObject().getAsNumber(PdfName.CA);
    }

    public <T extends PdfMarkupAnnotation> T setOpacity(PdfNumber ca) {
        return put(PdfName.CA, ca);
    }

    public PdfObject getRichText() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.RC);
    }

    public <T extends PdfMarkupAnnotation> T setRichText(PdfObject richText) {
        return put(PdfName.RC, richText);
    }

    public PdfString getCreationDate() throws PdfException {
        return getPdfObject().getAsString(PdfName.CreationDate);
    }

    public <T extends PdfMarkupAnnotation> T setCreationDate(PdfString creationDate) {
        return put(PdfName.CreationDate, creationDate);
    }

    public PdfDictionary getInReplyToObject() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.IRT);
    }

    public PdfAnnotation getInReplyTo() {
        return inReplyTo;
    }

    public <T extends PdfMarkupAnnotation> T setInReplyTo(PdfAnnotation inReplyTo) {
        this.inReplyTo = inReplyTo;
        return put(PdfName.IRT, inReplyTo);
    }

    public <T extends PdfMarkupAnnotation> T setPopup(PdfPopupAnnotation popup) {
        this.popup = popup;
        return put(PdfName.Popup, popup);
    }

    public PdfDictionary getPopupObject() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.Popup);
    }

    public PdfPopupAnnotation getPopup() {
        return popup;
    }

    public PdfString getSubject() throws PdfException {
        return getPdfObject().getAsString(PdfName.Subj);
    }

    public <T extends PdfMarkupAnnotation> T setSubject(PdfString subject) {
        return put(PdfName.Subj, subject);
    }

    public PdfName getReplyType() throws PdfException {
        return getPdfObject().getAsName(PdfName.RT);
    }

    public <T extends PdfMarkupAnnotation> T setReplyType(PdfName replyType) {
        return put(PdfName.RT, replyType);
    }

    public PdfName getIntent() throws PdfException {
        return getPdfObject().getAsName(PdfName.IT);
    }

    public <T extends PdfMarkupAnnotation> T setIntent(PdfName intent) {
        return put(PdfName.IT, intent);
    }

    public PdfDictionary getExternalData() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.ExData);
    }

    public <T extends PdfMarkupAnnotation> T setExternalData(PdfName exData) {
        return put(PdfName.ExData, exData);
    }

    public <T extends PdfMarkupAnnotation> T setRectangleDifferences(PdfArray rect) {
        return put(PdfName.RD, rect);
    }

    public PdfArray getRectangleDifferences() throws PdfException {
        return getPdfObject().getAsArray(PdfName.RD);
    }

    public PdfDictionary getBorderEffect() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.BE);
    }

    public <T extends PdfMarkupAnnotation> T setBorderEffect(PdfDictionary borderEffect) {
        return put(PdfName.BE, borderEffect);
    }

    public PdfArray getInteriorColor() throws PdfException {
        return getPdfObject().getAsArray(PdfName.IC);
    }

    public <T extends PdfMarkupAnnotation> T setInteriorColor(PdfArray interiorColor) {
        return put(PdfName.IC, interiorColor);
    }

    public <T extends PdfMarkupAnnotation> T setInteriorColor(float[] interiorColor) {
        return setInteriorColor(new PdfArray(interiorColor));
    }


    public PdfName getIconName() throws PdfException {
        return getPdfObject().getAsName(PdfName.Name);
    }

    public <T extends PdfMarkupAnnotation> T setIconName(PdfName name) {
        return put(PdfName.Name, name);
    }

    public <T extends PdfMarkupAnnotation> T setDrawnAfter(PdfString appearanceString) {
        return put(PdfName.DA, appearanceString);
    }

    public PdfString getDrawnAfter() throws PdfException {
        return getPdfObject().getAsString(PdfName.DA);
    }

    public int getJustification() throws PdfException {
        PdfNumber q = getPdfObject().getAsNumber(PdfName.Q);
        return q == null ? 0 : q.getIntValue();
    }

    public <T extends PdfMarkupAnnotation> T setJustification(int justification) {
        return put(PdfName.Q, new PdfNumber(justification));
    }
}
