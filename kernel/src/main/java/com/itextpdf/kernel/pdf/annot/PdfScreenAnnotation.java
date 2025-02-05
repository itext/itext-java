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
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.action.PdfAction;

public class PdfScreenAnnotation extends PdfAnnotation {


	public PdfScreenAnnotation(Rectangle rect) {
        super(rect);
    }

    /**
     * Instantiates a new {@link PdfScreenAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfScreenAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Screen;
    }

    /**
     * An {@link PdfAction} to perform, such as launching an application, playing a sound,
     * changing an annotation’s appearance state etc, when the annotation is activated.
     * @return {@link PdfDictionary} which defines the characteristics and behaviour of an action.
     */
    public PdfDictionary getAction() {
        return getPdfObject().getAsDictionary(PdfName.A);
    }

    /**
     * Sets a {@link PdfAction} to this annotation which will be performed when the annotation is activated.
     * @param action {@link PdfAction} to set to this annotation.
     * @return this {@link PdfScreenAnnotation} instance.
     */
    public PdfScreenAnnotation setAction(PdfAction action) {
        return (PdfScreenAnnotation) put(PdfName.A, action.getPdfObject());
    }

    /**
     * An additional actions dictionary that extends the set of events that can trigger the execution of an action.
     * See ISO-320001 12.6.3 Trigger Events.
     * @return an additional actions {@link PdfDictionary}.
     * @see #getAction()
     */
    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    /**
     * Sets an additional {@link PdfAction} to this annotation which will be performed in response to
     * the specific trigger event defined by {@code key}. See ISO-320001 12.6.3, "Trigger Events".
     * @param key a {@link PdfName} that denotes a type of the additional action to set.
     * @param action {@link PdfAction} to set as additional to this annotation.
     * @return this {@link PdfScreenAnnotation} instance.
     */
    public PdfScreenAnnotation setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    /**
     * An appearance characteristics dictionary containing additional information for constructing the
     * annotation’s appearance stream. See ISO-320001, Table 189.
     *
     * @return an appearance characteristics dictionary or null if it isn't specified.
     */
    public PdfDictionary getAppearanceCharacteristics() {
        return getPdfObject().getAsDictionary(PdfName.MK);
    }

    /**
     * Sets an appearance characteristics dictionary containing additional information for constructing the
     * annotation’s appearance stream. See ISO-320001, Table 189.
     *
     * @param characteristics the {@link PdfDictionary} with additional information for appearance stream.
     * @return this {@link PdfScreenAnnotation} instance.
     */
    public PdfScreenAnnotation setAppearanceCharacteristics(PdfDictionary characteristics) {
        return (PdfScreenAnnotation) put(PdfName.MK, characteristics);
    }
}
