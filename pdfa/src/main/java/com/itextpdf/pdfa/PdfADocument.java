package com.itextpdf.pdfa;

import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;

import java.io.IOException;

public class PdfADocument extends PdfDocument {
    private PdfAChecker checker;

    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        super(writer);
        addOutputIntent(outputIntent);
        setChecker(conformanceLevel, outputIntent);
    }

    public PdfADocument(PdfReader reader, PdfWriter writer, boolean append, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        super(reader, writer, append);
        addOutputIntent(outputIntent);
        setChecker(conformanceLevel, outputIntent);
    }

    public PdfADocument(PdfReader reader, PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        super(reader, writer);
        addOutputIntent(outputIntent);
        setChecker(conformanceLevel, outputIntent);
    }

    @Override
    public void checkPdfIsoConformance(Object obj, IsoKey key) {
        switch (key) {
            case CANVAS_STACK:
                checker.checkCanvasStack((Character) obj);
                break;
            case COLOR:
                //TODO checker.checkColor(obj);
                break;
            case INLINE_IMAGE:
                checker.checkInlineImage((PdfImageXObject) obj);
                break;
            case PDF_OBJECT:
                checker.checkPdfObject((PdfObject) obj);
                break;
        }
    }

    public PdfAConformanceLevel getConformanceLevel() {
        return checker.getConformanceLevel();
    }

    public void addOutputIntent(PdfOutputIntent outputIntent) {
        PdfArray outputIntents = catalog.getPdfObject().getAsArray(PdfName.OutputIntents);
        if (outputIntents == null) {
            outputIntents = new PdfArray();
            catalog.put(PdfName.OutputIntents, outputIntents);
        }
        outputIntents.add(outputIntent.getPdfObject());
    }

    @Override
    protected void checkPdfIsoConformance() {
        checker.checkDocument(catalog);
    }

    @Override
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        if (isClosing) {
            super.flushObject(pdfObject, canBeInObjStm);
        } else {
            //suppress the call
        }
    }

    private void setChecker(PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        String pdfAOutputIntentColorSpace = null;
        if (outputIntent.getPdfObject().get(PdfName.S).equals(PdfName.GTS_PDFA1)) {
            PdfStream destOutputProfile = outputIntent.getDestOutputProfile();
            pdfAOutputIntentColorSpace = IccProfile.getIccColorSpaceName(destOutputProfile.getBytes());
        }
        switch (conformanceLevel) {
            case PDF_A_1A:
            case PDF_A_1B:
                checker = new PdfA1Checker(conformanceLevel, pdfAOutputIntentColorSpace);
                break;
            case PDF_A_2A:
            case PDF_A_2B:
            case PDF_A_2U:
                checker = new PdfA2Checker(conformanceLevel, pdfAOutputIntentColorSpace);
                break;
            case PDF_A_3A:
            case PDF_A_3B:
            case PDF_A_3U:
                checker = new PdfA3Checker(conformanceLevel, pdfAOutputIntentColorSpace);
                break;
        }
    }
}
