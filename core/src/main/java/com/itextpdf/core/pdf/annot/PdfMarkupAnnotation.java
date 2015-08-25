package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.*;

abstract public class PdfMarkupAnnotation extends PdfAnnotation {

    protected PdfAnnotation inReplyTo = null;
    protected PdfPopupAnnotation popup = null;

    public PdfMarkupAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfMarkupAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    public PdfString getText() {
        return getPdfObject().getAsString(PdfName.T);
    }

    public <T extends PdfMarkupAnnotation> T setText(PdfString text) {
        return put(PdfName.T, text);
    }

    public PdfNumber getOpacity() {
        return getPdfObject().getAsNumber(PdfName.CA);
    }

    public <T extends PdfMarkupAnnotation> T setOpacity(PdfNumber ca) {
        return put(PdfName.CA, ca);
    }

    public PdfObject getRichText() {
        return getPdfObject().getAsDictionary(PdfName.RC);
    }

    public <T extends PdfMarkupAnnotation> T setRichText(PdfObject richText) {
        return put(PdfName.RC, richText);
    }

    public PdfString getCreationDate() {
        return getPdfObject().getAsString(PdfName.CreationDate);
    }

    public <T extends PdfMarkupAnnotation> T setCreationDate(PdfString creationDate) {
        return put(PdfName.CreationDate, creationDate);
    }

    public PdfDictionary getInReplyToObject() {
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

    public PdfDictionary getPopupObject() {
        return getPdfObject().getAsDictionary(PdfName.Popup);
    }

    public PdfPopupAnnotation getPopup() {
        return popup;
    }

    public PdfString getSubject() {
        return getPdfObject().getAsString(PdfName.Subj);
    }

    public <T extends PdfMarkupAnnotation> T setSubject(PdfString subject) {
        return put(PdfName.Subj, subject);
    }

    public PdfName getReplyType() {
        return getPdfObject().getAsName(PdfName.RT);
    }

    public <T extends PdfMarkupAnnotation> T setReplyType(PdfName replyType) {
        return put(PdfName.RT, replyType);
    }

    public PdfName getIntent() {
        return getPdfObject().getAsName(PdfName.IT);
    }

    public <T extends PdfMarkupAnnotation> T setIntent(PdfName intent) {
        return put(PdfName.IT, intent);
    }

    public PdfDictionary getExternalData() {
        return getPdfObject().getAsDictionary(PdfName.ExData);
    }

    public <T extends PdfMarkupAnnotation> T setExternalData(PdfName exData) {
        return put(PdfName.ExData, exData);
    }

    public <T extends PdfMarkupAnnotation> T setRectangleDifferences(PdfArray rect) {
        return put(PdfName.RD, rect);
    }

    public PdfArray getRectangleDifferences() {
        return getPdfObject().getAsArray(PdfName.RD);
    }

    public PdfDictionary getBorderEffect() {
        return getPdfObject().getAsDictionary(PdfName.BE);
    }

    public <T extends PdfMarkupAnnotation> T setBorderEffect(PdfDictionary borderEffect) {
        return put(PdfName.BE, borderEffect);
    }

    public PdfArray getInteriorColor() {
        return getPdfObject().getAsArray(PdfName.IC);
    }

    public <T extends PdfMarkupAnnotation> T setInteriorColor(PdfArray interiorColor) {
        return put(PdfName.IC, interiorColor);
    }

    public <T extends PdfMarkupAnnotation> T setInteriorColor(float[] interiorColor) {
        return setInteriorColor(new PdfArray(interiorColor));
    }


    public PdfName getIconName() {
        return getPdfObject().getAsName(PdfName.Name);
    }

    public <T extends PdfMarkupAnnotation> T setIconName(PdfName name) {
        return put(PdfName.Name, name);
    }

    public <T extends PdfMarkupAnnotation> T setDrawnAfter(PdfString appearanceString) {
        return put(PdfName.DA, appearanceString);
    }

    public PdfString getDrawnAfter() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    public int getJustification() {
        PdfNumber q = getPdfObject().getAsNumber(PdfName.Q);
        return q == null ? 0 : q.getIntValue();
    }

    public <T extends PdfMarkupAnnotation> T setJustification(int justification) {
        return put(PdfName.Q, new PdfNumber(justification));
    }
}
