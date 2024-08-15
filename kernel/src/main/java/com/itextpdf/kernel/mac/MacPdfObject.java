/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.mac;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

class MacPdfObject extends PdfObjectWrapper<PdfDictionary> {
    MacPdfObject(int macContainerSize) {
        super(new PdfDictionary());
        PdfLiteral macPlaceholder = new PdfLiteral(macContainerSize);
        PdfLiteral byteRangePlaceholder = new PdfLiteral(80);

        getPdfObject().put(new PdfName("MACLocation"), new PdfName("Standalone"));
        getPdfObject().put(new PdfName("MAC"), macPlaceholder);
        getPdfObject().put(PdfName.ByteRange, byteRangePlaceholder);
    }

    long[] computeByteRange(long totalLength) {
        PdfLiteral macPlaceholder = getMacPlaceholder();
        long macStart = macPlaceholder.getPosition();
        long macLength = macPlaceholder.getBytesCount();
        long macEnd = macStart + macLength;
        return new long[] {0, macStart, macEnd, totalLength - macEnd};
    }

    long getByteRangePosition() {
        return getByteRangePlaceholder().getPosition();
    }

    private PdfLiteral getMacPlaceholder() {
        PdfObject mac = getPdfObject().get(new PdfName("MAC"));
        return (PdfLiteral) mac;
    }

    private PdfLiteral getByteRangePlaceholder() {
        PdfObject br = getPdfObject().get(PdfName.ByteRange);
        return (PdfLiteral) br;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
