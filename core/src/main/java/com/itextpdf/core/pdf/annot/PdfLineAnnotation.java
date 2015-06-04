package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfLineAnnotation extends PdfMarkupAnnotation {

    public PdfLineAnnotation(PdfDocument document, Rectangle rect, float[] line) {
        super(document, rect);
        put(PdfName.L, new PdfArray(line));
    }

    public PdfLineAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Line;
    }

    public PdfArray getLine() {
        return getPdfObject().getAsArray(PdfName.L);
    }

    public PdfArray getLineEndingStyles() {
        return getPdfObject().getAsArray(PdfName.LE);
    }

    public PdfLineAnnotation setLineEndingStyles(PdfArray lineEndingStyles) {
        return put(PdfName.LE, lineEndingStyles);
    }

    public float getLeaderLine() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LE);
        return n == null ? 0 : n.getFloatValue();
    }

    public PdfLineAnnotation setLeaderLine(float leaderLine) {
        return put(PdfName.LE, new PdfNumber(leaderLine));
    }

    public float getLeaderLineExtension() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LLE);
        return n == null ? 0 : n.getFloatValue();
    }

    public PdfLineAnnotation setLeaderLineExtension(float leaderLineExtension) {
        return put(PdfName.LLE, new PdfNumber(leaderLineExtension));
    }

    public float getLeaderLineOffset() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LLO);
        return n == null ? 0 : n.getFloatValue();
    }

    public PdfLineAnnotation setLeaderLineOffset(float leaderLineOffset) {
        return put(PdfName.LLO, new PdfNumber(leaderLineOffset));
    }

    public boolean getContentsAsCaption() {
        PdfBoolean b = getPdfObject().getAsBoolean(PdfName.Cap);
        return b == null ? false : b.getValue();
    }

    public PdfLineAnnotation setContentsAsCaption(boolean contentsAsCaption) {
        return put(PdfName.Cap, new PdfBoolean(contentsAsCaption));
    }

    public PdfName getCaptionPosition() {
        return getPdfObject().getAsName(PdfName.CP);
    }

    public PdfLineAnnotation setCaptionPosition(PdfName captionPosition) {
        return put(PdfName.CP, captionPosition);
    }

    public PdfDictionary getMeasure() {
        return getPdfObject().getAsDictionary(PdfName.Measure);
    }

    public PdfLineAnnotation setMeasure(PdfDictionary measure) {
        return put(PdfName.Measure, measure);
    }

    public PdfArray getCaptionOffset() {
        return getPdfObject().getAsArray(PdfName.CO);
    }

    public PdfLinkAnnotation setCaptionOffset(PdfArray captionOffset) {
        return put(PdfName.CO, captionOffset);
    }

    public PdfLinkAnnotation setCaptionOffset(float[] captionOffset) {
        return setCaptionOffset(new PdfArray(captionOffset));
    }



}
