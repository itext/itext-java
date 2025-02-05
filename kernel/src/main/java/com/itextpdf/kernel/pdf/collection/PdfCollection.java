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
package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfCollection extends PdfObjectWrapper<PdfDictionary> {


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
     *
     * @param schema	an overview of the collection fields
     * @return this instance to support fluent interface
     */
    public PdfCollection setSchema(PdfCollectionSchema schema) {
        getPdfObject().put(PdfName.Schema, schema.getPdfObject());
        return this;
    }

    /**
     * Gets the Collection schema dictionary.
     *
     * @return the Collection schema dictionary
     */
    public PdfCollectionSchema getSchema(){
        return new PdfCollectionSchema(getPdfObject().getAsDictionary(PdfName.Schema));
    }

    /**
     * Identifies the document that will be initially presented
     * in the user interface.
     *
     * @param documentName a string that identifies an entry in the EmbeddedFiles name tree
     * @return this instance to support fluent interface
     */
    public PdfCollection setInitialDocument(String documentName) {
        getPdfObject().put(PdfName.D, new PdfString(documentName));
        return this;
    }

    /**
     * Retrieves the document that will be initially presented
     * in the user interface.
     *
     * @return a pdf string that identifies an entry in the EmbeddedFiles name tree
     */
    public PdfString getInitialDocument() {
        return getPdfObject().getAsString(PdfName.D);
    }

    /**
     * Sets the initial view.
     *
     * @param viewType is a type of view
     * @return this instance to support fluent interface
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
     * Check if view is in details mode.
     *
     * @return true if view is in details mode and false otherwise
     */
    public boolean isViewDetails() {
        PdfName view = getPdfObject().getAsName(PdfName.View);
        return view == null || view.equals(PdfName.D);
    }

    /**
     * Check if view is in tile mode.
     *
     * @return true if view is in tile mode and false otherwise
     */
    public boolean isViewTile() {
        return PdfName.T.equals(getPdfObject().getAsName(PdfName.View));
    }

    /**
     * Check if view is hidden.
     *
     * @return true if view is hidden and false otherwise
     */
    public boolean isViewHidden() {
        return PdfName.H.equals(getPdfObject().getAsName(PdfName.View));
    }

    /**
     * Sets the Collection sort dictionary.
     *
     * @param sort is the Collection sort dictionary
     * @return this instance to support fluent interface
     */
    public PdfCollection setSort(PdfCollectionSort sort){
        getPdfObject().put(PdfName.Sort, sort.getPdfObject());
        return this;
    }

    /**
     * Getter for the Collection sort dictionary.
     *
     * @return the Collection sort
     */
    public PdfCollectionSort getSort() {
        return new PdfCollectionSort(getPdfObject().getAsDictionary(PdfName.Sort));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
