/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;

import java.util.Map;

/**
 * This class shall be used for creation of destinations, associated with outline items, annotations
 * or actions within current document.
 * If you need to create a destination, associated with an object in another PDF
 * (e.g. Remote Go-To actions or Embedded Go-To actions), you should use {@link PdfExplicitRemoteGoToDestination} class instead.
 * Note that despite methods with integer value for page parameter are deprecated in this class,
 * Adobe Acrobat handles such destinations correctly, but removes them completely from a PDF,
 * when it is saved as an optimized pdf with the "discard-invalid-links" option.
 * Therefore it is strongly recommended to use methods accepting pdfPage instance, if the destination is inside of the current document.
 */
public class PdfExplicitDestination extends PdfDestination {


	public PdfExplicitDestination() {
        this(new PdfArray());
    }

    public PdfExplicitDestination(PdfArray pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfObject getDestinationPage(Map<String, PdfObject> names) {
        return ((PdfArray)getPdfObject()).get(0);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified by the factor zoom and positioned at the upper-left corner of the window.
     *
     * @param page the destination page
     * @param left the X coordinate of the left edge of the destination rectangle
     * @param top the Y coordinate of the upper edge of the destination rectangle
     * @param zoom zoom factor
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createXYZ(PdfPage page, float left, float top, float zoom) {
        return create(page, PdfName.XYZ, left, Float.NaN, Float.NaN, top, zoom);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified just enough to fit the entire page within the window both horizontally and vertically.
     *
     * @param page the destination page
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createFit(PdfPage page) {
        return create(page, PdfName.Fit, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified just enough to fit the entire width of the page within the window.
     *
     * @param page the destination page
     * @param top the Y coordinate of the upper edge of the destination rectangle
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createFitH(PdfPage page, float top) {
        return create(page, PdfName.FitH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified just enough to fit the entire height of the page within the window.
     *
     * @param page the destination page
     * @param left the X coordinate of the left edge of the destination rectangle
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createFitV(PdfPage page, float left) {
        return create(page, PdfName.FitV, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified just enough to fit the rectangle specified by the coordinates left, bottom, right, and top
     * entirely within the window both horizontally and vertically.
     *
     * @param page the destination page
     * @param left the X coordinate of the left edge of the destination rectangle
     * @param bottom the Y coordinate of the lower edge of the destination rectangle
     * @param right the X coordinate of the right edge of the destination rectangle
     * @param top the Y coordinate of the upper edge of the destination rectangle
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createFitR(PdfPage page, float left, float bottom, float right, float top) {
        return create(page, PdfName.FitR, left, bottom, right, top, Float.NaN);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified just enough to fit its bounding box entirely within the window both horizontally and vertically.
     *
     * @param page the destination page
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createFitB(PdfPage page) {
        return create(page, PdfName.FitB, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified just enough to fit the entire width of its bounding box within the window.
     *
     * @param page the destination page
     * @param top the Y coordinate of the upper edge of the destination rectangle
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createFitBH(PdfPage page, float top) {
        return create(page, PdfName.FitBH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    /**
     * Creates {@link PdfExplicitDestination}. The designated page will be displayed with its contents
     * magnified just enough to fit the entire height of its bounding box within the window.
     *
     * @param page the destination page
     * @param left the X coordinate of the left edge of the destination rectangle
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination createFitBV(PdfPage page, float left) {
        return create(page, PdfName.FitBV, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * Creates a {@link PdfExplicitDestination} associated with an object inside current PDF document.
     *
     * @param page the destination page
     * @param type a {@link PdfName} specifying one of the possible ways to define the area to be displayed.
     *            See ISO 32000-1, section 12.3.2.2 "Explicit Destinations", Table 151 – Destination syntax
     * @param left the X coordinate of the left edge of the destination rectangle
     * @param bottom the Y coordinate of the lower edge of the destination rectangle
     * @param right the X coordinate of the right edge of the destination rectangle
     * @param top the Y coordinate of the upper edge of the destination rectangle
     * @param zoom zoom factor
     * @return newly created {@link PdfExplicitDestination}
     */
    public static PdfExplicitDestination create(PdfPage page, PdfName type, float left, float bottom, float right, float top, float zoom) {
        return new PdfExplicitDestination().add(page).add(type).add(left).add(bottom).add(right).add(top).add(zoom);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

    private PdfExplicitDestination add(float value) {
        if (!Float.isNaN(value)) {
            ((PdfArray) getPdfObject()).add(new PdfNumber(value));
        }
        return this;
    }

    private PdfExplicitDestination add(int value) {
        ((PdfArray)getPdfObject()).add(new PdfNumber(value));
        return this;
    }

    private PdfExplicitDestination add(PdfPage page) {
        // Explicitly using object indirect reference here in order to correctly process released objects.
        ((PdfArray)getPdfObject()).add(page.getPdfObject().getIndirectReference());
        return this;
    }

    private PdfExplicitDestination add(PdfName type) {
        ((PdfArray)getPdfObject()).add(type);
        return this;
    }

}
