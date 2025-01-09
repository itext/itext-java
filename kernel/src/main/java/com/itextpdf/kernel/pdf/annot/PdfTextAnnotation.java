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
import com.itextpdf.kernel.pdf.PdfString;

public class PdfTextAnnotation extends PdfMarkupAnnotation {


	public PdfTextAnnotation(Rectangle rect) {
        super(rect);
    }

    /**
     * Instantiates a new {@link PdfTextAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfTextAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Text;
    }

    public PdfString getState() {
        return getPdfObject().getAsString(PdfName.State);
    }

    public PdfTextAnnotation setState(PdfString state) {
        return (PdfTextAnnotation) put(PdfName.State, state);
    }

    public PdfString getStateModel() {
        return getPdfObject().getAsString(PdfName.StateModel);
    }

    public PdfTextAnnotation setStateModel(PdfString stateModel) {
        return (PdfTextAnnotation) put(PdfName.StateModel, stateModel);
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
     * @return this {@link PdfTextAnnotation} instance.
     */
    public PdfTextAnnotation setOpen(boolean open) {
        return (PdfTextAnnotation) put(PdfName.Open, PdfBoolean.valueOf(open));
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
     * The name of an icon that is used in displaying the annotation.
     * @param name a {@link PdfName} that specifies the icon for displaying annotation. Possible values are:
     *             <ul>
     *                  <li>Comment
     *                  <li>Key
     *                  <li>Note
     *                  <li>Help
     *                  <li>NewParagraph
     *                  <li>Paragraph
     *                  <li>Insert
     *             </ul>
     * @return this {@link PdfTextAnnotation} instance.
     */
    public PdfTextAnnotation setIconName(PdfName name) {
        return (PdfTextAnnotation) put(PdfName.Name, name);
    }
}
