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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

public class PdfTransparencyGroup extends PdfObjectWrapper<PdfDictionary> {


	public PdfTransparencyGroup() {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.S, PdfName.Transparency);
    }

    /**
     * Determining the initial backdrop against which its stack is composited.
     *
     * @param isolated defines whether the {@link PdfName#I} flag will be set or removed
     */
    public void setIsolated(boolean isolated) {
        if (isolated) {
            getPdfObject().put(PdfName.I, PdfBoolean.TRUE);
        } else {
            getPdfObject().remove(PdfName.I);
        }
    }

    /**
     * Determining whether the objects within the stack are composited with one another or only with the group's backdrop.
     *
     * @param knockout defines whether the {@link PdfName#K} flag will be set or removed
     */
    public void setKnockout(boolean knockout) {
        if (knockout) {
            getPdfObject().put(PdfName.K, PdfBoolean.TRUE);
        } else {
            getPdfObject().remove(PdfName.K);
        }
    }

    public void setColorSpace(PdfName colorSpace) {
        getPdfObject().put(PdfName.CS, colorSpace);
    }

    public void setColorSpace(PdfArray colorSpace) {
        getPdfObject().put(PdfName.CS, colorSpace);
    }

    public PdfTransparencyGroup put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
