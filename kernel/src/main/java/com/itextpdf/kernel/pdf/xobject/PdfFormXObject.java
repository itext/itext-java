/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImage;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageHelper;

public class PdfFormXObject extends PdfXObject {

    private static final long serialVersionUID = 467500482711722178L;
	private PdfResources resources = null;

    public PdfFormXObject(Rectangle bBox) {
        super(new PdfStream());
        getPdfObject().put(PdfName.Type, PdfName.XObject);
        getPdfObject().put(PdfName.Subtype, PdfName.Form);
        if (bBox != null) {
            getPdfObject().put(PdfName.BBox, new PdfArray(bBox));
        }
    }

    public PdfFormXObject(PdfStream pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates form XObject from page content.
     * The page shall be from the document, to which FormXObject will be added.
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) {
        this(page.getCropBox());
        getPdfObject().getOutputStream().writeBytes(page.getContentBytes());
        resources = new PdfResources((PdfDictionary)page.getResources().getPdfObject().clone());
        getPdfObject().put(PdfName.Resources, resources.getPdfObject());
    }

    /**
     * Creates a form XObject from {@link WmfImage}.
     * Unlike other images, {@link WmfImage} images are represented as {@link PdfFormXObject}, not as
     * {@link PdfImageXObject}.
     * @param image image to create form object from
     * @param pdfDocument document instance which is needed for writing form stream contents
     */
    public PdfFormXObject(WmfImage image, PdfDocument pdfDocument) {
        this(new WmfImageHelper(image).createPdfForm(pdfDocument).getPdfObject());
    }

    public PdfResources getResources() {
        if (this.resources == null) {
            PdfDictionary resources = getPdfObject().getAsDictionary(PdfName.Resources);
            if (resources == null) {
                resources = new PdfDictionary();
                getPdfObject().put(PdfName.Resources, resources);
            }
            this.resources = new PdfResources(resources);
        }
        return resources;
    }

    @Override
    public void flush() {
        resources = null;
        if (getPdfObject().get(PdfName.BBox) == null) {
            throw new PdfException(PdfException.FormXObjectMustHaveBbox);
        }
        super.flush();
    }

    //Additional entries in form dictionary for Trap Network annotation
    public PdfFormXObject setProcessColorModel(PdfName model){
        return put(PdfName.PCM, model);
    }

    public PdfName getProcessColorModel() {
        return getPdfObject().getAsName(PdfName.PCM);
    }

    public PdfFormXObject setSeparationColorNames(PdfArray colorNames){
        return put(PdfName.SeparationColorNames, colorNames);
    }

    public PdfArray getSeparationColorNames() {
        return getPdfObject().getAsArray(PdfName.SeparationColorNames);
    }

    public PdfFormXObject setTrapRegions(PdfArray regions){
        return put(PdfName.TrapRegions, regions);
    }

    public PdfArray getTrapRegions() {
        return getPdfObject().getAsArray(PdfName.TrapRegions);
    }

    public PdfFormXObject setTrapStyles(PdfString trapStyles){
        return put(PdfName.TrapStyles, trapStyles);
    }

    public PdfString getTrapStyles() {
        return getPdfObject().getAsString(PdfName.TrapStyles);
    }

    //Additional entries in form dictionary for Printer Mark annotation
    public PdfFormXObject setMarkStyle(PdfString markStyle) {
        return put(PdfName.MarkStyle, markStyle);
    }

    public PdfString getMarkStyle() {
        return getPdfObject().getAsString(PdfName.MarkStyle);
    }

    public PdfArray getBBox() {
        return getPdfObject().getAsArray(PdfName.BBox);
    }

    public PdfFormXObject setBBox(PdfArray bBox) {
        return put(PdfName.BBox, bBox);
    }

    public PdfFormXObject setGroup(PdfTransparencyGroup transparency) {
        return put(PdfName.Group, transparency.getPdfObject());
    }

    @Override
    public Float getWidth() { return getBBox() == null ? null : getBBox().getAsFloat(2);}

    @Override
    public Float getHeight() { return getBBox() == null ? null : getBBox().getAsFloat(3); }
}
