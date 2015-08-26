package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.pdfa.PdfAConformanceLevel;

public class PdfA1Checker extends PdfAChecker {

    public PdfA1Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    @Override
    public void checkCanvasStack(char stackOperation) {

    }

    @Override
    public void checkInlineImage(PdfImageXObject inlineImage) {

    }

    @Override
    protected void checkAction(PdfDictionary action) {

    }

    @Override
    protected void checkExtGState(PdfDictionary extGState) {

    }

    @Override
    protected void checkImageXObject(PdfStream image) {

    }

    @Override
    protected void checkFormXObject(PdfStream form) {

    }

    @Override
    protected void checkFont(PdfDictionary font) {

    }

    @Override
    protected void checkPdfNumber(PdfNumber number) {

    }

    @Override
    protected void checkPdfStream(PdfStream stream) {

    }

    @Override
    protected void checkPdfString(PdfString string) {

    }
}
