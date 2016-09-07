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
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

/**
 * The purpose of a line annotation is to display a single straight line on the page.
 * When opened, it displays a pop-up window containing the text of the associated note.
 * See also ISO-320001 12.5.6.7 "Line Annotations".
 */
public class PdfLineAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -6047928061827404283L;

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
     * Creates a {@link PdfLineAnnotation} instance from the given {@link PdfDictionary}
     * that represents annotation object. This method is useful for property reading in reading mode or
     * modifying in stamping mode.
     * @param pdfDictionary a {@link PdfDictionary} that represents existing annotation in the document.
     */
    public PdfLineAnnotation(PdfDictionary pdfDictionary) {
        super(pdfDictionary);
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
     * in default user space. If the {@link PdfName#LL} (see {@link #getLeaderLine()}) entry is present, this value represents
     * the endpoints of the leader lines rather than the endpoints of the line itself.
     * @return An array of four numbers specifying the starting and ending coordinates of the line in default user space.
     */
    public PdfArray getLine() {
        return getPdfObject().getAsArray(PdfName.L);
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
     *     <li>{@link PdfName#Square} - A square filled with the annotation's interior color, if any; </li>
     *     <li>{@link PdfName#Circle} - A circle filled with the annotation's interior color, if any; </li>
     *     <li>{@link PdfName#Diamond} - A diamond shape filled with the annotation's interior color, if any; </li>
     *     <li>{@link PdfName#OpenArrow} - Two short lines meeting in an acute angle to form an open arrowhead; </li>
     *     <li>{@link PdfName#ClosedArrow} - Two short lines meeting in an acute angle as in the {@link PdfName#OpenArrow} style and
     *     connected by a third line to form a triangular closed arrowhead filled with the annotation's interior color, if any; </li>
     *     <li>{@link PdfName#None} - No line ending; </li>
     *     <li>{@link PdfName#Butt} - A short line at the endpoint perpendicular to the line itself; </li>
     *     <li>{@link PdfName#ROpenArrow} - Two short lines in the reverse direction from {@link PdfName#OpenArrow}; </li>
     *     <li>{@link PdfName#RClosedArrow} - A triangular closed arrowhead in the reverse direction from {@link PdfName#ClosedArrow}; </li>
     *     <li>{@link PdfName#Slash} - A short line at the endpoint approximately 30 degrees clockwise from perpendicular to the line itself; </li>
     * </ul>
     * see also ISO-320001, Table 176 "Line ending styles".
     * @param lineEndingStyles An array of two names specifying the line ending styles that is used in drawing the line.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setLineEndingStyles(PdfArray lineEndingStyles) {
        return (PdfLineAnnotation) put(PdfName.LE, lineEndingStyles);
    }

    /**
     * The length of leader lines in default user space that extend from each endpoint of the line perpendicular
     * to the line itself. A positive value means that the leader lines appear in the direction that is clockwise
     * when traversing the line from its starting point to its ending point (as specified by {@link PdfName#L} (see {@link #getLine()});
     * a negative value indicates the opposite direction.
     * @return a float specifying the length of leader lines in default user space.
     */
    public float getLeaderLine() {
        PdfNumber n = getPdfObject().getAsNumber(PdfName.LL);
        return n == null ? 0 : n.floatValue();
    }

    /**
     * Sets the length of leader lines in default user space that extend from each endpoint of the line perpendicular
     * to the line itself. A positive value means that the leader lines appear in the direction that is clockwise
     * when traversing the line from its starting point to its ending point (as specified by {@link PdfName#L} (see {@link #getLine()});
     * a negative value indicates the opposite direction.
     * @param leaderLine a float specifying the length of leader lines in default user space.
     * @return this {@link PdfLineAnnotation} instance.
     */
    public PdfLineAnnotation setLeaderLine(float leaderLine) {
        return (PdfLineAnnotation) put(PdfName.LL, new PdfNumber(leaderLine));
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
     * <b>This value shall not be set unless {@link PdfName#LL} (see {@link #setLeaderLine(float)}) is set.</b>
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
        return (PdfLineAnnotation) put(PdfName.Cap, new PdfBoolean(contentsAsCaption));
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
