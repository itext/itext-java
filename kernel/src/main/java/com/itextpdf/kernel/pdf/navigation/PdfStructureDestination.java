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

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.IPdfNameTreeAccess;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.List;

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
    public PdfObject getDestinationPage(IPdfNameTreeAccess names) {
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
            throw new PdfException(
                    KernelExceptionMessageConstant.STRUCTURE_ELEMENT_IN_STRUCTURE_DESTINATION_SHALL_BE_AN_INDIRECT_OBJECT);
        }
        ((PdfArray)getPdfObject()).add(elem.getPdfObject());
        return this;
    }

    private PdfStructureDestination add(PdfName type) {
        ((PdfArray)getPdfObject()).add(type);
        return this;
    }

}
