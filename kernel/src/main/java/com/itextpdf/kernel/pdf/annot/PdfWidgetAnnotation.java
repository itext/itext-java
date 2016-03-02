package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

import java.util.HashSet;

public class PdfWidgetAnnotation extends PdfAnnotation {

    public PdfWidgetAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfWidgetAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    private HashSet<PdfName> widgetEntries = new HashSet<PdfName>() {{
        add(PdfName.Subtype);
        add(PdfName.Type);
        add(PdfName.Rect);
        add(PdfName.Contents);
        add(PdfName.P);
        add(PdfName.NM);
        add(PdfName.M);
        add(PdfName.F);
        add(PdfName.AP);
        add(PdfName.AS);
        add(PdfName.Border);
        add(PdfName.C);
        add(PdfName.StructParent);
        add(PdfName.OC);
        add(PdfName.H);
        add(PdfName.MK);
        add(PdfName.A);
        add(PdfName.AA);
        add(PdfName.BS);
    }};

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

    /**
     * This method removes all widget annotation entries from the form field  the given annotation merged with.
     */
    public void releaseFormFieldFromWidgetAnnotation(){
        PdfDictionary annotDict = getPdfObject();
        getPdfObject().keySet().removeAll(widgetEntries);
        PdfDictionary parent = annotDict.getAsDictionary(PdfName.Parent);
        if (parent != null && annotDict.keySet().size() == 1) {
            PdfArray kids = parent.getAsArray(PdfName.Kids);
            kids.remove(annotDict.getIndirectReference());
            if (kids.isEmpty()) {
                parent.remove(PdfName.Kids);
            }
        }
    }
}
