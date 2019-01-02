/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.List;
import java.util.Map;

public class PdfStructureDestination extends PdfDestination {

    public PdfStructureDestination(PdfArray structureDestination) {
        super(structureDestination);
    }

    private PdfStructureDestination() {
        super(new PdfArray());
    }

    public static PdfStructureDestination createXYZ(PdfStructElem elem, float left, float top, float zoom) {
        return create(elem, PdfName.XYZ, left, Float.NaN, Float.NaN, top, zoom);
    }

    public static PdfStructureDestination createFit(PdfStructElem elem) {
        return create(elem, PdfName.Fit, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfStructureDestination createFitH(PdfStructElem elem, float top) {
        return create(elem, PdfName.FitH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfStructureDestination createFitV(PdfStructElem elem, float left) {
        return create(elem, PdfName.FitV, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfStructureDestination createFitR(PdfStructElem elem, float left, float bottom, float right, float top) {
        return create(elem, PdfName.FitR, left, bottom, right, top, Float.NaN);
    }

    public static PdfStructureDestination createFitB(PdfStructElem elem) {
        return create(elem, PdfName.FitB, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    public static PdfStructureDestination createFitBH(PdfStructElem elem, float top) {
        return create(elem, PdfName.FitBH, Float.NaN, Float.NaN, Float.NaN, top, Float.NaN);
    }

    public static PdfStructureDestination createFitBV(PdfStructElem elem, float left) {
        return create(elem, PdfName.FitBH, left, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }

    private static PdfStructureDestination create(PdfStructElem elem, PdfName type, float left, float bottom, float right, float top, float zoom) {
        return new PdfStructureDestination().add(elem).add(type).add(left).add(bottom).add(right).add(top).add(zoom);
    }

    @Override
    public PdfObject getDestinationPage(Map<String, PdfObject> names) {
        PdfObject firstObj = ((PdfArray)getPdfObject()).get(0);
        if (firstObj.isDictionary()) {
                PdfStructElem structElem = new PdfStructElem((PdfDictionary)firstObj);
                while (true) {
                    List<IStructureNode> kids = structElem.getKids();
                    IStructureNode firstKid = kids.size() > 0 ? kids.get(0) : null;
                    if (firstKid instanceof PdfMcr) {
                        return ((PdfMcr) firstKid).getPageObject();
                    } else if (firstKid instanceof PdfStructElem) {
                        structElem = (PdfStructElem) firstKid;
                    } else {
                        break;
                    }
            }
        }
        return null;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

    private PdfStructureDestination add(float value) {
        if (!Float.isNaN(value)) {
            ((PdfArray) getPdfObject()).add(new PdfNumber(value));
        }
        return this;
    }

    private PdfStructureDestination add(PdfStructElem elem) {
        if (elem.getPdfObject().getIndirectReference() == null) {
            throw new PdfException(PdfException.StructureElementInStructureDestinationShallBeAnIndirectObject);
        }
        ((PdfArray)getPdfObject()).add(elem.getPdfObject());
        return this;
    }

    private PdfStructureDestination add(PdfName type) {
        ((PdfArray)getPdfObject()).add(type);
        return this;
    }

}
