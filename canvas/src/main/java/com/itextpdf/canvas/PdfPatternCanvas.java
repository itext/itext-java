package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfPattern;

public class PdfPatternCanvas extends PdfCanvas {

    private PdfPattern.Tiling tilingPattern;

    public PdfPatternCanvas(PdfStream contentStream, PdfResources resources, PdfDocument document) {
        super(contentStream, resources, document);
        this.tilingPattern = new PdfPattern.Tiling(contentStream);
    }

    public PdfPatternCanvas(PdfPattern.Tiling pattern, PdfDocument document) {
        super(pattern.getPdfObject(), pattern.getResources(), document);
        this.tilingPattern = pattern;
    }

    @Override
    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern, boolean fill) {
        checkNoColor();
        return super.setColor(colorSpace, colorValue, pattern, fill);
    }

    private void checkNoColor() {
        if (!tilingPattern.isColored()) {
            throw new PdfException(PdfException.ContentStreamMustNotInvokeOperatorsThatSpecifyColorsOrOtherColorRelatedParameters);
        }
    }

}
