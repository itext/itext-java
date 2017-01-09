/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

import java.util.HashSet;

public class PdfWidgetAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 9013938639824707088L;
    public static final int HIDDEN = 1;
    public static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
    public static final int HIDDEN_BUT_PRINTABLE = 3;
    public static final int VISIBLE = 4;

	public PdfWidgetAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfWidgetAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    private HashSet<PdfName> widgetEntries = new HashSet<PdfName>();

    {
        widgetEntries.add(PdfName.Subtype);
        widgetEntries.add(PdfName.Type);
        widgetEntries.add(PdfName.Rect);
        widgetEntries.add(PdfName.Contents);
        widgetEntries.add(PdfName.P);
        widgetEntries.add(PdfName.NM);
        widgetEntries.add(PdfName.M);
        widgetEntries.add(PdfName.F);
        widgetEntries.add(PdfName.AP);
        widgetEntries.add(PdfName.AS);
        widgetEntries.add(PdfName.Border);
        widgetEntries.add(PdfName.C);
        widgetEntries.add(PdfName.StructParent);
        widgetEntries.add(PdfName.OC);
        widgetEntries.add(PdfName.H);
        widgetEntries.add(PdfName.MK);
        widgetEntries.add(PdfName.A);
        widgetEntries.add(PdfName.AA);
        widgetEntries.add(PdfName.BS);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Widget;
    }

    public PdfWidgetAnnotation setParent(PdfObject parent) {
        return (PdfWidgetAnnotation) put(PdfName.Parent, parent);
    }

    /**
     * Setter for the annotation's highlighting mode. Possible values are
     * <ul>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_NONE} - No highlighting.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_INVERT} - Invert the contents of the annotation rectangle.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_OUTLINE} - Invert the annotation's border.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_PUSH} - Display the annotation?s down appearance, if any.</li>
     *     <li>{@link PdfAnnotation#HIGHLIGHT_TOGGLE} - Same as P.</li>
     * </ul>
     * @param mode The new value for the annotation's highlighting mode.
     * @return The widget annotation which this method was called on.
     */
    public PdfWidgetAnnotation setHighlightMode(PdfName mode) {
        return (PdfWidgetAnnotation) put(PdfName.H, mode);
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
        for (PdfName entry: widgetEntries) {
            annotDict.remove(entry);
        }
        PdfDictionary parent = annotDict.getAsDictionary(PdfName.Parent);
        if (parent != null && annotDict.size() == 1) {
            PdfArray kids = parent.getAsArray(PdfName.Kids);
            kids.remove(annotDict.getIndirectReference());
            if (kids.size() == 0) {
                parent.remove(PdfName.Kids);
            }
        }
    }
    /**
     * Set the visibility flags of the Widget annotation
     * Options are: HIDDEN, HIDDEN_BUT_PRINTABLE, VISIBLE, VISIBLE_BUT_DOES_NOT_PRINT
     * @param visibility visibility option
     * @return the edited widget annotation
     */
    public PdfWidgetAnnotation setVisibility(int visibility) {
        switch (visibility) {
            case HIDDEN:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.HIDDEN));
                break;
            case VISIBLE_BUT_DOES_NOT_PRINT:
                break;
            case HIDDEN_BUT_PRINTABLE:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.NO_VIEW));
                break;
            case VISIBLE:
            default:
                getPdfObject().put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT));
                break;
        }
        return this;
    }
}
