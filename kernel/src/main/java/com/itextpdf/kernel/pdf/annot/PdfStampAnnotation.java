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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

public class PdfStampAnnotation extends  PdfMarkupAnnotation {


	public PdfStampAnnotation(Rectangle rect) {
        super(rect);
    }

    /**
     * Instantiates a new {@link PdfStampAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfStampAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Stamp;
    }

    public PdfStampAnnotation setStampName(PdfName name){
        return (PdfStampAnnotation) put (PdfName.Name, name);
    }

    public PdfName getStampName() {
        return getPdfObject().getAsName(PdfName.Name);
    }

    /**
     * The name of an icon that is used in displaying the annotation.
     * Possible values are described in {@link #setIconName(PdfName)}.
     *
     * @return a {@link PdfName} that specifies the icon for displaying annotation, or null if icon name is not specified.
     */
    public PdfName getIconName() {
        return getPdfObject().getAsName(PdfName.Name);
    }

    /**
     * Gets the rotation angle in degrees.
     *
     * @return {@link PdfNumber} representing the clockwise rotation in degrees.
     */
    public PdfNumber getRotation() {
        return getPdfObject().getAsNumber(PdfName.Rotate);
    }

    /**
     * The name of an icon that is used in displaying the annotation.
     * @param name a {@link PdfName} that specifies the icon for displaying annotation. Possible values are:
     *             <ul>
     *                  <li>Approved
     *                  <li>Experimental
     *                  <li>NotApproved
     *                  <li>AsIs
     *                  <li>Expired
     *                  <li>NotForPublicRelease
     *                  <li>Confidential
     *                  <li>Final
     *                  <li>Sold
     *                  <li>Departmental
     *                  <li>ForComment
     *                  <li>TopSecret
     *                  <li>Draft
     *                  <li>ForPublicRelease.
     *             </ul>
     * @return this {@link PdfStampAnnotation} instance.
     */
    public PdfStampAnnotation setIconName(PdfName name) {
        return (PdfStampAnnotation) put(PdfName.Name, name);
    }

    /**
     * Sets the rotation angle in degrees.
     * @param degAngle an integer representing the clockwise rotation in degrees.
     *
     * @return this {@link PdfStampAnnotation} instance.
     */
    public PdfStampAnnotation setRotation(int degAngle) {
        put(PdfName.Rotate, new PdfNumber(degAngle));
        return this;
    }
}
