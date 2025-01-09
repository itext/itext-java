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
package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.pdf.IPdfNameTreeAccess;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;

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
    public PdfObject getDestinationPage(IPdfNameTreeAccess names) {
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
