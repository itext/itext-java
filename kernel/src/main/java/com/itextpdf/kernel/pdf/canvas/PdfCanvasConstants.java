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
package com.itextpdf.kernel.pdf.canvas;

/**
 * A container for constants defined in the PDF specification (ISO 32000-1).
 */
public class PdfCanvasConstants {

    /**
     * The text rendering mode determines whether showing text causes glyph
     * outlines to be stroked, filled, used as a clipping boundary, or some
     * combination of the three. Stroking, filling, and clipping have the same
     * effects for a text object as they do for a path object, although they are
     * specified in an entirely different way.
     * 
     * If the text rendering mode calls for filling, the current nonstroking
     * color in the graphics state is used; if it calls for stroking, the
     * current stroking color is used.
     * 
     * All documentation for this class is taken from ISO 32000-1, section 9.3.6
     * "Text Rendering Mode".
     */
    public static final class TextRenderingMode {
        private TextRenderingMode() {}

        /** Fill text */
        public static final int FILL = 0;
        /** Stroke text, providing the outline of the glyphs */
        public static final int STROKE = 1;
        /** Fill and stroke text */
        public static final int FILL_STROKE = 2;
        /** Neither fill nor stroke, i.e. render invisibly */
        public static final int INVISIBLE = 3;
        /** Fill text and add to path for clipping */
        public static final int FILL_CLIP = 4;
        /** Stroke text and add to path for clipping */
        public static final int STROKE_CLIP = 5;
        /** Fill, then stroke text and add to path for clipping */
        public static final int FILL_STROKE_CLIP = 6;
        /** Add text to path for clipping */
        public static final int CLIP = 7;
    }

    /**
     * The line cap style specifies the shape to be used at the ends of open
     * subpaths (and dashes, if any) when they are stroked.
     * 
     * All documentation for this class is taken from ISO 32000-1, section
     * 8.4.3.3 "Line Cap Style".
     */
    public static class LineCapStyle {
        /**
         * The stroke is squared of at the endpoint of the path. There is no
         * projection beyond the end of the path.
         */
        public static final int BUTT = 0;
        /**
         * A semicircular arc with a diameter equal to the line width is drawn
         * around the endpoint and filled in.
         */
        public static final int ROUND = 1;
        /**
         * The stroke continues beyond the endpoint of the path for a distance
         * equal to half the line width and is squared off.
         */
        public static final int PROJECTING_SQUARE = 2;
    }

    /**
     * The line join style specifies the shape to be used at the corners of
     * paths that are stroked. Join styles are significant only at points where
     * consecutive segments of a path connect at an angle; segments that meet or
     * intersect fortuitously receive no special treatment.
     * 
     * All documentation for this class is taken from ISO 32000-1, section
     * 8.4.3.4 "Line Join Style".
     */
    public static class LineJoinStyle {
        /**
         * The outer edges of the strokes for the two segments are extended
         * until they meet at an angle, as in a picture frame. If the segments
         * meet at too sharp an angle, a bevel join is used instead.
         */
        public static final int MITER = 0;
        /**
         * An arc of a circle with a diameter equal to the line width is drawn
         * around the point where the two segments meet, connecting the outer
         * edges of the strokes for the two segments. This pieslice-shaped
         * figure is filled in, producing a rounded corner.
         */
        public static final int ROUND = 1;
        /**
         * The two segments are finished with butt caps (@see LineCapStyle#BUTT)
         * and the resulting notch beyond the ends of the segments is filled
         * with a triangle.
         */
        public static final int BEVEL = 2;
    }

    public static class FillingRule {

        public static final int NONZERO_WINDING = 1;

        public static final int EVEN_ODD = 2;
    }
}
