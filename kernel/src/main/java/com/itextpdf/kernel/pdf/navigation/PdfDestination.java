/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.pdf.IPdfNameTreeAccess;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;


public abstract class PdfDestination extends PdfObjectWrapper<PdfObject> {


    protected PdfDestination(PdfObject pdfObject) {
        super(pdfObject);
    }

    public abstract PdfObject getDestinationPage(IPdfNameTreeAccess names);


    /**
     * Creates {@link PdfDestination} implementation based on provided {@link PdfObject}.
     *
     * @param pdfObject {@link PdfObject} from which {@link PdfDestination} shall be created
     *
     * @return created {@link PdfDestination} implementation
     */
    public static PdfDestination makeDestination(PdfObject pdfObject) {
        return makeDestination(pdfObject, true);
    }

    /**
     * Creates {@link PdfDestination} implementation based on provided {@link PdfObject}.
     *
     * @param pdfObject {@link PdfObject} from which {@link PdfDestination} shall be created
     * @param throwException if {@code true}, throws exception in case of invalid parameter
     *
     * @return created {@link PdfDestination} implementation
     */
    public static PdfDestination makeDestination(PdfObject pdfObject, boolean throwException) {
        if (pdfObject.getType() == PdfObject.STRING) {
            return new PdfStringDestination((PdfString) pdfObject);
        } else if (pdfObject.getType() == PdfObject.NAME) {
            return new PdfNamedDestination((PdfName) pdfObject);
        } else if (pdfObject.getType() == PdfObject.ARRAY) {
            PdfArray destArray = (PdfArray) pdfObject;
            if (destArray.isEmpty()) {
                if (throwException) {
                    throw new IllegalArgumentException();
                } else {
                    return null;
                }
            } else {
                PdfObject firstObj = destArray.get(0);
                // In case of explicit destination for remote go-to action this is a page number
                if (firstObj.isNumber()) {
                    return new PdfExplicitRemoteGoToDestination(destArray);
                }
                // In case of explicit destination for not remote go-to action this is a page dictionary
                if (firstObj.isDictionary() && PdfName.Page.equals(((PdfDictionary) firstObj).getAsName(PdfName.Type))) {
                    return new PdfExplicitDestination(destArray);
                }
                // In case of structure destination this is a struct element dictionary or a string ID. Type is not required for structure elements
                return new PdfStructureDestination(destArray);
            }
        } else if (throwException) {
            throw new UnsupportedOperationException();
        } else {
            return null;
        }
    }
}
