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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfTextAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -2061119066076464569L;

	public PdfTextAnnotation(Rectangle rect) {
        super(rect);
    }

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
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
