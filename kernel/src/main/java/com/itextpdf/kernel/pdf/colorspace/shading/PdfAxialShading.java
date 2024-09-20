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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.function.IPdfFunction;
import com.itextpdf.kernel.pdf.function.PdfType2Function;

/**
 * The class that extends {@link AbstractPdfShading} and {@link AbstractPdfShadingBlend} classes
 * and is in charge of Shading Dictionary with axial type, that define a colour blend that varies along
 * a linear axis between two endpoints and extends indefinitely perpendicular to that axis.
 */
public class PdfAxialShading extends AbstractPdfShadingBlend {

    /**
     * Creates the new instance of the class from the existing {@link PdfDictionary}.
     *
     * @param pdfDictionary from which this {@link PdfAxialShading} will be created
     */
    public PdfAxialShading(PdfDictionary pdfDictionary) {
        super(pdfDictionary);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The special Pattern space isn't excepted
     * @param x0 the start coordinate of X axis expressed in the shading's target coordinate space
     * @param y0 the start coordinate of Y axis expressed in the shading's target coordinate space
     * @param color0 the {@code float[]} that represents the color in the start point
     * @param x1 the end coordinate of X axis expressed in the shading's target coordinate space
     * @param y1 the end coordinate of Y axis expressed in the shading's target coordinate space
     * @param color1 the {@code float[]} that represents the color in the end point
     */
    public PdfAxialShading(PdfColorSpace cs, float x0, float y0, float[] color0, float x1, float y1, float[] color1) {
        super(new PdfDictionary(), ShadingType.AXIAL, cs);

        setCoords(x0, y0, x1, y1);
        IPdfFunction func = new PdfType2Function(new float[] {0, 1}, null, color0, color1, 1);
        setFunction(func);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The special Pattern space isn't excepted
     * @param x0 the start coordinate of X axis expressed in the shading's target coordinate space
     * @param y0 the start coordinate of Y axis expressed in the shading's target coordinate space
     * @param color0 the {@code float[]} that represents the color in the start point
     * @param x1 the end coordinate of X axis expressed in the shading's target coordinate space
     * @param y1 the end coordinate of Y axis expressed in the shading's target coordinate space
     * @param color1 the {@code float[]} that represents the color in the end point
     * @param extend the array of two booleans that specified whether to extend the shading
     *               beyond the starting and ending points of the axis, respectively
     */
    public PdfAxialShading(PdfColorSpace cs, float x0, float y0, float[] color0, float x1, float y1,
            float[] color1, boolean[] extend) {
        this(cs, x0, y0, color0, x1, y1, color1);

        if (extend == null || extend.length != 2)
            throw new IllegalArgumentException("extend");

        setExtend(extend[0], extend[1]);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The special Pattern space isn't excepted
     * @param coords the {@link PdfArray} of four numbers [x0 y0 x1 y1] that specified the starting
     *               and the endings coordinates of thew axis, expressed in the shading's target coordinate space
     * @param function the {@link IPdfFunction} object, that is used to calculate color transitions
     */
    public PdfAxialShading(PdfColorSpace cs, PdfArray coords, IPdfFunction function) {
        this(cs, coords, null, function);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs       the {@link PdfColorSpace} object in which colour values shall be expressed.
     *                 The special Pattern space isn't excepted
     * @param coords   the {@link PdfArray} of four numbers [x0 y0 x1 y1] that specified
     *                 the starting and the endings coordinates of thew axis, expressed
     *                 in the shading's target coordinate space
     * @param domain   the {@link PdfArray} of two numbers [t0 t1] specifying the limiting values
     *                 of a parametric variable t which is considered to vary linearly between
     *                 these two values and becomes the input argument to the colour function
     * @param function the {@link IPdfFunction} object, that is used to calculate color transitions
     */
    public PdfAxialShading(PdfColorSpace cs, PdfArray coords, PdfArray domain, IPdfFunction function) {
        super(new PdfDictionary(), ShadingType.AXIAL, cs);
        setCoords(coords);
        if (domain != null) {
            setDomain(domain);
        }
        setFunction(function);
    }

    /**
     * Sets the Choords object with the four params expressed in the shading's target coordinate space.
     *
     * @param x0 the start coordinate of X axis to be set
     * @param y0 the start coordinate of Y axis to be set
     * @param x1 the end coordinate of X axis to be set
     * @param y1 the end coordinate of Y axis to be set
     */
    public final void setCoords(float x0, float y0, float x1, float y1) {
        setCoords(new PdfArray(new float[] {x0, y0, x1, y1}));
    }
}