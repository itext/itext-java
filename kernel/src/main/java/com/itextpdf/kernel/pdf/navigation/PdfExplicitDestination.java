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

    private static final long serialVersionUID = -1515785642472963298L;

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

    public static PdfExplicitDestination createXYZ(PdfPage page, float left, float top, float zoom) {
        return create(page, PdfName.XYZ, left, Float.NaN, Float.NaN, top, zoom);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createXYZ(int, float, float, float)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createXYZ(int pageNum, float left, float top, float zoom) {
        return create(pageNum, PdfName.XYZ, left, Float.NaN, Float.NaN, top, zoom);
    }

    public static PdfExplicitDestination createFit(PdfPage page) {
        return create(page, PdfName.Fit, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createFit(int)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createFit(int pageNum) {
        return create(pageNum, PdfName.Fit, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitDestination createFitH(PdfPage page, float top) {
        return create(page, PdfName.FitH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createFitH(int, float)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createFitH(int pageNum, float top) {
        return create(pageNum, PdfName.FitH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfExplicitDestination createFitV(PdfPage page, float left) {
        return create(page, PdfName.FitV, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createFitV(int, float)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createFitV(int pageNum, float left) {
        return create(pageNum, PdfName.FitV, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitDestination createFitR(PdfPage page, float left, float bottom, float right, float top) {
        return create(page, PdfName.FitR, left, bottom, right, top, Float.NaN);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createFitR(int, float, float, float, float)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createFitR(int pageNum, float left, float bottom, float right, float top) {
        return create(pageNum, PdfName.FitR, left, bottom, right, top, Float.NaN);
    }

    public static PdfExplicitDestination createFitB(PdfPage page) {
        return create(page, PdfName.FitB, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createFitB(int)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createFitB(int pageNum) {
        return create(pageNum, PdfName.FitB, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitDestination createFitBH(PdfPage page, float top) {
        return create(page, PdfName.FitBH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createFitBH(int, float)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createFitBH(int pageNum, float top) {
        return create(pageNum, PdfName.FitBH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfExplicitDestination createFitBV(PdfPage page, float left) {
        return create(page, PdfName.FitBH, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#createFitBV(int, float)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination createFitBV(int pageNum, float left) {
        return create(pageNum, PdfName.FitBH, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfExplicitDestination create(PdfPage page, PdfName type, float left, float bottom, float right, float top, float zoom) {
        return new PdfExplicitDestination().add(page).add(type).add(left).add(bottom).add(right).add(top).add(zoom);
    }

    /**
     * @deprecated Use {@link PdfExplicitRemoteGoToDestination#create(int, PdfName, float, float, float, float, float)} instead.
     */
    @Deprecated
    public static PdfExplicitDestination create(int pageNum, PdfName type, float left, float bottom, float right, float top, float zoom) {
        return new PdfExplicitDestination().add(--pageNum).add(type).add(left).add(bottom).add(right).add(top).add(zoom);
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
        ((PdfArray)getPdfObject()).add(page.getPdfObject());
        return this;
    }

    private PdfExplicitDestination add(PdfName type) {
        ((PdfArray)getPdfObject()).add(type);
        return this;
    }

}
