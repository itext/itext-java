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
import com.itextpdf.kernel.pdf.PdfObject;

public class PdfTextMarkupAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = 2189266742204503217L;
	/**
     * Subtypes
     */
    public static final PdfName MarkupHighlight = PdfName.Highlight;
    public static final PdfName MarkupUnderline = PdfName.Underline;
    public static final PdfName MarkupStrikeout = PdfName.StrikeOut;
    public static final PdfName MarkupSquiggly = PdfName.Squiggly;

    public PdfTextMarkupAnnotation(Rectangle rect, PdfName subtype, float[] quadPoints) {
        super(rect);
        put(PdfName.Subtype, subtype);
        setQuadPoints(new PdfArray(quadPoints));
    }

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfTextMarkupAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a text markup annotation of highlight style subtype.
     * <p>
     * IMPORTANT NOTE on <b>quadPoints</b> argument:
     * According to Table 179 in ISO 32000-1, the QuadPoints array lists the vertices in counterclockwise
     * order and the text orientation is defined by the first and second vertex. This basically means QuadPoints is
     * specified as lower-left, lower-right, top-right, top-left. HOWEVER, Adobe's interpretation
     * (tested at least with Acrobat 10, Acrobat 11, Reader 11) is top-left, top-right, lower-left, lower-right (Z-shaped order).
     * This means that if the QuadPoints array is specified according to the standard, the rendering is not as expected.
     * Other viewers seem to follow Adobe's interpretation. Hence we recommend to use and expect QuadPoints array in Z-order,
     * just as Acrobat and probably most other viewers expect.
     * @param rect the annotation rectangle, defining the location of the annotation on the page
     *             in default user space units.
     * @param quadPoints An array of 8 × n numbers specifying the coordinates of n quadrilaterals in default user space.
     *                   Each quadrilateral shall encompasses a word or group of contiguous words in the text underlying
     *                   the annotation. The text is oriented with respect to the edge connecting first two vertices.
     * @return created {@link PdfTextMarkupAnnotation} of Highlight type.
     */
    public static PdfTextMarkupAnnotation createHighLight(Rectangle rect, float[] quadPoints) {
        return new PdfTextMarkupAnnotation(rect, MarkupHighlight, quadPoints);
    }

    /**
     * Creates a text markup annotation of underline style subtype.
     * <p>
     * IMPORTANT NOTE on <b>quadPoints</b> argument:
     * According to Table 179 in ISO 32000-1, the QuadPoints array lists the vertices in counterclockwise
     * order and the text orientation is defined by the first and second vertex. This basically means QuadPoints is
     * specified as lower-left, lower-right, top-right, top-left. HOWEVER, Adobe's interpretation
     * (tested at least with Acrobat 10, Acrobat 11, Reader 11) is top-left, top-right, lower-left, lower-right (Z-shaped order).
     * This means that if the QuadPoints array is specified according to the standard, the rendering is not as expected.
     * Other viewers seem to follow Adobe's interpretation. Hence we recommend to use and expect QuadPoints array in Z-order,
     * just as Acrobat and probably most other viewers expect.
     * @param rect the annotation rectangle, defining the location of the annotation on the page
     *             in default user space units.
     * @param quadPoints An array of 8 × n numbers specifying the coordinates of n quadrilaterals in default user space.
     *                   Each quadrilateral shall encompasses a word or group of contiguous words in the text underlying
     *                   the annotation. The text is oriented with respect to the edge connecting first two vertices.
     * @return created {@link PdfTextMarkupAnnotation} of Underline type.
     */
    public static PdfTextMarkupAnnotation createUnderline(Rectangle rect, float[] quadPoints) {
        return new PdfTextMarkupAnnotation(rect, MarkupUnderline, quadPoints);
    }

    /**
     * Creates a text markup annotation of strikeout style subtype.
     * <p>
     * IMPORTANT NOTE on <b>quadPoints</b> argument:
     * According to Table 179 in ISO 32000-1, the QuadPoints array lists the vertices in counterclockwise
     * order and the text orientation is defined by the first and second vertex. This basically means QuadPoints is
     * specified as lower-left, lower-right, top-right, top-left. HOWEVER, Adobe's interpretation
     * (tested at least with Acrobat 10, Acrobat 11, Reader 11) is top-left, top-right, lower-left, lower-right (Z-shaped order).
     * This means that if the QuadPoints array is specified according to the standard, the rendering is not as expected.
     * Other viewers seem to follow Adobe's interpretation. Hence we recommend to use and expect QuadPoints array in Z-order,
     * just as Acrobat and probably most other viewers expect.
     * @param rect the annotation rectangle, defining the location of the annotation on the page
     *             in default user space units.
     * @param quadPoints An array of 8 × n numbers specifying the coordinates of n quadrilaterals in default user space.
     *                   Each quadrilateral shall encompasses a word or group of contiguous words in the text underlying
     *                   the annotation. The text is oriented with respect to the edge connecting first two vertices.
     * @return created {@link PdfTextMarkupAnnotation} of Strikeout type.
     */
    public static PdfTextMarkupAnnotation createStrikeout(Rectangle rect, float[] quadPoints) {
        return new PdfTextMarkupAnnotation(rect, MarkupStrikeout, quadPoints);
    }

    /**
     * Creates a text markup annotation of squiggly-underline type.
     * <p>
     * IMPORTANT NOTE on <b>quadPoints</b> argument:
     * According to Table 179 in ISO 32000-1, the QuadPoints array lists the vertices in counterclockwise
     * order and the text orientation is defined by the first and second vertex. This basically means QuadPoints is
     * specified as lower-left, lower-right, top-right, top-left. HOWEVER, Adobe's interpretation
     * (tested at least with Acrobat 10, Acrobat 11, Reader 11) is top-left, top-right, lower-left, lower-right (Z-shaped order).
     * This means that if the QuadPoints array is specified according to the standard, the rendering is not as expected.
     * Other viewers seem to follow Adobe's interpretation. Hence we recommend to use and expect QuadPoints array in Z-order,
     * just as Acrobat and probably most other viewers expect.
     * @param rect the annotation rectangle, defining the location of the annotation on the page
     *             in default user space units.
     * @param quadPoints An array of 8 × n numbers specifying the coordinates of n quadrilaterals in default user space.
     *                   Each quadrilateral shall encompasses a word or group of contiguous words in the text underlying
     *                   the annotation. The text is oriented with respect to the edge connecting first two vertices.
     * @return created {@link PdfTextMarkupAnnotation} of squiggly-underline type.
     */
    public static PdfTextMarkupAnnotation createSquiggly(Rectangle rect, float[] quadPoints) {
        return new PdfTextMarkupAnnotation(rect, MarkupSquiggly, quadPoints);
    }

    @Override
    public PdfName getSubtype() {
        PdfName subType = getPdfObject().getAsName(PdfName.Subtype);
        if (subType == null) {
            subType = PdfName.Underline;
        }
        return subType;
    }

    /**
     * An array of 8 × n numbers specifying the coordinates of n quadrilaterals in default user space.
     * Quadrilaterals are used to define a word or group of contiguous words in the text
     * underlying the text markup annotation.
     *
     * <p>
     * IMPORTANT NOTE: According to Table 179 in ISO 32000-1, the QuadPoints array lists the vertices in counterclockwise
     * order and the text orientation is defined by the first and second vertex. This basically means QuadPoints is
     * specified as lower-left, lower-right, top-right, top-left. HOWEVER, Adobe's interpretation
     * (tested at least with Acrobat 10, Acrobat 11, Reader 11) is top-left, top-right, lower-left, lower-right (Z-shaped order).
     * This means that if the QuadPoints array is specified according to the standard, the rendering is not as expected.
     * Other viewers seem to follow Adobe's interpretation. Hence we recommend to use and expect QuadPoints array in Z-order,
     * just as Acrobat and probably most other viewers expect.
     * @return an {@link PdfArray} of 8 × n numbers specifying the coordinates of n quadrilaterals.
     */
    public PdfArray getQuadPoints() {
        return getPdfObject().getAsArray(PdfName.QuadPoints);
    }

    /**
     * Sets n quadrilaterals in default user space by passing an {@link PdfArray} of 8 × n numbers.
     * Quadrilaterals are used to define a word or group of contiguous words in the text
     * underlying the text markup annotation.
     *
     * <p>
     * IMPORTANT NOTE: According to Table 179 in ISO 32000-1, the QuadPoints array lists the vertices in counterclockwise
     * order and the text orientation is defined by the first and second vertex. This basically means QuadPoints is
     * specified as lower-left, lower-right, top-right, top-left. HOWEVER, Adobe's interpretation
     * (tested at least with Acrobat 10, Acrobat 11, Reader 11) is top-left, top-right, lower-left, lower-right (Z-shaped order).
     * This means that if the QuadPoints array is specified according to the standard, the rendering is not as expected.
     * Other viewers seem to follow Adobe's interpretation. Hence we recommend to use and expect QuadPoints array in Z-order,
     * just as Acrobat and probably most other viewers expect.
     * @param quadPoints an {@link PdfArray} of 8 × n numbers specifying the coordinates of n quadrilaterals.
     * @return this {@link PdfTextMarkupAnnotation} instance.
     */
    public PdfTextMarkupAnnotation setQuadPoints(PdfArray quadPoints) {
        return (PdfTextMarkupAnnotation) put(PdfName.QuadPoints, quadPoints);
    }
}
