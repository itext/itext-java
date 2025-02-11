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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.layer.IPdfOCG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract wrapper for supported types of XObject.
 *
 * @see PdfFormXObject
 * @see PdfImageXObject
 */
public abstract class PdfXObject extends PdfObjectWrapper<PdfStream> {


    protected PdfXObject(PdfStream pdfObject) {
        super(pdfObject);
    }

    /**
     * Create {@link PdfFormXObject} or {@link PdfImageXObject} by {@link PdfStream}.
     *
     * @param stream {@link PdfStream} with either {@link PdfName#Form}
     *               or {@link PdfName#Image} {@link PdfName#Subtype}
     * @return either {@link PdfFormXObject} or {@link PdfImageXObject}.
     */
    public static PdfXObject makeXObject(PdfStream stream) {
        if (PdfName.Form.equals(stream.getAsName(PdfName.Subtype))) {
            return new PdfFormXObject(stream);
        } else if (PdfName.Image.equals(stream.getAsName(PdfName.Subtype))) {
            return new PdfImageXObject(stream);
        } else {
            throw new UnsupportedOperationException(KernelExceptionMessageConstant.UNSUPPORTED_XOBJECT_TYPE);
        }
    }

    /**
     * Calculates a rectangle with the specified coordinates and width, and the height is
     * calculated in such a way that the original proportions of the xObject do not change.
     *
     * <p>
     * To calculate the original width and height of the xObject, the BBox and Matrix fields
     * are used. For mor information see paragraph 8.10.1 in ISO-32000-1.
     *
     * @param xObject the xObject for which we are calculating the rectangle
     * @param x the x-coordinate of the lower-left corner of the rectangle
     * @param y the y-coordinate of the lower-left corner of the rectangle
     * @param width the width of the rectangle
     * @return the rectangle with specified coordinates and width
     */
    public static Rectangle calculateProportionallyFitRectangleWithWidth(PdfXObject xObject, float x, float y, float width) {
        if (xObject instanceof PdfFormXObject) {
            PdfFormXObject formXObject = (PdfFormXObject) xObject;
            Rectangle bBox = PdfFormXObject.calculateBBoxMultipliedByMatrix(formXObject);
            return new Rectangle(x, y, width, (width / bBox.getWidth()) * bBox.getHeight());
        } else if (xObject instanceof PdfImageXObject) {
            PdfImageXObject imageXObject = (PdfImageXObject) xObject;
            return new Rectangle(x, y, width, (width / imageXObject.getWidth()) * imageXObject.getHeight());
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Calculates a rectangle with the specified coordinates and height, and the width is
     * calculated in such a way that the original proportions of the xObject do not change.
     *
     * <p>
     * To calculate the original width and height of the xObject, the BBox and Matrix fields
     * are used. For mor information see paragraph 8.10.1 in ISO-32000-1.
     *
     * @param xObject the xObject for which we are calculating the rectangle
     * @param x the x-coordinate of the lower-left corner of the rectangle
     * @param y the y-coordinate of the lower-left corner of the rectangle
     * @param height the height of the rectangle
     * @return the rectangle with specified coordinates and height
     */
    public static Rectangle calculateProportionallyFitRectangleWithHeight(PdfXObject xObject, float x, float y, float height) {
        if (xObject instanceof PdfFormXObject) {
            PdfFormXObject formXObject = (PdfFormXObject) xObject;
            Rectangle bBox = PdfFormXObject.calculateBBoxMultipliedByMatrix(formXObject);
            return new Rectangle(x, y, (height / bBox.getHeight()) * bBox.getWidth(), height);
        } else if (xObject instanceof PdfImageXObject) {
            PdfImageXObject imageXObject = (PdfImageXObject) xObject;
            return new Rectangle(x, y, (height / imageXObject.getHeight()) * imageXObject.getWidth(), height);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Sets the layer this XObject belongs to.
     *
     * @param layer the layer this XObject belongs to.
     */
    public void setLayer(IPdfOCG layer) {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

    /**
     * Gets width of XObject.
     *
     * @return float value.
     */
    public float getWidth() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets height of XObject.
     *
     * @return float value.
     */
    public float getHeight() {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds file associated with PDF XObject and identifies the relationship between them.
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the XObject dictionary.
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     *
     * @param fs          file specification dictionary of associated file
     */
    public void addAssociatedFile(PdfFileSpec fs) {
        if (null == ((PdfDictionary)fs.getPdfObject()).get(PdfName.AFRelationship)) {
            Logger logger = LoggerFactory.getLogger(PdfXObject.class);
            logger.error(IoLogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
        }
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null) {
            afArray = new PdfArray();
            getPdfObject().put(PdfName.AF, afArray);
        }
        afArray.add(fs.getPdfObject());
    }

    /**
     * Returns files associated with XObject.
     *
     * @param create defines whether AF arrays will be created if it doesn't exist
     * @return associated files array
     */
    public PdfArray getAssociatedFiles(boolean create) {
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null && create) {
            afArray = new PdfArray();
            getPdfObject().put(PdfName.AF, afArray);
        }
        return afArray;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
