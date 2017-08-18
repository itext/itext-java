package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.List;
import java.util.Map;

public class PdfStructureDestination extends PdfDestination {

    public PdfStructureDestination(PdfArray structureDestination) {
        super(structureDestination);
    }

    private PdfStructureDestination() {
        super(new PdfArray());
    }

    public static PdfStructureDestination createXYZ(PdfStructElem elem, float left, float top, float zoom) {
        return create(elem, PdfName.XYZ, left, Float.NaN, Float.NaN, top, zoom);
    }

    public static PdfStructureDestination createFit(PdfStructElem elem) {
        return create(elem, PdfName.Fit, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfStructureDestination createFitH(PdfStructElem elem, float top) {
        return create(elem, PdfName.FitH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfStructureDestination createFitV(PdfStructElem elem, float left) {
        return create(elem, PdfName.FitV, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfStructureDestination createFitR(PdfStructElem elem, float left, float bottom, float right, float top) {
        return create(elem, PdfName.FitR, left, bottom, right, top, Float.NaN);
    }

    public static PdfStructureDestination createFitB(PdfStructElem elem) {
        return create(elem, PdfName.FitB, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfStructureDestination createFitBH(PdfStructElem elem, float top) {
        return create(elem, PdfName.FitBH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfStructureDestination createFitBV(PdfStructElem elem, float left) {
        return create(elem, PdfName.FitBH, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    private static PdfStructureDestination create(PdfStructElem elem, PdfName type, float left, float bottom, float right, float top, float zoom) {
        return new PdfStructureDestination().add(elem).add(type).add(left).add(bottom).add(right).add(top).add(zoom);
    }

    @Override
    public PdfObject getDestinationPage(Map<String, PdfObject> names) {
        PdfObject firstObj = ((PdfArray)getPdfObject()).get(0);
        if (firstObj.isDictionary()) {
                PdfStructElem structElem = new PdfStructElem((PdfDictionary)firstObj);
                while (true) {
                    List<IStructureNode> kids = structElem.getKids();
                    IStructureNode firstKid = kids.size() > 0 ? kids.get(0) : null;
                    if (firstKid instanceof PdfMcr) {
                        return ((PdfMcr) firstKid).getPageObject();
                    } else if (firstKid instanceof PdfStructElem) {
                        structElem = (PdfStructElem) firstKid;
                    } else {
                        break;
                    }
            }
        }
        return null;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

    private PdfStructureDestination add(float value) {
        if (!Float.isNaN(value)) {
            ((PdfArray) getPdfObject()).add(new PdfNumber(value));
        }
        return this;
    }

    private PdfStructureDestination add(PdfStructElem elem) {
        if (elem.getPdfObject().getIndirectReference() == null) {
            throw new PdfException(PdfException.StructureElementInStructureDestinationShallBeAnIndirectObject);
        }
        ((PdfArray)getPdfObject()).add(elem.getPdfObject());
        return this;
    }

    private PdfStructureDestination add(PdfName type) {
        ((PdfArray)getPdfObject()).add(type);
        return this;
    }

}
