package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfPattern;

public class PdfPatternCanvas extends PdfCanvas {

    private PdfPattern.Tiling tilingPattern;

    public PdfPatternCanvas(PdfStream contentStream, PdfResources resources) throws PdfException {
        super(contentStream, resources);
        this.tilingPattern = new PdfPattern.Tiling(contentStream, document);
    }

    public PdfPatternCanvas(PdfPattern.Tiling pattern) throws PdfException {
        super(pattern.getPdfObject(), pattern.getResources());
        this.tilingPattern = pattern;
    }

    @Override
    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern, boolean fill) throws PdfException {
        checkNoColor();
        return super.setColor(colorSpace, colorValue, pattern, fill);
    }

    private void checkNoColor() throws PdfException {
        if (!tilingPattern.isColored()) {
            throw new PdfException(PdfException.ContentStreamMustNotInvokeOperatorsThatSpecifyColorsOrOtherColorRelatedParameters);
        }
    }

}
