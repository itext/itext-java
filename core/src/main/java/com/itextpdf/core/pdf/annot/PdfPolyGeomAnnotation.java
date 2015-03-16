package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfPolyGeomAnnotation extends PdfMarkupAnnotation {

    /**
     * Subtypes
     */
    public static final PdfName Polygon = PdfName.Polygon;
    public static final PdfName PolyLine = PdfName.PolyLine;

    public PdfPolyGeomAnnotation(PdfDocument document, Rectangle rect, PdfName subtype, float vertices[]) throws PdfException {
        super(document, rect);
        setSubtype(subtype);
        setVertices(vertices);
    }

    public PdfPolyGeomAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    static public PdfPolyGeomAnnotation createPolygon(PdfDocument document, Rectangle rect, float vertices[]) throws PdfException {
        return new PdfPolyGeomAnnotation(document, rect, Polygon, vertices);
    }

    static public PdfPolyGeomAnnotation createPolyLine(PdfDocument document, Rectangle rect, float vertices[]) throws PdfException {
        return new PdfPolyGeomAnnotation(document, rect, PolyLine, vertices);
    }

    @Override
    public PdfName getSubtype() throws PdfException {
        return getPdfObject().getAsName(PdfName.Subtype);
    }

    public PdfArray getVertices() throws PdfException {
        return getPdfObject().getAsArray(PdfName.Vertices);
    }

    public PdfPolyGeomAnnotation setVertices(PdfArray vertices) {
        return put(PdfName.Vertices, vertices);
    }

    public PdfPolyGeomAnnotation setVertices(float[] vertices) {
        return put(PdfName.Vertices, new PdfArray(vertices));
    }

    public PdfArray getLineEndingStyles() throws PdfException {
        return getPdfObject().getAsArray(PdfName.LE);
    }

    public PdfPolyGeomAnnotation setLineEndingStyles(PdfArray lineEndingStyles) {
        return put(PdfName.LE, lineEndingStyles);
    }

    public PdfDictionary getMeasure() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.Measure);
    }

    public PdfPolyGeomAnnotation setMeasure(PdfDictionary measure) {
        return put(PdfName.Measure, measure);
    }

    private void setSubtype(PdfName subtype) {
        put(PdfName.Subtype, subtype);
    }

}
