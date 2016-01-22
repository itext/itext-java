package com.itextpdf.core.pdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfPattern;

/**
 * A PdfCanvas instance with an inherent tiling pattern.
 */
public class PdfPatternCanvas extends PdfCanvas {

    private final PdfPattern.Tiling tilingPattern;

    /**
     * Creates PdfPatternCanvas from content stream of page, form XObject, pattern etc.
     *
     * @param contentStream @see PdfStream.
     * @param resources the resources, a specialized dictionary that can be used by PDF instructions in the content stream
     * @param document the document that the resulting content stream will be written to
     */
    public PdfPatternCanvas(PdfStream contentStream, PdfResources resources, PdfDocument document) {
        super(contentStream, resources, document);
        this.tilingPattern = new PdfPattern.Tiling(contentStream);
    }

    /**
     * Creates PdfPatternCanvas for a document from a provided Tiling pattern
     * @param pattern @see PdfPattern.Tiling. The Tiling pattern must be colored
     * @param document the document that the resulting content stream will be written to 
     */
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
