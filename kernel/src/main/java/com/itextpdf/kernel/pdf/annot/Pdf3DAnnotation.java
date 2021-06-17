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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

/**
 * This class represents 3D annotations by which 3D artwork shall be represented in a PDF document.
 * See also ISO-32000-2 13.6.2 "3D annotations".
 */
public class Pdf3DAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 3823509772499230844L;

    /**
     * Creates a {@link Pdf3DAnnotation} instance.
     *
     * @param rect the annotation rectangle, defining the location of the annotation on the page
     *             in default user space units. See {@link PdfAnnotation#setRectangle(PdfArray)}.
     * @param artwork 3D artwork which is represented by the annotation
     */
	public Pdf3DAnnotation(Rectangle rect, PdfObject artwork) {
        super(rect);
        put(PdfName._3DD, artwork);
    }

    /**
     * Instantiates a new {@link Pdf3DAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    public Pdf3DAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfName getSubtype() {
        return PdfName._3D;
    }

    /**
     * Sets the default initial view of the 3D artwork that shall be used when the annotation is activated.
     *
     * @param initialView the default initial view of the 3D artwork that shall be used
     *                    when the annotation is activated
     * @return this {@link Pdf3DAnnotation} instance
     */
    public Pdf3DAnnotation setDefaultInitialView(PdfObject initialView) {
        return (Pdf3DAnnotation) put(PdfName._3DV, initialView);
    }

    /**
     * Gets the default initial view of the 3D artwork that shall be used when the annotation is activated.
     *
     * @return the default initial view of the 3D artwork that shall be used when the annotation is activated
     */
    public PdfObject getDefaultInitialView() {
        return getPdfObject().get(PdfName._3DV);
    }

    /**
     * Sets the activation dictionary that defines the times at which the annotation shall be
     * activated and deactivated and the state of the 3D artwork instance at those times.
     *
     * @param activationDictionary dictionary that defines the times at which the annotation
     *                             shall be activated and deactivated and the state of the 3D artwork
     *                             instance at those times.
     * @return this {@link Pdf3DAnnotation} instance
     */
    public Pdf3DAnnotation setActivationDictionary(PdfDictionary activationDictionary) {
        return (Pdf3DAnnotation) put(PdfName._3DA, activationDictionary);
    }

    /**
     * Gets the activation dictionary that defines the times at which the annotation shall be
     * activated and deactivated and the state of the 3D artwork instance at those times.
     *
     * @return the activation dictionary that defines the times at which the annotation shall be
     * activated and deactivated and the state of the 3D artwork instance at those times.
     */
    public PdfDictionary getActivationDictionary() {
        return getPdfObject().getAsDictionary(PdfName._3DA);
    }

    /**
     * Sets the primary use of the 3D annotation.
     *
     * <p>
     * If true, it is intended to be interactive; if false, it is intended to be manipulated programmatically,
     * as with an ECMAScript animation. Interactive PDF processors may present different user interface controls
     * for interactive 3D annotations (for example, to rotate, pan, or zoom the artwork) than for those
     * managed by a script or other mechanism.
     *
     * <p>
     * Default value: true.
     *
     * @param interactive if true, it is intended to be interactive; if false, it is intended to be
     *                    manipulated programmatically
     * @return this {@link Pdf3DAnnotation} instance
     */
    public Pdf3DAnnotation setInteractive(boolean interactive) {
        return (Pdf3DAnnotation) put(PdfName._3DI, PdfBoolean.valueOf(interactive));
    }

    /**
     * Indicates whether the 3D annotation is intended to be interactive or not.
     *
     * @return whether the 3D annotation is intended to be interactive or not
     */
    public PdfBoolean isInteractive() {
        return getPdfObject().getAsBoolean(PdfName._3DI);
    }

    /**
     * Sets the 3D view box, which is the rectangular area in which the 3D artwork shall be drawn.
     * It shall be within the rectangle specified by the annotation’s Rect entry and shall be expressed
     * in the annotation’s target coordinate system.
     *
     * <p>
     * Default value: the annotation’s Rect entry, expressed in the target coordinate system.
     * This value is [-w/2 -h/2 w/2 h/2], where w and h are the width and height, respectively, of Rect.
     *
     * @param viewBox the rectangular area in which the 3D artwork shall be drawn
     * @return this {@link Pdf3DAnnotation} instance
     */
    public Pdf3DAnnotation setViewBox(Rectangle viewBox) {
        return (Pdf3DAnnotation) put(PdfName._3DB, new PdfArray(viewBox));
    }

    /**
     * Gets the 3D view box, which is the rectangular area in which the 3D artwork shall be drawn.
     *
     * @return the 3D view box, which is the rectangular area in which the 3D artwork shall be drawn.
     */
    public Rectangle getViewBox() {
        return getPdfObject().getAsRectangle(PdfName._3DB);
    }
}
