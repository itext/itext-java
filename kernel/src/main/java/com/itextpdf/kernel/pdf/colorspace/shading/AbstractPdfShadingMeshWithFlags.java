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
package com.itextpdf.kernel.pdf.colorspace.shading;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

/**
 * The PdfShadingMeshFlags class which extends {@link AbstractPdfShading} and {@link AbstractPdfShadingMesh}
 * and represents shadings which are based on a mesh, with all fields from {@link AbstractPdfShadingMesh}
 * as well as BitsPerFlag in the PDF object.
 */
public abstract class AbstractPdfShadingMeshWithFlags extends AbstractPdfShadingMesh {

    /**
     * Gets the number of bits used to represent the edge flag for each vertex.
     * But only the least significant 2 bits in each flag value shall be used.
     * The valid flag values are 0, 1, 2 or 3.
     *
     * @return the number of bits. Can be 2, 4 or 8
     */
    public int getBitsPerFlag() {
        return (int) getPdfObject().getAsInt(PdfName.BitsPerFlag);
    }

    /**
     * Sets the number of bits used to represent the edge flag for each vertex.
     * But only the least significant 2 bits in each flag value shall be used.
     * The valid flag values are 0, 1, 2 or 3.
     *
     * @param bitsPerFlag the number of bits to be set. Shall be 2, 4 or 8
     */
    public final void setBitsPerFlag(int bitsPerFlag) {
        getPdfObject().put(PdfName.BitsPerFlag, new PdfNumber(bitsPerFlag));
        setModified();
    }

    /**
     * Constructor for PdfShadingBlend object using a PdfDictionary.
     *
     * @param pdfObject input PdfDictionary
     */
    protected AbstractPdfShadingMeshWithFlags(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructor for PdfShadingBlend object using a PdfDictionary, shading type and color space.
     *
     * @param pdfObject input PdfDictionary
     * @param type shading type
     * @param colorSpace color space
     */
    protected AbstractPdfShadingMeshWithFlags(PdfDictionary pdfObject, int type, PdfColorSpace colorSpace) {
        super(pdfObject, type, colorSpace);
    }
}
