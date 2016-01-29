package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;

public class PdfWidgetAnnotation extends PdfAnnotation {

    public PdfWidgetAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfWidgetAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Widget;
    }

    public PdfWidgetAnnotation setParent(PdfObject parent) {
        return put(PdfName.Parent, parent);
    }

    /**
     * Setter for the annotation's highlighting mode. Possible values are
     * <ul>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_NONE} - No highlighting.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_INVERT} - Invert the contents of the annotation rectangle.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_OUTLINE} - Invert the annotation's border.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_PUSH} - Display the annotation’s down appearance, if any.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_TOGGLE} - Same as P.</li>
     * </ul>
     * @param mode The new value for the annotation's highlighting mode.
     * @return The widget annotation which this method was called on.
     */
    public PdfWidgetAnnotation setHighlightMode(PdfName mode) {
        return put(PdfName.H, mode);
    }

    /**
     * Getter for the annotation's highlighting mode.
     * @return Current value of the annotation's highlighting mode.
     */
    public PdfName getHighlightMode() {
        return getPdfObject().getAsName(PdfName.H);
    }
}
