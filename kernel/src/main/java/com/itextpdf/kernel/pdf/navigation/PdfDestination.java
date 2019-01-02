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
package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.Map;


public abstract class PdfDestination extends PdfObjectWrapper<PdfObject> {

    private static final long serialVersionUID = 8102903000978704308L;

    protected PdfDestination(PdfObject pdfObject) {
        super(pdfObject);
    }

    public abstract PdfObject getDestinationPage(Map<String, PdfObject> names);


    public static PdfDestination makeDestination(PdfObject pdfObject) {
        if (pdfObject.getType() == PdfObject.STRING) {
            return new PdfStringDestination((PdfString) pdfObject);
        } else if (pdfObject.getType() == PdfObject.NAME) {
            return new PdfNamedDestination((PdfName) pdfObject);
        } else if (pdfObject.getType() == PdfObject.ARRAY) {
            PdfArray destArray = (PdfArray) pdfObject;
            if (destArray.size() == 0) {
                throw new IllegalArgumentException();
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
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
