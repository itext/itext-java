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
package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfCollection extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 5184499156015360355L;

    /** A type of initial view */
    public static final int DETAILS = 0;
    /** A type of initial view */
    public static final int TILE = 1;
    /** A type of initial view */
    public static final int HIDDEN = 2;

    public PdfCollection(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructs a PDF Collection.
     */
    public PdfCollection() {
        this(new PdfDictionary());
    }

    /**
     * Sets the Collection schema dictionary.
     * @param schema	an overview of the collection fields
     */
    public PdfCollection setSchema(PdfCollectionSchema schema) {
        getPdfObject().put(PdfName.Schema, schema.getPdfObject());
        return this;
    }

    public PdfCollectionSchema getSchema(){
        return new PdfCollectionSchema(getPdfObject().getAsDictionary(PdfName.Schema));
    }

    /**
     * Identifies the document that will be initially presented
     * in the user interface.
     * @param documentName a string that identifies an entry in the EmbeddedFiles name tree
     */
    public PdfCollection setInitialDocument(String documentName) {
        getPdfObject().put(PdfName.D, new PdfString(documentName));
        return this;
    }

    public PdfString getInitialDocument() {
        return getPdfObject().getAsString(PdfName.D);
    }

    /**
     * Sets the initial view.
     * @param viewType
     */
    public PdfCollection setView(int viewType) {
        switch (viewType) {
            default:
                getPdfObject().put(PdfName.View, PdfName.D);
                break;
            case TILE:
                getPdfObject().put(PdfName.View, PdfName.T);
                break;
            case HIDDEN:
                getPdfObject().put(PdfName.View, PdfName.H);
                break;
        }
        return this;
    }

    /**
     * @deprecated  Will always return null. The return will be changed to PdfName in 7.2.
     *              Use {@code getPdfObject().getAsName(PdfName.View)},
     *              or one of {@link #isViewDetails()}, {@link #isViewTile()}, {@link #isViewHidden()}.
     */
    @Deprecated
    public PdfNumber getView() {
        return getPdfObject().getAsNumber(PdfName.View);
    }

    public boolean isViewDetails() {
        PdfName view = getPdfObject().getAsName(PdfName.View);
        return view == null || view.equals(PdfName.D);
    }

    public boolean isViewTile() {
        return PdfName.T.equals(getPdfObject().getAsName(PdfName.View));
    }

    public boolean isViewHidden() {
        return PdfName.H.equals(getPdfObject().getAsName(PdfName.View));
    }

    /**
     * Sets the Collection sort dictionary.
     * @param sort
     */
    public PdfCollection setSort(PdfCollectionSort sort){
        getPdfObject().put(PdfName.Sort, sort.getPdfObject());
        return this;
    }

    public PdfCollectionSort getSort() {
        return new PdfCollectionSort(getPdfObject().getAsDictionary(PdfName.Sort));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
