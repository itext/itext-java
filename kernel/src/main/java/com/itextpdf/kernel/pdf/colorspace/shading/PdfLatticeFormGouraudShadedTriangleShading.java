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
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

/**
 *The class that extends {@link AbstractPdfShading} and {@link AbstractPdfShadingMesh} classes
 * and is in charge of Shading Dictionary with lattice-form Gouraud-shaded triangle mesh type.
 *
 * <p>
 * This type is similar to {@link PdfFreeFormGouraudShadedTriangleShading} but instead of using free-form geometry,
 * the vertices are arranged in a pseudorectangular lattice,
 * which is topologically equivalent to a rectangular grid.
 * The vertices are organized into rows, which need not be geometrically linear.
 *
 * <p>
 * The verticals data in stream is similar to {@link PdfFreeFormGouraudShadedTriangleShading},
 * except there is no edge flag.
 */
public class PdfLatticeFormGouraudShadedTriangleShading extends AbstractPdfShadingMesh {

    /**
     * Creates the new instance of the class from the existing {@link PdfStream}.
     *
     * @param pdfStream from which this {@link PdfLatticeFormGouraudShadedTriangleShading} will be created
     */
    public PdfLatticeFormGouraudShadedTriangleShading(PdfStream pdfStream) {
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
     * @param verticesPerRow the number of vertices in each row of the lattice (shall be &gt; 1).
     *                       The number of rows need not be specified
     * @param decode the {@code int[]} of numbers specifying how to map vertex coordinates and colour components
     *               into the appropriate ranges of values. The ranges shall be specified as follows:
     *               [x_min x_max y_min y_max c1_min c1_max … cn_min cn_max].
     *               Only one pair of color values shall be specified if a Function entry is present
     */
    public PdfLatticeFormGouraudShadedTriangleShading(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent,
            int verticesPerRow, float[] decode) {
        this(cs, bitsPerCoordinate, bitsPerComponent, verticesPerRow, new PdfArray(decode));
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
     * @param verticesPerRow the number of vertices in each row of the lattice (shall be &gt; 1).
     *                       The number of rows need not be specified
     * @param decode the {@link PdfArray} of numbers specifying how to map vertex coordinates and colour components
     *               into the appropriate ranges of values. The ranges shall be specified as follows:
     *               [x_min x_max y_min y_max c1_min c1_max … cn_min cn_max].
     *               Only one pair of color values shall be specified if a Function entry is present
     */
    public PdfLatticeFormGouraudShadedTriangleShading(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent,
            int verticesPerRow, PdfArray decode) {
        super(new PdfStream(), ShadingType.LATTICE_FORM_GOURAUD_SHADED_TRIANGLE_MESH, cs);

        setBitsPerCoordinate(bitsPerCoordinate);
        setBitsPerComponent(bitsPerComponent);
        setVerticesPerRow(verticesPerRow);
        setDecode(decode);
    }

    /**
     * Gets the number of vertices in each row of the lattice.
     *
     * @return the number of vertices. Can only be greater than 1
     */
    public int getVerticesPerRow() {
        return (int) getPdfObject().getAsInt(PdfName.VerticesPerRow);
    }

    /**
     * Sets the number of vertices in each row of the lattice.
     * The number of rows need not be specified.
     *
     * @param verticesPerRow the number of vertices to be set. Shall be greater than 1
     */
    public final void setVerticesPerRow(int verticesPerRow) {
        getPdfObject().put(PdfName.VerticesPerRow, new PdfNumber(verticesPerRow));
        setModified();
    }
}