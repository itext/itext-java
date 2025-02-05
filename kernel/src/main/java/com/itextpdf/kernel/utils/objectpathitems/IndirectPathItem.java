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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.kernel.pdf.PdfIndirectReference;

/**
 * An item in the indirect path (see {@link ObjectPath}. It encapsulates two corresponding objects from the two
 * comparing documents that were met to get to the path base objects during comparing process.
 */
public final class IndirectPathItem {
    private final PdfIndirectReference cmpObject;
    private final PdfIndirectReference outObject;

    /**
     * Creates {@link IndirectPathItem} instance for two corresponding objects from two comparing documents.
     *
     * @param cmpObject an object from the cmp document.
     * @param outObject an object from the out document.
     */
    public IndirectPathItem(PdfIndirectReference cmpObject, PdfIndirectReference outObject) {
        this.cmpObject = cmpObject;
        this.outObject = outObject;
    }

    /**
     * Method returns a {@link IndirectPathItem} object from the cmp object that was met to get
     * to the path base objects during comparing process.
     *
     * @return a {@link IndirectPathItem} object from the cmp object.
     */
    public PdfIndirectReference getCmpObject() {
        return cmpObject;
    }

    /**
     * Method returns a {@link IndirectPathItem} object that was met to get to the path base
     * objects during comparing process.
     *
     * @return an object from the out object
     */
    public PdfIndirectReference getOutObject() {
        return outObject;
    }

    @Override
    public int hashCode() {
        return cmpObject.hashCode() * 31 + outObject.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && (obj.getClass() == getClass() && cmpObject.equals(((IndirectPathItem) obj).cmpObject)
                && outObject.equals(((IndirectPathItem) obj).outObject));
    }
}
