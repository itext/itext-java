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
 * Interface to sign a document. The signing is fully done externally, including the container composition.
 */
public interface IExternalSignatureContainer {

    /**
     * Produces the container with the signature.
     * @param data the data to sign
     * @return a container with the signature and other objects, like CRL and OCSP. The container will generally be a PKCS7 one.
     * @throws GeneralSecurityException the general security exception
     */
    byte[] sign(InputStream data) throws GeneralSecurityException;

    /**
     * Modifies the signature dictionary to suit the container. At least the keys {@link PdfName#Filter} and
     * {@link PdfName#SubFilter} will have to be set.
     * @param signDic the signature dictionary
     */
    void modifySigningDictionary(PdfDictionary signDic);
}
