/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import org.slf4j.LoggerFactory;

public class PdfPolyGeomAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -9038993253308315792L;
    
	/**
     * Subtypes
     */
    public static final PdfName Polygon = PdfName.Polygon;
    public static final PdfName PolyLine = PdfName.PolyLine;

    public PdfPolyGeomAnnotation(Rectangle rect, PdfName subtype, float[] vertices) {
        super(rect);
        setSubtype(subtype);
        setVertices(vertices);
    }

    public PdfPolyGeomAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public static PdfPolyGeomAnnotation createPolygon(Rectangle rect, float[] vertices) {
        return new PdfPolyGeomAnnotation(rect, Polygon, vertices);
    }

    public static PdfPolyGeomAnnotation createPolyLine(Rectangle rect, float[] vertices) {
        return new PdfPolyGeomAnnotation(rect, PolyLine, vertices);
    }

    @Override
    public PdfName getSubtype() {
        return getPdfObject().getAsName(PdfName.Subtype);
    }

    public PdfArray getVertices() {
        return getPdfObject().getAsArray(PdfName.Vertices);
    }

    public PdfPolyGeomAnnotation setVertices(PdfArray vertices) {
        if (getPdfObject().containsKey(PdfName.Path)) {
            LoggerFactory.getLogger(getClass()).warn(LogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED);
        }
        return (PdfPolyGeomAnnotation) put(PdfName.Vertices, vertices);
    }

    public PdfPolyGeomAnnotation setVertices(float[] vertices) {
        if (getPdfObject().containsKey(PdfName.Path)) {
            LoggerFactory.getLogger(getClass()).warn(LogMessageConstant.PATH_KEY_IS_PRESENT_VERTICES_WILL_BE_IGNORED);
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
            LoggerFactory.getLogger(getClass()).error(LogMessageConstant.IF_PATH_IS_SET_VERTICES_SHALL_NOT_BE_PRESENT);
        }
        return (PdfPolyGeomAnnotation) put(PdfName.Path, path);
    }

    private void setSubtype(PdfName subtype) {
        put(PdfName.Subtype, subtype);
    }

}
