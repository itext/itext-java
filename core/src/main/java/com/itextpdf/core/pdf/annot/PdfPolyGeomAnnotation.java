package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

public class PdfPolyGeomAnnotation extends PdfMarkupAnnotation {

    /**
     * Subtypes
     */
    public static final PdfName Polygon = PdfName.Polygon;
    public static final PdfName PolyLine = PdfName.PolyLine;

    public PdfPolyGeomAnnotation(Rectangle rect, PdfName subtype, float vertices[]) {
        super(rect);
        setSubtype(subtype);
        setVertices(vertices);
    }

    public PdfPolyGeomAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public static PdfPolyGeomAnnotation createPolygon(Rectangle rect, float vertices[]) {
        return new PdfPolyGeomAnnotation(rect, Polygon, vertices);
    }

    public static PdfPolyGeomAnnotation createPolyLine(Rectangle rect, float vertices[]) {
        return new PdfPolyGeomAnnotation(rect, PolyLine, vertices);
    }

    @Override
    public PdfName getSubtype() {
        return getPdfObject().getAsName(PdfName.Subtype);
    }

    public PdfArray getVertices() {
        return getPdfObject().getAsArray(PdfName.Vertices);
    }

    public PdfPolyGeomAnnotation setVertices(PdfArray vertices) {
        return put(PdfName.Vertices, vertices);
    }

    public PdfPolyGeomAnnotation setVertices(float[] vertices) {
        return put(PdfName.Vertices, new PdfArray(vertices));
    }

    public PdfArray getLineEndingStyles() {
        return getPdfObject().getAsArray(PdfName.LE);
    }

    public PdfPolyGeomAnnotation setLineEndingStyles(PdfArray lineEndingStyles) {
        return put(PdfName.LE, lineEndingStyles);
    }

    public PdfDictionary getMeasure() {
        return getPdfObject().getAsDictionary(PdfName.Measure);
    }

    public PdfPolyGeomAnnotation setMeasure(PdfDictionary measure) {
        return put(PdfName.Measure, measure);
    }

    private void setSubtype(PdfName subtype) {
        put(PdfName.Subtype, subtype);
    }

}
