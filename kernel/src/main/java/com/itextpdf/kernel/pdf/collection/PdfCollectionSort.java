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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.util.Arrays;

public class PdfCollectionSort extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -3871923275239410475L;

    public PdfCollectionSort(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructs a PDF Collection Sort Dictionary.
     *
     * @param key the key of the field that will be used to sort entries
     */
    public PdfCollectionSort(String key) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.S, new PdfName(key));
    }

    /**
     * Constructs a PDF Collection Sort Dictionary.
     *
     * @param keys the keys of the fields that will be used to sort entries
     */
    public PdfCollectionSort(String[] keys) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.S, new PdfArray(Arrays.asList(keys), true));
    }

    /**
     * Defines the sort order of the field (ascending or descending).
     *
     * @param ascending true is the default, use false for descending order
     */
    public PdfCollectionSort setSortOrder(boolean ascending) {
        PdfObject obj = getPdfObject().get(PdfName.S);
        if (obj.isName()) {
            getPdfObject().put(PdfName.A, PdfBoolean.valueOf(ascending));
        } else {
            throw new PdfException(PdfException.YouHaveToDefineABooleanArrayForThisCollectionSortDictionary);
        }
        return this;
    }

    /**
     * Defines the sort order of the field (ascending or descending).
     *
     * @param ascending an array with every element corresponding with a name of a field.
     */
    public PdfCollectionSort setSortOrder(boolean[] ascending) {
        PdfObject obj = getPdfObject().get(PdfName.S);
        if (obj.isArray()) {
            if (((PdfArray) obj).size() != ascending.length) {
                throw new PdfException(PdfException.NumberOfBooleansInTheArrayDoesntCorrespondWithTheNumberOfFields);
            }
            getPdfObject().put(PdfName.A, new PdfArray(ascending));
            return this;
        } else {
            throw new PdfException(PdfException.YouNeedASingleBooleanForThisCollectionSortDictionary);
        }
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
