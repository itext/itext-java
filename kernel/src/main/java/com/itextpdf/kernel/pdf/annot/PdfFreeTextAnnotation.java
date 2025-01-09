/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        if (contents != null) {
            setContents(contents);
        }
    }

    /**
     * Instantiates a new {@link PdfFreeTextAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
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
        return q == null ? PdfFreeTextAnnotation.LEFT_JUSTIFIED : q.intValue();
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

    /**
     * Gets the rotation angle in degrees.
     *
     * @return {@link PdfNumber} representing the clockwise rotation in degrees.
     */
    public PdfNumber getRotation() {
        return getPdfObject().getAsNumber(PdfName.Rotate);
    }

    /**
     * Sets the rotation angle in degrees.
     * @param degAngle an integer representing the clockwise rotation in degrees.
     *
     * @return this {@link PdfFreeTextAnnotation} instance.
     */
    public PdfFreeTextAnnotation setRotation(int degAngle) {
        put(PdfName.Rotate, new PdfNumber(degAngle));
        return this;
    }
}
