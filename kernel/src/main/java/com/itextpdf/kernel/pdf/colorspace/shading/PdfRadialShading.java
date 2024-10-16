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
 * and is in charge of Shading Dictionary with radial type, that defines a colour blend that varies between two circles.
 *
 * <p>
 * This type of shading shall not be used with an Indexed colour space
 */
public class PdfRadialShading extends AbstractPdfShadingBlend {

    /**
     * Creates the new instance of the class from the existing {@link PdfDictionary}.
     *
     * @param pdfDictionary from which this {@link PdfRadialShading} will be created
     */
    public PdfRadialShading(PdfDictionary pdfDictionary) {
        super(pdfDictionary);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The Indexed color space isn't excepted
     * @param x0 the X coordinate of starting circle's centre, expressed in in the shading’s target coordinate space
     * @param y0 the Y coordinate of starting circle's centre, expressed in in the shading’s target coordinate space
     * @param r0 the radius of starting circle's centre, should be greater or equal to 0.
     *           If 0 then starting circle is treated as point.
     *           If both radii are 0, nothing shall be painted
     * @param color0 the {@code float[]} that represents the color in the start circle
     * @param x1 the X coordinate of ending circle's centre, expressed in in the shading’s target coordinate space
     * @param y1 the Y coordinate of ending circle's centre, expressed in in the shading’s target coordinate space
     * @param r1 the radius of ending circle's centre, should be greater or equal to 0.
     *           If 0 then ending circle is treated as point.
     *           If both radii are 0, nothing shall be painted
     * @param color1 the {@code float[]} that represents the color in the end circle
     */
    public PdfRadialShading(PdfColorSpace cs, float x0, float y0, float r0, float[] color0, float x1, float y1,
            float r1, float[] color1) {
        super(new PdfDictionary(), ShadingType.RADIAL, cs);

        setCoords(x0, y0, r0, x1, y1, r1);
        IPdfFunction func = new PdfType2Function(new float[] {0, 1}, null,
                color0, color1, 1);
        setFunction(func);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The Indexed color space isn't excepted
     * @param x0 the X coordinate of starting circle's centre, expressed in in the shading’s target coordinate space
     * @param y0 the Y coordinate of starting circle's centre, expressed in in the shading’s target coordinate space
     * @param r0 the radius of starting circle's centre, should be greater or equal to 0.
     *           If 0 then starting circle is treated as point.
     *           If both radii are 0, nothing shall be painted
     * @param color0 the {@code float[]} that represents the color in the start circle
     * @param x1 the X coordinate of ending circle's centre, expressed in in the shading’s target coordinate space
     * @param y1 the Y coordinate of ending circle's centre, expressed in in the shading’s target coordinate space
     * @param r1 the radius of ending circle's centre, should be greater or equal to 0.
     *           If 0 then ending circle is treated as point.
     *           If both radii are 0, nothing shall be painted
     * @param color1 the {@code float[]} that represents the color in the end circle
     * @param extend the array of two {@code boolean} that specified whether to extend the shading
     *               beyond the starting and ending points of the axis, respectively
     */
    public PdfRadialShading(PdfColorSpace cs, float x0, float y0, float r0, float[] color0, float x1, float y1, float r1,
            float[] color1, boolean[] extend) {
        this(cs, x0, y0, r0, color0, x1, y1, r1, color1);

        if (extend == null || extend.length != 2)
            throw new IllegalArgumentException("extend");

        setExtend(extend[0], extend[1]);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
     *           The Indexed color space isn't excepted
     * @param coords the {@link PdfArray} of of six numbers [x0 y0 r0 x1 y1 r1],
     *               specifying the centres and radii of the starting and ending circles,
     *               expressed in the shading’s target coordinate space.
     *               The radii r0 and r1 shall both be greater than or equal to 0.
     *               If one radius is 0, the corresponding circle shall be treated as a point;
     *               if both are 0, nothing shall be painted
     * @param function the {@link IPdfFunction} object, that is used to calculate color transitions
     */
    public PdfRadialShading(PdfColorSpace cs, PdfArray coords, IPdfFunction function) {
        super(new PdfDictionary(), ShadingType.RADIAL, cs);
        setCoords(coords);
        setFunction(function);
    }

    /**
     * Sets the coords object.
     *
     * @param x0 the X coordinate of starting circle's centre, expressed in in the shading’s target coordinate space
     * @param y0 the Y coordinate of starting circle's centre, expressed in in the shading’s target coordinate space
     * @param r0 the radius of starting circle's centre, should be greater or equal to 0.
     *           If 0 then starting circle is treated as point.
     *           If both radii are 0, nothing shall be painted
     * @param x1 the X coordinate of ending circle's centre, expressed in in the shading’s target coordinate space
     * @param y1 the Y coordinate of ending circle's centre, expressed in in the shading’s target coordinate space
     * @param r1 the radius of ending circle's centre, should be greater or equal to 0.
     *           If 0 then ending circle is treated as point.
     *           If both radii are 0, nothing shall be painted
     */
    public final void setCoords(float x0, float y0, float r0, float x1, float y1, float r1) {
        setCoords(new PdfArray(new float[] {x0, y0, r0, x1, y1, r1}));
    }
}