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
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.da.AnnotationDefaultAppearance;

public class PdfFreeTextAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -7835504102518915220L;
	/**
     * Text justification options.
     */
    public static final int LEFT_JUSTIFIED = 0;
    public static final int CENTERED = 1;
    public static final int RIGHT_JUSTIFIED = 2;

    /**
     * Creates new instance
     *
     * @param rect - rectangle that specifies annotation position and bounds on page
     * @param contents - the displayed text
     */
    public PdfFreeTextAnnotation(Rectangle rect, PdfString contents) {
        super(rect);
        setContents(contents);
    }

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfFreeTextAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.FreeText;
    }

    public PdfString getDefaultStyleString() {
        return getPdfObject().getAsString(PdfName.DS);
    }

    public PdfFreeTextAnnotation setDefaultStyleString(PdfString defaultStyleString) {
        return (PdfFreeTextAnnotation) put(PdfName.DS, defaultStyleString);
    }

    /**
     * The default appearance string that shall be used in formatting the text. See ISO-32001 12.7.3.3, "Variable Text".
     * @return a {@link PdfString} that specifies the default appearance, or null if default appereance is not specified.
     */
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * The default appearance string that shall be used in formatting the text. See ISO-32001 12.7.3.3, "Variable Text".
     * @param appearanceString a {@link PdfString} that specifies the default appearance.
     * @return this {@link PdfFreeTextAnnotation} instance.
     */
    public PdfFreeTextAnnotation setDefaultAppearance(PdfString appearanceString) {
        return (PdfFreeTextAnnotation) put(PdfName.DA, appearanceString);
    }

    public PdfFreeTextAnnotation setDefaultAppearance(AnnotationDefaultAppearance da) {
        return setDefaultAppearance(da.toPdfString());
    }

    public PdfArray getCalloutLine() {
        return getPdfObject().getAsArray(PdfName.CL);
    }

    public PdfFreeTextAnnotation setCalloutLine(float[] calloutLine) {
        return setCalloutLine(new PdfArray(calloutLine));
    }

    public PdfFreeTextAnnotation setCalloutLine(PdfArray calloutLine) {
        return (PdfFreeTextAnnotation) put(PdfName.CL, calloutLine);
    }

    public PdfName getLineEndingStyle() {
        return getPdfObject().getAsName(PdfName.LE);
    }

    public PdfFreeTextAnnotation setLineEndingStyle(PdfName lineEndingStyle) {
        return (PdfFreeTextAnnotation) put(PdfName.LE, lineEndingStyle);
    }

    /**
     * A code specifying the form of quadding (justification) that is used in displaying the annotation's text:
     * 0 - Left-justified, 1 - Centered, 2 - Right-justified. Default value: 0 (left-justified).
     * @return a code specifying the form of quadding (justification), returns the default value if not explicitly specified.
     */
    public int getJustification() {
        PdfNumber q = getPdfObject().getAsNumber(PdfName.Q);
        return q == null ? 0 : q.intValue();
    }

    /**
     * A code specifying the form of quadding (justification) that is used in displaying the annotation's text:
     * 0 - Left-justified, 1 - Centered, 2 - Right-justified. Default value: 0 (left-justified).
     * @param justification a code specifying the form of quadding (justification).
     * @return this {@link PdfFreeTextAnnotation} instance.
     */
    public PdfFreeTextAnnotation setJustification(int justification) {
        return (PdfFreeTextAnnotation) put(PdfName.Q, new PdfNumber(justification));
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
     * @return this {@link PdfFreeTextAnnotation} instance.
     */
    public PdfFreeTextAnnotation setBorderStyle(PdfDictionary borderStyle) {
        return (PdfFreeTextAnnotation) put(PdfName.BS, borderStyle);
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
     * @return this {@link PdfFreeTextAnnotation} instance.
     * @see #getBorderStyle()
     */
    public PdfFreeTextAnnotation setBorderStyle(PdfName style) {
        return setBorderStyle(BorderStyleUtil.setStyle(getBorderStyle(), style));
    }

    /**
     * Setter for the annotation's preset dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for the annotation border style (see {@link #setBorderStyle(PdfName)}.
     * See ISO-320001 8.4.3.6, "Line Dash Pattern" for the format in which dash pattern shall be specified.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return this {@link PdfFreeTextAnnotation} instance.
     */
    public PdfFreeTextAnnotation setDashPattern(PdfArray dashPattern) {
        return setBorderStyle(BorderStyleUtil.setDashPattern(getBorderStyle(), dashPattern));
    }

    /**
     * A set of four numbers describing the numerical differences between two rectangles:
     * the Rect entry of the annotation and the inner rectangle where the annotation's text should be displayed
     *
     * @return null if not specified, otherwise a {@link PdfArray} with four numbers which correspond to the
     * differences in default user space between the left, top, right, and bottom coordinates of Rect and those
     * of the inner rectangle, respectively.
     */
    public PdfArray getRectangleDifferences() {
        return getPdfObject().getAsArray(PdfName.RD);
    }

    /**
     * A set of four numbers describing the numerical differences between two rectangles:
     * the Rect entry of the annotation and the inner rectangle where the annotation's text should be displayed
     *
     * @param rect a {@link PdfArray} with four numbers which correspond to the differences in default user space between
     *             the left, top, right, and bottom coordinates of Rect and those of the inner rectangle, respectively.
     *             Each value shall be greater than or equal to 0. The sum of the top and bottom differences shall be
     *             less than the height of Rect, and the sum of the left and right differences shall be less than
     *             the width of Rect.
     * @return this {@link PdfFreeTextAnnotation} instance.
     */
    public PdfFreeTextAnnotation setRectangleDifferences(PdfArray rect) {
        return (PdfFreeTextAnnotation) put(PdfName.RD, rect);
    }

    /**
     * A border effect dictionary that specifies an effect that shall be applied to the border of the annotations.
     *
     * @return a {@link PdfDictionary}, which is a border effect dictionary (see ISO-320001, Table 167).
     */
    public PdfDictionary getBorderEffect() {
        return getPdfObject().getAsDictionary(PdfName.BE);
    }

    /**
     * Sets a border effect dictionary that specifies an effect that shall be applied to the border of the annotations.
     *
     * @param borderEffect a {@link PdfDictionary} which contents shall be specified in accordance to ISO-320001, Table 167.
     * @return this {@link PdfFreeTextAnnotation} instance.
     */
    public PdfFreeTextAnnotation setBorderEffect(PdfDictionary borderEffect) {
        return (PdfFreeTextAnnotation) put(PdfName.BE, borderEffect);
    }
}
