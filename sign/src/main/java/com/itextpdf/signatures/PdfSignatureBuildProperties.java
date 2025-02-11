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
package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

/**
 * Dictionary that stores signature build properties.
 */
public class PdfSignatureBuildProperties extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates new PdfSignatureBuildProperties.
     */
    public PdfSignatureBuildProperties() {
        super(new PdfDictionary());
    }

    /**
     * Creates new PdfSignatureBuildProperties with preset values.
     *
     * @param dict PdfDictionary containing preset values
     */
    public PdfSignatureBuildProperties(PdfDictionary dict) {
        super(dict);
    }

    /**
     * Sets the signatureCreator property in the underlying
     * {@link PdfSignatureApp} dictionary.
     *
     * @param name the signature creator's name to be set
     */
    public void setSignatureCreator(String name) {
        getPdfSignatureAppProperty().setSignatureCreator(name);
    }

    /**
     * Gets the {@link PdfSignatureApp} from this dictionary. If it
     * does not exist, it adds a new {@link PdfSignatureApp} and
     * returns this instance.
     *
     * @return {@link PdfSignatureApp}
     */
    private PdfSignatureApp getPdfSignatureAppProperty() {
        PdfDictionary appPropDic = getPdfObject().getAsDictionary(PdfName.App);

        if (appPropDic == null) {
            appPropDic = new PdfDictionary();
            getPdfObject().put(PdfName.App, appPropDic);
        }

        return new PdfSignatureApp(appPropDic);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
