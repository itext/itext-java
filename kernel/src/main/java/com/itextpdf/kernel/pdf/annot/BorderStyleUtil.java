package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

class BorderStyleUtil {

    private BorderStyleUtil(){
    }

    /**
     * Setter for the border style. Possible values are
     * <ul>
     *     <li>{@link PdfAnnotation#STYLE_SOLID} - A solid rectangle surrounding the annotation.</li>
     *     <li>{@link PdfAnnotation#STYLE_DASHED} - A dashed rectangle surrounding the annotation.</li>
     *     <li>{@link PdfAnnotation#STYLE_BEVELED} - A simulated embossed rectangle that appears to be raised above the surface of the page.</li>
     *     <li>{@link PdfAnnotation#STYLE_INSET} - A simulated engraved rectangle that appears to be recessed below the surface of the page.</li>
     *     <li>{@link PdfAnnotation#STYLE_UNDERLINE} - A single line along the bottom of the annotation rectangle.</li>
     * </ul>
     * See also ISO-320001, Table 166.
     * @param bs original border style dictionary.
     * @param style The new value for the annotation's border style.
     * @return Updated border style dictionary entry.
     */
    public static final PdfDictionary setStyle(PdfDictionary bs, PdfName style) {
        if (null == bs) {
            bs = new PdfDictionary();
        }
        bs.put(PdfName.S, style);
        return bs;
    }

    /**
     * Setter for the dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for border style (see {@link #setStyle(PdfDictionary, PdfName)}.
     * See ISO-320001 8.4.3.6, “Line Dash Pattern” for the format in which dash pattern shall be specified.
     *
     * @param bs original border style dictionary.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return Updated border style dictionary entry.
     */
    public static final PdfDictionary setDashPattern(PdfDictionary bs, PdfArray dashPattern) {
        if (null == bs) {
            bs = new PdfDictionary();
        }
        bs.put(PdfName.D, dashPattern);
        return bs;
    }
}
