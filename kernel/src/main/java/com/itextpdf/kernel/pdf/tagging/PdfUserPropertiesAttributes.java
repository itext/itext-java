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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import java.util.List;

public class PdfUserPropertiesAttributes extends PdfStructureAttributes {

    public PdfUserPropertiesAttributes(PdfDictionary attributesDict) {
        super(attributesDict);
    }

    public PdfUserPropertiesAttributes() {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.O, PdfName.UserProperties);
        getPdfObject().put(PdfName.P, new PdfArray());
    }

    public PdfUserPropertiesAttributes(List<PdfUserProperty> userProperties) {
        this();
        PdfArray arr = getPdfObject().getAsArray(PdfName.P);
        for (PdfUserProperty userProperty : userProperties) {
            arr.add(userProperty.getPdfObject());
        }
    }

    public PdfUserPropertiesAttributes addUserProperty(PdfUserProperty userProperty) {
        getPdfObject().getAsArray(PdfName.P).add(userProperty.getPdfObject());
        setModified();
        return this;
    }

    public PdfUserProperty getUserProperty(int i) {
        PdfDictionary propDict = getPdfObject().getAsArray(PdfName.P).getAsDictionary(i);
        if (propDict == null) {
            return null;
        }
        return new PdfUserProperty(propDict);
    }

    public PdfUserPropertiesAttributes removeUserProperty(int i) {
        getPdfObject().getAsArray(PdfName.P).remove(i);
        return this;
    }

    public int getNumberOfUserProperties() {
        return getPdfObject().getAsArray(PdfName.P).size();
    }
}

