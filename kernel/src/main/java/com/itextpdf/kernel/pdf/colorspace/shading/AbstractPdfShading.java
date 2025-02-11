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

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Pattern;
import com.itextpdf.kernel.pdf.function.IPdfFunction;

/**
 * The PdfShading class that represents the Shading Dictionary PDF object.
 */
public abstract class AbstractPdfShading extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates the {@link AbstractPdfShading} object from the existing {@link PdfDictionary} with corresponding type.
     *
     * @param shadingDictionary {@link PdfDictionary} from which the {@link AbstractPdfShading} object will be created
     *
     * @return Created {@link AbstractPdfShading} object
     */
    public static AbstractPdfShading makeShading(PdfDictionary shadingDictionary) {
        if (!shadingDictionary.containsKey(PdfName.ShadingType)) {
            throw new PdfException(KernelExceptionMessageConstant.SHADING_TYPE_NOT_FOUND);
        }
        if (!shadingDictionary.containsKey(PdfName.ColorSpace)) {
            throw new PdfException(KernelExceptionMessageConstant.COLOR_SPACE_NOT_FOUND);
        }

        AbstractPdfShading shading;
        switch (shadingDictionary.getAsNumber(PdfName.ShadingType).intValue()) {
            case ShadingType.FUNCTION_BASED:
                shading = new PdfFunctionBasedShading(shadingDictionary);
                break;
            case ShadingType.AXIAL:
                shading = new PdfAxialShading(shadingDictionary);
                break;
            case ShadingType.RADIAL:
                shading = new PdfRadialShading(shadingDictionary);
                break;
            case ShadingType.FREE_FORM_GOURAUD_SHADED_TRIANGLE_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE);
                }
                shading = new PdfFreeFormGouraudShadedTriangleShading((PdfStream) shadingDictionary);
                break;
            case ShadingType.LATTICE_FORM_GOURAUD_SHADED_TRIANGLE_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE);
                }
                shading = new PdfLatticeFormGouraudShadedTriangleShading((PdfStream) shadingDictionary);
                break;
            case ShadingType.COONS_PATCH_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE);
                }
                shading = new PdfCoonsPatchShading((PdfStream) shadingDictionary);
                break;
            case ShadingType.TENSOR_PRODUCT_PATCH_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE);
                }
                shading = new PdfTensorProductPatchShading((PdfStream) shadingDictionary);
                break;
            default:
                throw new PdfException(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE);
        }
        return shading;
    }

    /**
     * Creates the {@link AbstractPdfShading} object from the existing {@link PdfDictionary}.
     *
     * @param pdfObject {@link PdfDictionary} from which the {@link AbstractPdfShading} object will be created
     */
    protected AbstractPdfShading(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates the {@link AbstractPdfShading} object from the existing {@link PdfDictionary},
     * using provided type and colorspace.
     *
     * @param pdfObject {@link PdfDictionary} from which the {@link AbstractPdfShading} object will be created
     * @param type type with which this {@link AbstractPdfShading} object will be created
     * @param colorSpace {@link PdfColorSpace} with which this {@link AbstractPdfShading} object will be created
     */
    protected AbstractPdfShading(PdfDictionary pdfObject, int type, PdfColorSpace colorSpace) {
        super(pdfObject);
        getPdfObject().put(PdfName.ShadingType, new PdfNumber(type));
        if (colorSpace instanceof Pattern) {
            throw new IllegalArgumentException("colorSpace");
        }
        getPdfObject().put(PdfName.ColorSpace, colorSpace.getPdfObject());
    }

    /**
     * Gets the shading type.
     *
     * @return int value of {@link PdfName#ShadingType}
     */
    public int getShadingType() {
        return (int) getPdfObject().getAsInt(PdfName.ShadingType);
    }

    /**
     * Gets the color space in which colour values shall be expressed.
     *
     * @return {@link PdfObject} Color space
     */
    public PdfObject getColorSpace() {
        return getPdfObject().get(PdfName.ColorSpace);
    }

    /**
     * Gets the function PdfObject that represents color transitions
     * across the shading geometry.
     *
     * @return {@link PdfObject} Function
     */
    public PdfObject getFunction() {
        return getPdfObject().get(PdfName.Function);
    }

    /**
     * Sets the function that represents color transitions
     * across the shading geometry as one object.
     *
     * @param function The {@link IPdfFunction} to set
     */
    public final void setFunction(IPdfFunction function) {
        getPdfObject().put(PdfName.Function, function.getAsPdfObject());
        setModified();
    }

    /**
     * Sets the function object that represents color transitions
     * across the shading geometry as an array of functions.
     *
     * @param functions The array of {@link IPdfFunction} to be set
     */
    public final void setFunction(IPdfFunction[] functions) {
        PdfArray arr = new PdfArray();
        for (IPdfFunction func : functions) {
            arr.add(func.getAsPdfObject());
        }
        getPdfObject().put(PdfName.Function, arr);
        setModified();
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public final void flush() {
        super.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
