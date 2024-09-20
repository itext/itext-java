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
package com.itextpdf.kernel.pdf.colorspace.shading;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

/**
 * The class that extends {@link AbstractPdfShading}, {@link AbstractPdfShadingMesh}
 * and {@link AbstractPdfShadingMeshWithFlags} classes and is in charge of Shading Dictionary
 * with Tensor-Product Patch mesh type.
 *
 * <p>
 * This type of shading is identical to {@link PdfCoonsPatchShading}, except that it's based on a
 * bicubic tensor-product patch defined by 16 control points.
 *
 * <p>
 * For the format of data stream, that defines patches, see ISO-320001 Table 86.
 */
public class PdfTensorProductPatchShading extends AbstractPdfShadingMeshWithFlags {

    /**
     * Creates the new instance of the class from the existing {@link PdfStream}.
     *
     * @param pdfStream from which this {@link PdfTensorProductPatchShading} will be created
     */
    public PdfTensorProductPatchShading(PdfStream pdfStream) {
        super(pdfStream);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The special Pattern space isn't excepted
     * @param bitsPerCoordinate the number of bits used to represent each vertex coordinate.
     *                          The value shall be 1, 2, 4, 8, 12, 16, 24, or 32
     * @param bitsPerComponent the number of bits used to represent each colour component.
     *                         The value shall be 1, 2, 4, 8, 12, or 16
     * @param bitsPerFlag the number of bits used to represent the edge flag for each vertex.
     *                    The value of BitsPerFlag shall be 2, 4, or 8,
     *                    but only the least significant 2 bits in each flag value shall be used.
     *                    The value for the edge flag shall be 0, 1, 2 or 3
     * @param decode the {@code int[]} of numbers specifying how to map vertex coordinates and colour components
     *               into the appropriate ranges of values. The ranges shall be specified as follows:
     *               [x_min x_max y_min y_max c1_min c1_max … cn_min cn_max].
     *               Only one pair of color values shall be specified if a Function entry is present
     */
    public PdfTensorProductPatchShading(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent,
            int bitsPerFlag, float[] decode) {
        this(cs, bitsPerCoordinate, bitsPerComponent, bitsPerFlag, new PdfArray(decode));
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The special Pattern space isn't excepted
     * @param bitsPerCoordinate the number of bits used to represent each vertex coordinate.
     *                          The value shall be 1, 2, 4, 8, 12, 16, 24, or 32
     * @param bitsPerComponent the number of bits used to represent each colour component.
     *                         The value shall be 1, 2, 4, 8, 12, or 16
     * @param bitsPerFlag the number of bits used to represent the edge flag for each vertex.
     *                    The value of BitsPerFlag shall be 2, 4, or 8,
     *                    but only the least significant 2 bits in each flag value shall be used.
     *                    The value for the edge flag shall be 0, 1, 2 or 3
     * @param decode the {@link PdfArray} of numbers specifying how to map vertex coordinates and colour components
     *               into the appropriate ranges of values. The ranges shall be specified as follows:
     *               [x_min x_max y_min y_max c1_min c1_max … cn_min cn_max].
     *               Only one pair of color values shall be specified if a Function entry is present
     */
    public PdfTensorProductPatchShading(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent,
            int bitsPerFlag, PdfArray decode) {
        super(new PdfStream(), ShadingType.TENSOR_PRODUCT_PATCH_MESH, cs);

        setBitsPerCoordinate(bitsPerCoordinate);
        setBitsPerComponent(bitsPerComponent);
        setBitsPerFlag(bitsPerFlag);
        setDecode(decode);
    }
}