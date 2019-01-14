/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
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

    private static final long serialVersionUID = 467500482711722178L;
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
            throw new PdfException(PdfException.FormXObjectMustHaveBbox);
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
        return this;
    }

}
