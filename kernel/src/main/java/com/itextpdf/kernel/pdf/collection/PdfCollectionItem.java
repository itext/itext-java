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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;


public class PdfCollectionItem extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -6471103872805179766L;
	private PdfCollectionSchema schema;

    public PdfCollectionItem(PdfCollectionSchema schema) {
        super(new PdfDictionary());
        this.schema = schema;
    }

    /**
     * Sets the value of the collection item.
     * @param key
     * @param value
     */
    public PdfCollectionItem addItem(String key, String value) {
        PdfCollectionField field = schema.getField(key);
        getPdfObject().put(new PdfName(key), field.getValue(value));
        return this;
    }

    /**
     * Sets the value of the collection item.
     * @param d
     */
    public void addItem(String key, PdfDate d) {
        PdfCollectionField field = schema.getField(key);
        if (field.subType == PdfCollectionField.DATE) {
            getPdfObject().put(new PdfName(key), d.getPdfObject());
        }
    }

    /**
     * Sets the value of the collection item.
     * @param n
     */
    public void addItem(String key, PdfNumber n) {
        PdfCollectionField field = schema.getField(key);
        if (field.subType == PdfCollectionField.NUMBER) {
            getPdfObject().put(new PdfName(key), n);
        }
    }

    /**
     * Adds a prefix for the Collection item.
     * You can only use this method after you have set the value of the item.
     * @param key
     * @param prefix
     */
    public PdfCollectionItem setPrefix(String key, String prefix) {
        PdfName fieldName = new PdfName(key);
        PdfObject obj = getPdfObject().get(fieldName);
        if (obj == null) {
            throw new PdfException(PdfException.YouMustSetAValueBeforeAddingAPrefix);
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
