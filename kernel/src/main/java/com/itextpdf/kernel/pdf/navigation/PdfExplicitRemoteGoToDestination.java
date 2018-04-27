package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

import java.util.Map;

/**
 * This class shall be used for creation of destinations, associated Remote Go-To and Embedded Go-To actions only,
 * i.e. the destination point is in another PDF.
 * If you need to create a destination, associated with an object inside current PDF, you should use {@link PdfExplicitDestination} class instead.
 */
public class PdfExplicitRemoteGoToDestination extends PdfDestination {

    private static final long serialVersionUID = 5354781072160968173L;

    public PdfExplicitRemoteGoToDestination() {
        this(new PdfArray());
    }

    public PdfExplicitRemoteGoToDestination(PdfArray pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfObject getDestinationPage(Map<String, PdfObject> names) {
        return ((PdfArray)getPdfObject()).get(0);
    }

    @Override
    public PdfDestination replaceNamedDestination(Map<Object, PdfObject> names) { return this; }

    public static PdfExplicitRemoteGoToDestination createXYZ(int pageNum, float left, float top, float zoom) {
        return create(pageNum, PdfName.XYZ, left, Float.NaN, Float.NaN, top, zoom);
    }

    public static PdfExplicitRemoteGoToDestination createFit(int pageNum) {
        return create(pageNum, PdfName.Fit, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitRemoteGoToDestination createFitH(int pageNum, float top) {
        return create(pageNum, PdfName.FitH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfExplicitRemoteGoToDestination createFitV(int pageNum, float left) {
        return create(pageNum, PdfName.FitV, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitRemoteGoToDestination createFitR(int pageNum, float left, float bottom, float right, float top) {
        return create(pageNum, PdfName.FitR, left, bottom, right, top, Float.NaN);
    }

    public static PdfExplicitRemoteGoToDestination createFitB(int pageNum) {
        return create(pageNum, PdfName.FitB, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitRemoteGoToDestination createFitBH(int pageNum, float top) {
        return create(pageNum, PdfName.FitBH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfExplicitRemoteGoToDestination createFitBV(int pageNum, float left) {
        return create(pageNum, PdfName.FitBH, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitRemoteGoToDestination create(int pageNum, PdfName type, float left, float bottom, float right, float top, float zoom) {
        return new PdfExplicitRemoteGoToDestination().add(--pageNum).add(type).add(left).add(bottom).add(right).add(top).add(zoom);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

    private PdfExplicitRemoteGoToDestination add(float value) {
        if (!Float.isNaN(value)) {
            ((PdfArray) getPdfObject()).add(new PdfNumber(value));
        }
        return this;
    }

    private PdfExplicitRemoteGoToDestination add(int value) {
        ((PdfArray)getPdfObject()).add(new PdfNumber(value));
        return this;
    }

    private PdfExplicitRemoteGoToDestination add(PdfName type) {
        ((PdfArray)getPdfObject()).add(type);
        return this;
    }
}
