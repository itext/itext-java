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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.util.Arrays;

public class PdfCollectionSort extends PdfObjectWrapper<PdfDictionary> {


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
     * @return this instance to support fluent interface
     */
    public PdfCollectionSort setSortOrder(boolean ascending) {
        PdfObject obj = getPdfObject().get(PdfName.S);
        if (obj.isName()) {
            getPdfObject().put(PdfName.A, PdfBoolean.valueOf(ascending));
        } else {
            throw new PdfException(
                    KernelExceptionMessageConstant.YOU_HAVE_TO_DEFINE_A_BOOLEAN_ARRAY_FOR_THIS_COLLECTION_SORT_DICTIONARY);
        }
        return this;
    }

    /**
     * Defines the sort order of the field (ascending or descending).
     *
     * @param ascending an array with every element corresponding with a name of a field.
     * @return this instance to support fluent interface
     */
    public PdfCollectionSort setSortOrder(boolean[] ascending) {
        PdfObject obj = getPdfObject().get(PdfName.S);
        if (obj.isArray()) {
            if (((PdfArray) obj).size() != ascending.length) {
                throw new PdfException(
                        KernelExceptionMessageConstant.NUMBER_OF_BOOLEANS_IN_THE_ARRAY_DOES_NOT_CORRESPOND_WITH_THE_NUMBER_OF_FIELDS);
            }
            getPdfObject().put(PdfName.A, new PdfArray(ascending));
            return this;
        } else {
            throw new PdfException(
                    KernelExceptionMessageConstant.YOU_NEED_A_SINGLE_BOOLEAN_FOR_THIS_COLLECTION_SORT_DICTIONARY);
        }
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
