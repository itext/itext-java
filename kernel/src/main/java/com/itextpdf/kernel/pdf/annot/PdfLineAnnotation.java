/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

/**
 * The purpose of a line annotation is to display a single straight line on the page.
 * When opened, it displays a pop-up window containing the text of the associated note.
 * See also ISO-320001 12.5.6.7 "Line Annotations".
 */
public class PdfLineAnnotation extends PdfMarkupAnnotation {


    /**
     * Creates a {@link PdfLineAnnotation} instance.
     * @param rect the annotation rectangle, defining the location of the annotation on the page
     *             in default user space units. See {@link PdfAnnotation#setRectangle(PdfArray)}.
     * @param line an array of four numbers, [x1 y1 x2 y2], specifying the starting and ending coordinates
     *             of the line in default user space. See also {@link #getLine()}.
     */
	public PdfLineAnnotation(Rectangle rect, float[] line) {
        super(rect);
        put(PdfName.L, new PdfArray(line));
    }

    /**
     * Instantiates a new {@link PdfLineAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfLineAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfName getSubtype() {
        return PdfName.Line;
    }

    /**
     * An array of four numbers, [x1 y1 x2 y2], specifying the starting and ending coordinates of the line
     * in default user space. If the {@link PdfName#LL} entry is present, this value represents
     * the endpoints of the leader lines rather than the endpoints of the line itself.
     * @return An array of four numbers specifying the starting and ending coordinates of the line in default user space.
     */
    public PdfArray getLine() {
        return getPdfObject().getAsArray(PdfName.L);
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
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setBorderStyle(PdfDictionary borderStyle) {
        return (PdfLineAnnotation) put(PdfName.BS, borderStyle);
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
     * @return this {@link PdfLineAnnotation} instance.
     * @see #getBorderStyle()
     */
    public PdfLineAnnotation setBorderStyle(PdfName style) {
        return setBorderStyle(BorderStyleUtil.setStyle(getBorderStyle(), style));
    }

    /**
     * Setter for the annotation's preset dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for the annotation border style (see {@link #setBorderStyle(PdfName)}.
     * See ISO-320001 8.4.3.6, "Line Dash Pattern" for the format in which dash pattern shall be specified.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setDashPattern(PdfArray dashPattern) {
        return setBorderStyle(BorderStyleUtil.setDashPattern(getBorderStyle(), dashPattern));
    }

    /**
     * An array of two names specifying the line ending styles that is used in drawing the line.
     * The first and second elements of the array shall specify the line ending styles for the endpoints defined,
     * respectively, by the first and second pairs of coordinates, (x1, y1) and (x2, y2), in the {@link PdfName#L} array
     * (see {@link #getLine()}. For possible values see {@link #setLineEndingStyles(PdfArray)}.
     * @return An array of two names specifying the line ending styles that is used in drawing the line; or null if line
     * endings style is not explicitly defined, default value is [/None /None].
     */
    public PdfArray getLineEndingStyles() {
        return getPdfObject().getAsArray(PdfName.LE);
    }

    /**
     * Sets the line ending styles that are used in drawing the line.
     * The first and second elements of the array shall specify the line ending styles for the endpoints defined,
     * respectively, by the first and second pairs of coordinates, (x1, y1) and (x2, y2), in the {@link PdfName#L} array
     * (see {@link #getLine()}. Possible values for styles are:
     * <ul>
     *     <li>{@link PdfName#Square} - A square filled with the annotation's interior color, if any;
     *     <li>{@link PdfName#Circle} - A circle filled with the annotation's interior color, if any;
     *     <li>{@link PdfName#Diamond} - A diamond shape filled with the annotation's interior color, if any;
     *     <li>{@link PdfName#OpenArrow} - Two short lines meeting in an acute angle to form an open arrowhead;
     *     <li>{@link PdfName#ClosedArrow} - Two short lines meeting in an acute angle as in the {@link PdfName#OpenArrow} style and
     *     connected by a third line to form a triangular closed arrowhead filled with the annotation's interior color, if any;
     *     <li>{@link PdfName#None} - No line ending;
     *     <li>{@link PdfName#Butt} - A short line at the endpoint perpendicular to the line itself;
     *     <li>{@link PdfName#ROpenArrow} - Two short lines in the reverse direction from {@link PdfName#OpenArrow};
     *     <li>{@link PdfName#RClosedArrow} - A triangular closed arrowhead in the reverse direction from {@link PdfName#ClosedArrow};
     *     <li>{@link PdfName#Slash} - A short line at the endpoint approximately 30 degrees clockwise from perpendicular to the line itself;
     * </ul>
     * see also ISO-320001, Table 176 "Line ending styles".
     * @param lineEndingStyles An array of two names specifying the line ending styles that is used in drawing the line.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setLineEndingStyles(PdfArray lineEndingStyles) {
        return (PdfLineAnnotation) put(PdfName.LE, lineEndingStyles);
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
     *
     * @param interiorColor a {@link PdfArray} of numbers in the range 0.0 to 1.0. The number of array elements determines
     *                      the colour space in which the colour is defined: 0 - No colour, transparent; 1 - DeviceGray,
     *                      3 - DeviceRGB, 4 - DeviceCMYK. For the {@link PdfRedactAnnotation} number of elements shall be
     *                      equal to 3 (which defines DeviceRGB colour space).
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setInteriorColor(PdfArray interiorColor) {
        return (PdfLineAnnotation) put(PdfName.IC, interiorColor);
    }

    /**
     * An array of numbers in the range 0.0 to 1.0 specifying the interior color
     * which is used to fill the annotation's line endings.
     *
     * @param interiorColor an array of floats in the range 0.0 to 1.0.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setInteriorColor(float[] interiorColor) {
        return setInteriorColor(new PdfArray(interiorColor));
    }

    /**
     * The length of leader lines in default user space that extend from each endpoint of the line perpendicular
     * to the line itself. A positive value means that the leader lines appear in the direction that is clockwise
     * when traversing the line from its starting point to its ending point (as specified by {@link PdfName#L} (see {@link #getLine()});
     * a negative value indicates the opposite direction.
     * @return a float specifying the length of leader lines in default user space.
     */
    public float getLeaderLineLength() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LL);
        return n == null ? 0 : n.floatValue();
    }

    /**
     * Sets the length of leader lines in default user space that extend from each endpoint of the line perpendicular
     * to the line itself. A positive value means that the leader lines appear in the direction that is clockwise
     * when traversing the line from its starting point to its ending point (as specified by {@link PdfName#L} (see {@link #getLine()});
     * a negative value indicates the opposite direction.
     * @param leaderLineLength a float specifying the length of leader lines in default user space.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setLeaderLineLength(float leaderLineLength) {
        return (PdfLineAnnotation) put(PdfName.LL, new PdfNumber(leaderLineLength));
    }

    /**
     * A non-negative number that represents the length of leader line extensions that extend from the line proper
     * 180 degrees from the leader lines.
     * @return a non-negative float that represents the length of leader line extensions; or if the leader line extension
     * is not explicitly set, returns the default value, which is 0.
     */
    public float getLeaderLineExtension() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LLE);
        return n == null ? 0 : n.floatValue();
    }

    /**
     * Sets the length of leader line extensions that extend from the line proper 180 degrees from the leader lines.
     * <b>This value shall not be set unless {@link PdfName#LL} is set.</b>
     * @param leaderLineExtension a non-negative float that represents the length of leader line extensions.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setLeaderLineExtension(float leaderLineExtension) {
        return (PdfLineAnnotation) put(PdfName.LLE, new PdfNumber(leaderLineExtension));
    }

    /**
     * A non-negative number that represents the length of the leader line offset, which is the amount of empty space
     * between the endpoints of the annotation and the beginning of the leader lines.
     * @return a non-negative number that represents the length of the leader line offset,
     * or null if leader line offset is not set.
     */
    public float getLeaderLineOffset() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LLO);
        return n == null ? 0 : n.floatValue();
    }

    /**
     * Sets the length of the leader line offset, which is the amount of empty space between the endpoints of the
     * annotation and the beginning of the leader lines.
     * @param leaderLineOffset a non-negative number that represents the length of the leader line offset.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setLeaderLineOffset(float leaderLineOffset) {
        return (PdfLineAnnotation) put(PdfName.LLO, new PdfNumber(leaderLineOffset));
    }

    /**
     * If true, the text specified by the {@link PdfName#Contents} or {@link PdfName#RC} entries
     * (see {@link PdfAnnotation#getContents()} and {@link PdfMarkupAnnotation#getRichText()})
     * is replicated as a caption in the appearance of the line.
     * @return true, if the annotation text is replicated as a caption, false otherwise. If this property is
     * not set, default value is used which is <i>false</i>.
     */
    public boolean getContentsAsCaption() {
        PdfBoolean b = getPdfObject().getAsBoolean(PdfName.Cap);
        return b != null && b.getValue();
    }

    /**
     * If set to true, the text specified by the {@link PdfName#Contents} or {@link PdfName#RC} entries
     * (see {@link PdfAnnotation#getContents()} and {@link PdfMarkupAnnotation#getRichText()})
     * will be replicated as a caption in the appearance of the line.
     * @param contentsAsCaption true, if the annotation text should be replicated as a caption, false otherwise.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setContentsAsCaption(boolean contentsAsCaption) {
        return (PdfLineAnnotation) put(PdfName.Cap, PdfBoolean.valueOf(contentsAsCaption));
    }

    /**
     * A name describing the annotation's caption positioning. Valid values are {@link PdfName#Inline}, meaning the caption
     * is centered inside the line, and {@link PdfName#Top}, meaning the caption is on top of the line.
     * @return a name describing the annotation's caption positioning, or null if the caption positioning is not
     * explicitly defined (in this case the default value is used, which is {@link PdfName#Inline}).
     */
    public PdfName getCaptionPosition() {
        return getPdfObject().getAsName(PdfName.CP);
    }

    /**
     * Sets annotation's caption positioning. Valid values are {@link PdfName#Inline}, meaning the caption
     * is centered inside the line, and {@link PdfName#Top}, meaning the caption is on top of the line.
     * @param captionPosition a name describing the annotation's caption positioning.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setCaptionPosition(PdfName captionPosition) {
        return (PdfLineAnnotation) put(PdfName.CP, captionPosition);
    }

    /**
     * A measure dictionary (see ISO-320001, Table 261) that specifies the scale and units that apply to the line annotation.
     * @return a {@link PdfDictionary} that represents a measure dictionary.
     */
    public PdfDictionary getMeasure() {
        return getPdfObject().getAsDictionary(PdfName.Measure);
    }

    /**
     * Sets a measure dictionary that specifies the scale and units that apply to the line annotation.
     * @param measure a {@link PdfDictionary} that represents a measure dictionary, see ISO-320001, Table 261 for valid
     *                contents specification.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setMeasure(PdfDictionary measure) {
        return (PdfLineAnnotation) put(PdfName.Measure, measure);
    }

    /**
     * An array of two numbers that specifies the offset of the caption text from its normal position.
     * The first value is the horizontal offset along the annotation line from its midpoint, with a positive value
     * indicating offset to the right and a negative value indicating offset to the left. The second value is the vertical
     * offset perpendicular to the annotation line, with a positive value indicating a shift up and a negative value indicating
     * a shift down.
     * @return a {@link PdfArray} of two numbers that specifies the offset of the caption text from its normal position,
     * or null if caption offset is not explicitly specified (in this case a default value is used, which is [0, 0]).
     */
    public PdfArray getCaptionOffset() {
        return getPdfObject().getAsArray(PdfName.CO);
    }

    /**
     * Sets the offset of the caption text from its normal position.
     * @param captionOffset a {@link PdfArray} of two numbers that specifies the offset of the caption text from its
     *                      normal position. The first value defines the horizontal offset along the annotation line from
     *                      its midpoint, with a positive value indicating offset to the right and a negative value indicating
     *                      offset to the left. The second value defines the vertical offset perpendicular to the annotation line,
     *                      with a positive value indicating a shift up and a negative value indicating a shift down.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setCaptionOffset(PdfArray captionOffset) {
        return (PdfLineAnnotation) put(PdfName.CO, captionOffset);
    }

    /**
     * Sets the offset of the caption text from its normal position.
     * @param captionOffset an array of two floats that specifies the offset of the caption text from its
     *                      normal position. The first value defines the horizontal offset along the annotation line from
     *                      its midpoint, with a positive value indicating offset to the right and a negative value indicating
     *                      offset to the left. The second value defines the vertical offset perpendicular to the annotation line,
     *                      with a positive value indicating a shift up and a negative value indicating a shift down.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setCaptionOffset(float[] captionOffset) {
        return setCaptionOffset(new PdfArray(captionOffset));
    }

}
