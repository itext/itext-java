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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import org.slf4j.LoggerFactory;
import com.itextpdf.kernel.pdf.PdfObject;

public abstract class PdfPolyGeomAnnotation extends PdfMarkupAnnotation {

    PdfPolyGeomAnnotation(Rectangle rect, float[] vertices) {
        super(rect);
        setVertices(vertices);
    }

    /**
     * Instantiates a new {@link PdfPolyGeomAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfPolyGeomAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public static PdfPolyGeomAnnotation createPolygon(Rectangle rect, float[] vertices) {
        return new PdfPolygonAnnotation(rect, vertices);
    }

    public static PdfPolyGeomAnnotation createPolyLine(Rectangle rect, float[] vertices) {
        return new PdfPolylineAnnotation(rect, vertices);
    }

    public PdfArray getVertices() {
        return getPdfObject().getAsArray(PdfName.Vertices);
    }

    public PdfPolyGeomAnnotation setVertices(PdfArray vertices) {
        if (getPdfObject().containsKey(PdfName.Path)) {
            LoggerFactory.getLogger(getClass()).warn(IoLogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED);
        }
        return (PdfPolyGeomAnnotation) put(PdfName.Vertices, vertices);
    }

    public PdfPolyGeomAnnotation setVertices(float[] vertices) {
        if (getPdfObject().containsKey(PdfName.Path)) {
            LoggerFactory.getLogger(getClass()).warn(IoLogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED);
        }
        return (PdfPolyGeomAnnotation) put(PdfName.Vertices, new PdfArray(vertices));
    }

    public PdfArray getLineEndingStyles() {
        return getPdfObject().getAsArray(PdfName.LE);
    }

    public PdfPolyGeomAnnotation setLineEndingStyles(PdfArray lineEndingStyles) {
        return (PdfPolyGeomAnnotation) put(PdfName.LE, lineEndingStyles);
    }

    public PdfDictionary getMeasure() {
        return getPdfObject().getAsDictionary(PdfName.Measure);
    }

    public PdfPolyGeomAnnotation setMeasure(PdfDictionary measure) {
        return (PdfPolyGeomAnnotation) put(PdfName.Measure, measure);
    }

    /**
     * PDF 2.0. An array of n arrays, each supplying the operands for a
     * path building operator (m, l or c).
     * Each of the n arrays shall contain pairs of values specifying the points (x and
     * y values) for a path drawing operation.
     * The first array shall be of length 2 and specifies the operand of a moveto
     * operator which establishes a current point.
     * Subsequent arrays of length 2 specify the operands of lineto operators.
     * Arrays of length 6 specify the operands for curveto operators.
     * Each array is processed in sequence to construct the path.
     *
     * @return path, or <code>null</code> if path is not set
     */
    public PdfArray getPath() {
        return getPdfObject().getAsArray(PdfName.Path);
    }

    /**
     * PDF 2.0. An array of n arrays, each supplying the operands for a
     * path building operator (m, l or c).
     * Each of the n arrays shall contain pairs of values specifying the points (x and
     * y values) for a path drawing operation.
     * The first array shall be of length 2 and specifies the operand of a moveto
     * operator which establishes a current point.
     * Subsequent arrays of length 2 specify the operands of lineto operators.
     * Arrays of length 6 specify the operands for curveto operators.
     * Each array is processed in sequence to construct the path.
     *
     * @param path the path to set
     * @return this {@link PdfPolyGeomAnnotation} instance
     */
    public PdfPolyGeomAnnotation setPath(PdfArray path) {
        if (getPdfObject().containsKey(PdfName.Vertices)) {
            LoggerFactory.getLogger(getClass()).error(IoLogMessageConstant.IF_PATH_IS_SET_VERTICES_SHALL_NOT_BE_PRESENT);
        }
        return (PdfPolyGeomAnnotation) put(PdfName.Path, path);
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
     * @return this {@link PdfPolyGeomAnnotation} instance.
     */
    public PdfPolyGeomAnnotation setBorderStyle(PdfDictionary borderStyle) {
        return (PdfPolyGeomAnnotation) put(PdfName.BS, borderStyle);
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
     * @return this {@link PdfPolyGeomAnnotation} instance.
     * @see #getBorderStyle()
     */
    public PdfPolyGeomAnnotation setBorderStyle(PdfName style) {
        return setBorderStyle(BorderStyleUtil.setStyle(getBorderStyle(), style));
    }

    /**
     * Setter for the annotation's preset dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for the annotation border style (see {@link #setBorderStyle(PdfName)}.
     * See ISO-320001 8.4.3.6, "Line Dash Pattern" for the format in which dash pattern shall be specified.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return this {@link PdfPolyGeomAnnotation} instance.
     */
    public PdfPolyGeomAnnotation setDashPattern(PdfArray dashPattern) {
        return setBorderStyle(BorderStyleUtil.setDashPattern(getBorderStyle(), dashPattern));
    }

    /**
     * Gets a border effect dictionary that specifies an effect that shall be applied to the border of the annotations.
     * @return a {@link PdfDictionary}, which is a border effect dictionary (see ISO-320001, Table 167).
     */
    public PdfDictionary getBorderEffect() {
        return getPdfObject().getAsDictionary(PdfName.BE);
    }

    /**
     * Sets a border effect dictionary that specifies an effect that shall be applied to the border of the annotations.
     *
     * @param borderEffect a {@link PdfDictionary} which contents shall be specified in accordance to ISO-320001, Table 167.
     * @return this {@link PdfPolyGeomAnnotation} instance.
     */
    public PdfPolyGeomAnnotation setBorderEffect(PdfDictionary borderEffect) {
        return (PdfPolyGeomAnnotation) put(PdfName.BE, borderEffect);
    }

    /**
     * The interior color which is used to fill the annotation's line endings.
     *
     * @return {@link Color} of either {@link DeviceGray}, {@link DeviceRgb} or {@link DeviceCmyk} type which defines
     * interior color of the annotation, or null if interior color is not specified.
     */
    public Color getInteriorColor() {
        return InteriorColorUtil.parseInteriorColor(getPdfObject().getAsArray(PdfName.IC));
    }

    /**
     * An array of numbers in the range 0.0 to 1.0 specifying the interior color
     * which is used to fill the annotation's line endings.
     * @param interiorColor a {@link PdfArray} of numbers in the range 0.0 to 1.0. The number of array elements determines
     *                      the colour space in which the colour is defined: 0 - No colour, transparent; 1 - DeviceGray,
     *                      3 - DeviceRGB, 4 - DeviceCMYK. For the {@link PdfRedactAnnotation} number of elements shall be
     *                      equal to 3 (which defines DeviceRGB colour space).
     * @return this {@link PdfPolyGeomAnnotation} instance.
     */
    public PdfPolyGeomAnnotation setInteriorColor(PdfArray interiorColor) {
        return (PdfPolyGeomAnnotation) put(PdfName.IC, interiorColor);
    }

    /**
     * An array of numbers in the range 0.0 to 1.0 specifying the interior color
     * which is used to fill the annotation's line endings.
     *
     * @param interiorColor an array of floats in the range 0.0 to 1.0.
     * @return this {@link PdfPolyGeomAnnotation} instance.
     */
    public PdfPolyGeomAnnotation setInteriorColor(float[] interiorColor) {
        return setInteriorColor(new PdfArray(interiorColor));
    }

}
