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

import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Produces a blank (or empty) signature. Useful for deferred signing with
 * MakeSignature.signExternalContainer().
 */
public class ExternalBlankSignatureContainer implements IExternalSignatureContainer {

    /* The Signature dictionary. Should contain values for /Filter and /SubFilter at minimum. */
    private PdfDictionary sigDic;

    /**
     * Creates an ExternalBlankSignatureContainer.
     * @param sigDic PdfDictionary containing signature iformation. /SubFilter and /Filter aren't set in this constructor.
     */
    public ExternalBlankSignatureContainer(PdfDictionary sigDic) {
        this.sigDic = sigDic;
    }

    /**
     * Creates an ExternalBlankSignatureContainer. This constructor will create the PdfDictionary for the
     * signature information and will insert the  /Filter and /SubFilter values into this dictionary.
     *
     * @param filter PdfName of the signature handler to use when validating this signature
     * @param subFilter PdfName that describes the encoding of the signature
     */
    public ExternalBlankSignatureContainer(PdfName filter, PdfName subFilter) {
        sigDic = new PdfDictionary();
        sigDic.put(PdfName.Filter, filter);
        sigDic.put(PdfName.SubFilter, subFilter);
    }

    @Override
    public byte[] sign(InputStream data) throws GeneralSecurityException {
        return new byte[0];
    }

    @Override
    public void modifySigningDictionary(PdfDictionary signDic) {
        signDic.putAll(sigDic);
    }
}
