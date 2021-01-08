/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
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

    private static final long serialVersionUID = -480702872582809954L;

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
            throw new UnsupportedOperationException(PdfException.UnsupportedXObjectType);
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
            logger.error(LogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
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
