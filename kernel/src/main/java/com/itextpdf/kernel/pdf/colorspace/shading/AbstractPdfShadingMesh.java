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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

/**
 * The PdfShadingMesh class which extends {@link AbstractPdfShading} and represents shadings which are based on a mesh,
 * with BitsPerCoordinate, BitsPerComponent and Decode fields in the PDF object.
 */
public abstract class AbstractPdfShadingMesh extends AbstractPdfShading {

    /**
     * Gets the number of bits used to represent each vertex coordinate.
     *
     * @return the number of bits. Can be 1, 2, 4, 8, 12, 16, 24, or 32
     */
    public int getBitsPerCoordinate() {
        return (int) getPdfObject().getAsInt(PdfName.BitsPerCoordinate);
    }

    /**
     * Sets the number of bits used to represent each vertex coordinate.
     *
     * @param bitsPerCoordinate the number of bits to be set. Shall be 1, 2, 4, 8, 12, 16, 24, or 32
     */
    public final void setBitsPerCoordinate(int bitsPerCoordinate) {
        getPdfObject().put(PdfName.BitsPerCoordinate, new PdfNumber(bitsPerCoordinate));
        setModified();
    }

    /**
     * Gets the number of bits used to represent each colour component.
     *
     * @return the number of bits. Can be 1, 2, 4, 8, 12, or 16
     */
    public int getBitsPerComponent() {
        return (int) getPdfObject().getAsInt(PdfName.BitsPerComponent);
    }

    /**
     * Sets the number of bits used to represent each colour component.
     *
     * @param bitsPerComponent the number of bits to be set. Shall be 1, 2, 4, 8, 12, or 16
     */
    public final void setBitsPerComponent(int bitsPerComponent) {
        getPdfObject().put(PdfName.BitsPerComponent, new PdfNumber(bitsPerComponent));
        setModified();
    }

    /**
     * Gets the {@link PdfArray} of numbers specifying how to map vertex coordinates and colour components
     * into the appropriate ranges of values. The ranges shall be specified as follows:
     * [x_min x_max y_min y_max c1_min c1_max … cn_min cn_max].
     * Only one pair of color values shall be specified if a Function entry is present.
     *
     * @return the {@link PdfArray} Decode object
     */
    public PdfArray getDecode() {
        return getPdfObject().getAsArray(PdfName.Decode);
    }

    /**
     * Sets the {@code float[]} of numbers specifying how to map vertex coordinates and colour components
     * into the appropriate ranges of values. The ranges shall be specified as follows:
     * [x_min x_max y_min y_max c1_min c1_max … cn_min cn_max].
     * Only one pair of color values shall be specified if a Function entry is present.
     *
     * @param decode the {@code float[]} of Decode object to set
     */
    public final void setDecode(float[] decode) {
        setDecode(new PdfArray(decode));
    }

    /**
     * Sets the {@link PdfArray} of numbers specifying how to map vertex coordinates and colour components
     * into the appropriate ranges of values. The ranges shall be specified as follows:
     * [x_min x_max y_min y_max c1_min c1_max … cn_min cn_max].
     * Only one pair of color values shall be specified if a Function entry is present.
     *
     * @param decode the {@link PdfArray} Decode object to set
     */
    public final void setDecode(PdfArray decode) {
        getPdfObject().put(PdfName.Decode, decode);
        setModified();
    }

    /**
     * Constructor for PdfShadingBlend object using a PdfDictionary.
     *
     * @param pdfObject input PdfDictionary
     */
    protected AbstractPdfShadingMesh(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructor for PdfShadingBlend object using a PdfDictionary, shading type and color space.
     *
     * @param pdfObject input PdfDictionary
     * @param type shading type
     * @param colorSpace color space
     */
    protected AbstractPdfShadingMesh(PdfDictionary pdfObject, int type, PdfColorSpace colorSpace) {
        super(pdfObject, type, colorSpace);
    }
}
