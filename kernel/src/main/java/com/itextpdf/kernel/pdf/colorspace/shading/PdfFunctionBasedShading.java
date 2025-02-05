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
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.function.IPdfFunction;

/**
 * The class that extends {@link AbstractPdfShading} class and is in charge of Shading Dictionary
 * with function-based type, that defines color at every point in the domain by a specified mathematical function.
 */
public class PdfFunctionBasedShading extends AbstractPdfShading {

    /**
     * Creates the new instance of the class from the existing {@link PdfDictionary}.
     *
     * @param pdfDictionary from which this {@link PdfFunctionBasedShading} will be created
     */
    public PdfFunctionBasedShading(PdfDictionary pdfDictionary) {
        super(pdfDictionary);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param colorSpace the {@link PdfColorSpace} object in which colour values shall be expressed
     * @param function the {@link IPdfFunction}, that is used to calculate color transitions
     */
    public PdfFunctionBasedShading(PdfColorSpace colorSpace, IPdfFunction function) {
        this(colorSpace.getPdfObject(), function);
    }

    /**
     * Creates the new instance of the class.
     *
     * @param colorSpace the {@link PdfObject}, that represents color space in which colour values shall be expressed
     * @param function the {@link IPdfFunction}, that is used to calculate color transitions
     */
    public PdfFunctionBasedShading(PdfObject colorSpace, IPdfFunction function) {
        super(new PdfDictionary(), ShadingType.FUNCTION_BASED, PdfColorSpace.makeColorSpace(colorSpace));

        setFunction(function);
    }

    /**
     * Gets the {@link PdfArray} domain rectangle object that establishes an internal coordinate space
     * for the shading that is independent of the target coordinate space in which it shall be painted.
     *
     * @return {@link PdfArray} domain rectangle
     */
    public PdfArray getDomain() {
        return getPdfObject().getAsArray(PdfName.Domain);
    }

    /**
     * Sets the {@link PdfArray} domain rectangle object that establishes an internal coordinate space
     * for the shading that is independent of the target coordinate space in which it shall be painted.
     *
     * @param xmin the Xmin coordinate of rectangle
     * @param xmax the Xmax coordinate of rectangle
     * @param ymin the Ymin coordinate of rectangle
     * @param ymax the Ymax coordinate of rectangle
     */
    public void setDomain(float xmin, float xmax, float ymin, float ymax) {
        setDomain(new PdfArray(new float[] {xmin, xmax, ymin, ymax}));
    }

    /**
     * Sets the {@link PdfArray} domain rectangle object that establishes an internal coordinate space
     * for the shading that is independent of the target coordinate space in which it shall be painted.
     *
     * @param domain the {@link PdfArray} domain rectangle object to be set
     */
    public void setDomain(PdfArray domain) {
        getPdfObject().put(PdfName.Domain, domain);
        setModified();
    }

    /**
     * Gets the {@link PdfArray} of floats that represents the transformation matrix that maps the domain rectangle
     * into a corresponding figure in the target coordinate space.
     *
     * @return the {@link PdfArray} of transformation matrix (identical matrix by default)
     */
    public PdfArray getMatrix() {
        PdfArray matrix = getPdfObject().getAsArray(PdfName.Matrix);
        if (matrix == null) {
            matrix = new PdfArray(new float[]{1, 0, 0, 1, 0, 0});
            setMatrix(matrix);
        }
        return matrix;
    }

    /**
     * Sets the array of floats that represents the transformation matrix that maps the domain rectangle
     * into a corresponding figure in the target coordinate space.
     *
     * @param matrix the {@code float[]} of transformation matrix to be set
     */
    public void setMatrix(float[] matrix) {
        setMatrix(new PdfArray(matrix));
    }

    /**
     * Sets the array of floats that represents the transformation matrix that maps the domain rectangle
     * into a corresponding figure in the target coordinate space.
     *
     * @param matrix the {@link PdfArray} transformation matrix object to be set
     */
    public void setMatrix(PdfArray matrix) {
        getPdfObject().put(PdfName.Matrix, matrix);
        setModified();
    }
}
