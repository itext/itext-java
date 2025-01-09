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
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

public class PdfCollectionSchema extends PdfObjectWrapper<PdfDictionary>{


	public PdfCollectionSchema(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a Collection Schema dictionary.
     */
    public PdfCollectionSchema() {
        this(new PdfDictionary());
    }

    /**
     * Adds a Collection field to the Schema.
     *
     * @param name the name of the collection field
     * @param field a Collection Field
     * @return this instance to support fluent interface
     */
    public PdfCollectionSchema addField(String name, PdfCollectionField field) {
        getPdfObject().put(new PdfName(name), field.getPdfObject());
        return this;
    }

    /**
     * Retrieves a Collection field from the Schema.
     *
     * @param name is the name of the collection field
     * @return a {@link PdfCollectionField Collection field}
     */
    public PdfCollectionField getField(String name) {
        return new PdfCollectionField(getPdfObject().getAsDictionary(new PdfName(name)));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
