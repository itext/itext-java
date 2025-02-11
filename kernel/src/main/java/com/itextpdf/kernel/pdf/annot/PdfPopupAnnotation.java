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
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

public class PdfPopupAnnotation extends PdfAnnotation {

	
    protected PdfAnnotation parent;

    public PdfPopupAnnotation(Rectangle rect) {
        super(rect);
    }

    /**
     * Instantiates a new {@link PdfPopupAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfPopupAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfName getSubtype() {
        return PdfName.Popup;
    }

    public PdfDictionary getParentObject() {
        return getPdfObject().getAsDictionary(PdfName.Parent);
    }

    public PdfAnnotation getParent() {
        if (parent == null) {
            parent = makeAnnotation(getParentObject());
        }
        return parent;
    }

    public PdfPopupAnnotation setParent(PdfAnnotation parent) {
        this.parent = parent;
        return (PdfPopupAnnotation) put(PdfName.Parent, parent.getPdfObject());
    }

    /**
     * A flag specifying whether the annotation shall initially be displayed open.
     * This flag has affect to not all kinds of annotations.
     * @return true if annotation is initially open, false - if closed.
     */
    public boolean getOpen() {
        return PdfBoolean.TRUE.equals(getPdfObject().getAsBoolean(PdfName.Open));
    }

    /**
     * Sets a flag specifying whether the annotation shall initially be displayed open.
     * This flag has affect to not all kinds of annotations.
     * @param open true if annotation shall initially be open, false - if closed.
     * @return this {@link PdfPopupAnnotation} instance.
     */
    public PdfPopupAnnotation setOpen(boolean open) {
        return (PdfPopupAnnotation) put(PdfName.Open, PdfBoolean.valueOf(open));
    }

}
