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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;


public class PdfCollectionItem extends PdfObjectWrapper<PdfDictionary> {

	private PdfCollectionSchema schema;

    public PdfCollectionItem(PdfCollectionSchema schema) {
        super(new PdfDictionary());
        this.schema = schema;
    }

    /**
     * Sets the value of the collection item.
     *
     * @param key is a key with which the specified value is to be associated
     * @param value is a value to be associated with the specified key
     * @return this instance to support fluent interface
     */
    public PdfCollectionItem addItem(String key, String value) {
        PdfCollectionField field = schema.getField(key);
        getPdfObject().put(new PdfName(key), field.getValue(value));
        return this;
    }

    /**
     * Sets the date value of the collection item.
     *
     * @param key is a key with which the specified date value is to be associated
     * @param date is a {@link PdfDate PDF date} value to be associated with the specified key
     */
    public void addItem(String key, PdfDate date) {
        PdfCollectionField field = schema.getField(key);
        if (field.subType == PdfCollectionField.DATE) {
            getPdfObject().put(new PdfName(key), date.getPdfObject());
        }
    }

    /**
     * Sets the number value of the collection item.
     *
     * @param key is a key with which the specified number value is to be associated
     * @param number is a {@link PdfNumber PDF number} value to be associated with the specified key
     */
    public void addItem(String key, PdfNumber number) {
        PdfCollectionField field = schema.getField(key);
        if (field.subType == PdfCollectionField.NUMBER) {
            getPdfObject().put(new PdfName(key), number);
        }
    }

    /**
     * Adds a prefix for the Collection item.
     * You can only use this method after you have set the value of the item.
     *
     * @param key is a key identifying the Collection item
     * @param prefix is a prefix to be added
     * @return this instance to support fluent interface
     */
    public PdfCollectionItem setPrefix(String key, String prefix) {
        PdfName fieldName = new PdfName(key);
        PdfObject obj = getPdfObject().get(fieldName);
        if (obj == null) {
            throw new PdfException(KernelExceptionMessageConstant.YOU_MUST_SET_A_VALUE_BEFORE_ADDING_A_PREFIX);
        }
        PdfDictionary subItem = new PdfDictionary();
        subItem.put(PdfName.D, obj);
        subItem.put(PdfName.P, new PdfString(prefix));
        getPdfObject().put(fieldName, subItem);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
