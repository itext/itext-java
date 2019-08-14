/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.kernel.pdf.action.PdfAction;

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

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfWidgetAnnotation(PdfDictionary pdfObject) {
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
     *     <li>{@link PdfAnnotation#HIGHLIGHT_NONE} - No highlighting.
     *     <li>{@link PdfAnnotation#HIGHLIGHT_INVERT} - Invert the contents of the annotation rectangle.
     *     <li>{@link PdfAnnotation#HIGHLIGHT_OUTLINE} - Invert the annotation's border.
     *     <li>{@link PdfAnnotation#HIGHLIGHT_PUSH} - Display the annotation?s down appearance, if any.
     *     <li>{@link PdfAnnotation#HIGHLIGHT_TOGGLE} - Same as P.
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
            kids.remove(annotDict);
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

    /**
     * An {@link PdfAction} to perform, such as launching an application, playing a sound,
     * changing an annotation’s appearance state etc, when the annotation is activated.
     * @return {@link PdfDictionary} which defines the characteristics and behaviour of an action.
     */
    public PdfDictionary getAction() {
        return getPdfObject().getAsDictionary(PdfName.A);
    }

    /**
     * Sets a {@link PdfAction} to this annotation which will be performed when the annotation is activated.
     * @param action {@link PdfAction} to set to this annotation.
     * @return this {@link PdfWidgetAnnotation} instance.
     */
    public PdfWidgetAnnotation setAction(PdfAction action) {
        return (PdfWidgetAnnotation) put(PdfName.A, action.getPdfObject());
    }

    /**
     * An additional actions dictionary that extends the set of events that can trigger the execution of an action.
     * See ISO-320001 12.6.3 Trigger Events.
     * @return an additional actions {@link PdfDictionary}.
     * @see #getAction()
     */
    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    /**
     * Sets an additional {@link PdfAction} to this annotation which will be performed in response to
     * the specific trigger event defined by {@code key}. See ISO-320001 12.6.3, "Trigger Events".
     * @param key a {@link PdfName} that denotes a type of the additional action to set.
     * @param action {@link PdfAction} to set as additional to this annotation.
     * @return this {@link PdfWidgetAnnotation} instance.
     */
    public PdfWidgetAnnotation setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    /**
     * An appearance characteristics dictionary containing additional information for constructing the
     * annotation’s appearance stream. See ISO-320001, Table 189.
     *
     * @return an appearance characteristics dictionary or null if it isn't specified.
     */
    public PdfDictionary getAppearanceCharacteristics() {
        return getPdfObject().getAsDictionary(PdfName.MK);
    }

    /**
     * Sets an appearance characteristics dictionary containing additional information for constructing the
     * annotation’s appearance stream. See ISO-320001, Table 189.
     *
     * @param characteristics the {@link PdfDictionary} with additional information for appearance stream.
     * @return this {@link PdfWidgetAnnotation} instance.
     */
    public PdfWidgetAnnotation setAppearanceCharacteristics(PdfDictionary characteristics) {
        return (PdfWidgetAnnotation) put(PdfName.MK, characteristics);
    }

    /**
     * The dictionaries for some annotation types (such as free text and polygon annotations) can include the BS entry.
     * That entry specifies a border style dictionary that has more settings than the array specified for the Border
     * entry (see {@link PdfAnnotation#getBorder()}). If an annotation dictionary includes the BS entry, then the Border
     * entry is ignored. If annotation includes AP (see {@link PdfAnnotation#getAppearanceDictionary()}) it takes
     * precedence over the BS entry. For more info on BS entry see ISO-320001, Table 166.
     * @return {@link PdfDictionary} which is a border style dictionary or null if it is not specified.
     */
    public PdfDictionary getBorderStyle() {
        return getPdfObject().getAsDictionary(PdfName.BS);
    }

    /**
     * Sets border style dictionary that has more settings than the array specified for the Border entry ({@link PdfAnnotation#getBorder()}).
     * See ISO-320001, Table 166 and {@link #getBorderStyle()} for more info.
     * @param borderStyle a border style dictionary specifying the line width and dash pattern that shall be used
     *                    in drawing the annotation’s border.
     * @return this {@link PdfWidgetAnnotation} instance.
     */
    public PdfWidgetAnnotation setBorderStyle(PdfDictionary borderStyle) {
        return (PdfWidgetAnnotation) put(PdfName.BS, borderStyle);
    }

    /**
     * Setter for the annotation's preset border style. Possible values are
     * <ul>
     *     <li>{@link PdfAnnotation#STYLE_SOLID} - A solid rectangle surrounding the annotation.
     *     <li>{@link PdfAnnotation#STYLE_DASHED} - A dashed rectangle surrounding the annotation.
     *     <li>{@link PdfAnnotation#STYLE_BEVELED} - A simulated embossed rectangle that appears to be raised above the surface of the page.
     *     <li>{@link PdfAnnotation#STYLE_INSET} - A simulated engraved rectangle that appears to be recessed below the surface of the page.
     *     <li>{@link PdfAnnotation#STYLE_UNDERLINE} - A single line along the bottom of the annotation rectangle.
     * </ul>
     * See also ISO-320001, Table 166.
     * @param style The new value for the annotation's border style.
     * @return this {@link PdfWidgetAnnotation} instance.
     * @see #getBorderStyle()
     */
    public PdfWidgetAnnotation setBorderStyle(PdfName style) {
        return setBorderStyle(BorderStyleUtil.setStyle(getBorderStyle(), style));
    }

    /**
     * Setter for the annotation's preset dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for the annotation border style (see {@link #setBorderStyle(PdfName)}.
     * See ISO-320001 8.4.3.6, "Line Dash Pattern" for the format in which dash pattern shall be specified.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return this {@link PdfWidgetAnnotation} instance.
     */
    public PdfWidgetAnnotation setDashPattern(PdfArray dashPattern) {
        return setBorderStyle(BorderStyleUtil.setDashPattern(getBorderStyle(), dashPattern));
    }
}
