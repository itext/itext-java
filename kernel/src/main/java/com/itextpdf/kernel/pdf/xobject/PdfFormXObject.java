/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageHelper;

/**
 * A wrapper for Form XObject. ISO 32000-1, 8.10 FormXObjects.
 */
public class PdfFormXObject extends PdfXObject {

    protected PdfResources resources = null;

    /**
     * Creates a new instance of Form XObject.
     *
     * @param bBox the form XObject’s bounding box.
     */
    public PdfFormXObject(Rectangle bBox) {
        super(new PdfStream());
        getPdfObject().put(PdfName.Type, PdfName.XObject);
        getPdfObject().put(PdfName.Subtype, PdfName.Form);
        if (bBox != null) {
            getPdfObject().put(PdfName.BBox, new PdfArray(bBox));
        }
    }

    /**
     * Create {@link PdfFormXObject} instance by {@link PdfStream}.
     * Note, this constructor doesn't perform any additional checks
     *
     * @param pdfStream {@link PdfStream} with Form XObject.
     * @see PdfXObject#makeXObject(PdfStream)
     */
    public PdfFormXObject(PdfStream pdfStream) {
        super(pdfStream);
        if (!getPdfObject().containsKey(PdfName.Subtype)) {
            getPdfObject().put(PdfName.Subtype, PdfName.Form);
        }
    }

    /**
     * Creates form XObject from page content.
     * The page shall be from the document, to which FormXObject will be added.
     *
     * @param page an instance of {@link PdfPage}
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getCropBox());
        getPdfObject().getOutputStream().writeBytes(page.getContentBytes());
        resources = new PdfResources((PdfDictionary) page.getResources().getPdfObject().clone());
        getPdfObject().put(PdfName.Resources, resources.getPdfObject());
    }

    /**
     * Creates a form XObject from {@link com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData}.
     * Unlike other images, {@link com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData} images are represented as {@link PdfFormXObject}, not as
     * {@link PdfImageXObject}.
     *
     * @param image       image to create form object from
     * @param pdfDocument document instance which is needed for writing form stream contents
     */
    public PdfFormXObject(WmfImageData image, PdfDocument pdfDocument) {
        this(new WmfImageHelper(image).createFormXObject(pdfDocument).getPdfObject());
    }

    /**
     * Calculates the coordinates of the xObject BBox multiplied by the Matrix field.
     *
     * <p>
     * For mor information see paragraph 8.10.1 in ISO-32000-1.
     *
     * @param form the object for which calculate the coordinates of the bBox
     * @return the bBox {@link Rectangle}
     */
    public static Rectangle calculateBBoxMultipliedByMatrix(PdfFormXObject form) {
        PdfArray pdfArrayBBox = form.getPdfObject().getAsArray(PdfName.BBox);
        if (pdfArrayBBox == null) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_FORM_XOBJECT_HAS_INVALID_BBOX);
        }
        float[] bBoxArray = pdfArrayBBox.toFloatArray();
        PdfArray pdfArrayMatrix = form.getPdfObject().getAsArray(PdfName.Matrix);
        float[] matrixArray;
        if (pdfArrayMatrix == null) {
            matrixArray = new float[] {1, 0, 0, 1, 0, 0};
        } else {
            matrixArray = pdfArrayMatrix.toFloatArray();
        }
        Matrix matrix = new Matrix(matrixArray[0], matrixArray[1], matrixArray[2], matrixArray[3], matrixArray[4], matrixArray[5]);
        Vector bBoxMin = new Vector(bBoxArray[0], bBoxArray[1], 1);
        Vector bBoxMax = new Vector(bBoxArray[2], bBoxArray[3], 1);

        Vector bBoxMinByMatrix = bBoxMin.cross(matrix);
        Vector bBoxMaxByMatrix = bBoxMax.cross(matrix);
        float width = bBoxMaxByMatrix.get(Vector.I1) - bBoxMinByMatrix.get(Vector.I1);
        float height = bBoxMaxByMatrix.get(Vector.I2) - bBoxMinByMatrix.get(Vector.I2);

        return new Rectangle(bBoxMinByMatrix.get(Vector.I1), bBoxMinByMatrix.get(Vector.I2), width, height);
    }

    /**
     * Gets {@link PdfResources} of the Form XObject.
     * Note, if there is no resources, a new instance will be created.
     *
     * @return not null instance of {@link PdfResources}.
     */
    public PdfResources getResources() {
        if (this.resources == null) {
            PdfDictionary resourcesDict = getPdfObject().getAsDictionary(PdfName.Resources);
            if (resourcesDict == null) {
                resourcesDict = new PdfDictionary();
                getPdfObject().put(PdfName.Resources, resourcesDict);
            }
            this.resources = new PdfResources(resourcesDict);
        }
        return resources;
    }

    /**
     * Gets Form XObject's BBox, {@link PdfName#BBox} key.
     *
     * @return a {@link PdfArray}, that represents {@link Rectangle}.
     */
    public PdfArray getBBox() {
        return getPdfObject().getAsArray(PdfName.BBox);
    }

    /**
     * Sets Form XObject's BBox, {@link PdfName#BBox} key.
     *
     * @param bBox a {@link PdfArray}, that represents {@link Rectangle}.
     * @return object itself.
     */
    public PdfFormXObject setBBox(PdfArray bBox) {
        return put(PdfName.BBox, bBox);
    }

    /**
     * Sets a group attributes dictionary indicating that the contents of the form XObject
     * shall be treated as a group and specifying the attributes of that group.
     * {@link PdfName#Group} key.
     *
     * @param transparency instance of {@link PdfTransparencyGroup}.
     * @return object itself.
     * @see PdfTransparencyGroup
     */
    public PdfFormXObject setGroup(PdfTransparencyGroup transparency) {
        return put(PdfName.Group, transparency.getPdfObject());
    }

    /**
     * Gets width based on XObject's BBox.
     *
     * @return float value.
     */
    @Override
    public float getWidth() {
        return getBBox() == null ? 0 : getBBox().getAsNumber(2).floatValue() - getBBox().getAsNumber(0).floatValue();
    }

    /**
     * Gets height based on XObject's BBox.
     *
     * @return float value.
     */
    @Override
    public float getHeight() {
        return getBBox() == null ? 0 : getBBox().getAsNumber(3).floatValue() - getBBox().getAsNumber(1).floatValue();
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        resources = null;
        if (getPdfObject().get(PdfName.BBox) == null) {
            throw new PdfException(KernelExceptionMessageConstant.FORM_XOBJECT_MUST_HAVE_BBOX);
        }
        super.flush();
    }

    //-----Additional entries in form dictionary for Trap Network annotation

    /**
     * Sets process color model for trap network appearance, {@link PdfName#PCM} key.
     *
     * @param model shall be one of the valid values: {@link PdfName#DeviceGray},
     *              {@link PdfName#DeviceRGB}, {@link PdfName#DeviceCMYK}, {@link PdfName#DeviceCMY},
     *              {@link PdfName#DeviceRGBK}, and {@link PdfName#DeviceN}.
     * @return object itself.
     */
    public PdfFormXObject setProcessColorModel(PdfName model) {
        return put(PdfName.PCM, model);
    }

    /**
     * Gets process color model of trap network appearance, {@link PdfName#PCM} key.
     *
     * @return a {@link PdfName} instance, possible values: {@link PdfName#DeviceGray},
     * {@link PdfName#DeviceRGB}, {@link PdfName#DeviceCMYK}, {@link PdfName#DeviceCMY},
     * {@link PdfName#DeviceRGBK}, and {@link PdfName#DeviceN}.
     */
    public PdfName getProcessColorModel() {
        return getPdfObject().getAsName(PdfName.PCM);
    }

    /**
     * Sets separation color names for the trap network appearance, {@link PdfName#SeparationColorNames} key.
     *
     * @param colorNames an array of names identifying the colorants that were assumed
     *                   when the trap network appearance was created.
     * @return object itself.
     */
    public PdfFormXObject setSeparationColorNames(PdfArray colorNames) {
        return put(PdfName.SeparationColorNames, colorNames);
    }

    /**
     * Gets separation color names of trap network appearance, {@link PdfName#SeparationColorNames} key.
     *
     * @return an {@link PdfArray} of names identifying the colorants.
     */
    public PdfArray getSeparationColorNames() {
        return getPdfObject().getAsArray(PdfName.SeparationColorNames);
    }

    /**
     * Sets an array of <b>TrapRegion</b> objects defining the page’s trapping zones
     * and the associated trapping parameters, as described in Adobe Technical Note #5620,
     * Portable Job Ticket Format. {@link PdfName#TrapRegions} key.
     *
     * @param regions A {@link PdfArray} of indirect references to <b>TrapRegion</b> objects.
     * @return object itself.
     */
    public PdfFormXObject setTrapRegions(PdfArray regions) {
        return put(PdfName.TrapRegions, regions);
    }

    /**
     * Gets an array of <b>TrapRegion</b> objects defining the page’s trapping zones
     * and the associated trapping parameters, as described in Adobe Technical Note #5620,
     * Portable Job Ticket Format. {@link PdfName#TrapRegions} key.
     *
     * @return A {@link PdfArray} of indirect references to <b>TrapRegion</b> objects.
     */
    public PdfArray getTrapRegions() {
        return getPdfObject().getAsArray(PdfName.TrapRegions);
    }

    /**
     * Sets a human-readable text string that described this trap network to the user.
     * {@link PdfName#TrapStyles} key.
     *
     * @param trapStyles a {@link PdfString} value.
     * @return object itself.
     */
    public PdfFormXObject setTrapStyles(PdfString trapStyles) {
        return put(PdfName.TrapStyles, trapStyles);
    }

    /**
     * Gets a human-readable text string that described this trap network to the user.
     * {@link PdfName#TrapStyles} key.
     *
     * @return a {@link PdfString} value.
     */
    public PdfString getTrapStyles() {
        return getPdfObject().getAsString(PdfName.TrapStyles);
    }

    //-----Additional entries in form dictionary for Printer Mark annotation

    /**
     * Sets a text string representing the printer’s mark in human-readable form.
     *
     * @param markStyle a string value.
     * @return object itself.
     */
    public PdfFormXObject setMarkStyle(PdfString markStyle) {
        return put(PdfName.MarkStyle, markStyle);
    }

    /**
     * Gets a text string representing the printer’s mark in human-readable form.
     *
     * @return a string value.
     */
    public PdfString getMarkStyle() {
        return getPdfObject().getAsString(PdfName.MarkStyle);
    }

    /**
     * Puts the value into Image XObject dictionary and associates it with the specified key.
     * If the key is already present, it will override the old value with the specified one.
     *
     * @param key   key to insert or to override
     * @param value the value to associate with the specified key
     * @return object itself.
     */
    public PdfFormXObject put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

}
