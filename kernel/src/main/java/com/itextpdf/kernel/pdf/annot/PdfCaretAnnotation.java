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
import com.itextpdf.kernel.pdf.PdfString;

public class PdfCaretAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = 1542932123958535397L;

	public PdfCaretAnnotation(Rectangle rect) {
        super(rect);
    }

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfCaretAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Caret;
    }

    public PdfCaretAnnotation setSymbol(PdfString symbol) {
        return (PdfCaretAnnotation) put(PdfName.Sy, symbol);
    }

    public PdfString getSymbol() {
        return getPdfObject().getAsString(PdfName.Sy);
    }

    /**
     * A set of four numbers describing the numerical differences between two rectangles:
     * the Rect entry of the annotation and the actual boundaries of the underlying caret.
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
     * the Rect entry of the annotation and the actual boundaries of the underlying caret.
     *
     * @param rect a {@link PdfArray} with four numbers which correspond to the differences in default user space between
     *             the left, top, right, and bottom coordinates of Rect and those of the inner rectangle, respectively.
     *             Each value shall be greater than or equal to 0. The sum of the top and bottom differences shall be
     *             less than the height of Rect, and the sum of the left and right differences shall be less than
     *             the width of Rect.
     * @return this {@link PdfCaretAnnotation} instance.
     */
    public PdfCaretAnnotation setRectangleDifferences(PdfArray rect) {
        return (PdfCaretAnnotation) put(PdfName.RD, rect);
    }
}
