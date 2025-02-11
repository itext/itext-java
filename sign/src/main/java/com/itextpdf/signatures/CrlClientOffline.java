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

import com.itextpdf.kernel.exceptions.PdfException;
import java.security.cert.CRL;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of the CrlClient that handles offline
 * Certificate Revocation Lists.
 */
public class CrlClientOffline implements ICrlClient {

    /**
     * The CRL as a byte array.
     */
    private List<byte[]> crls = new ArrayList<>();

    /**
     * Creates an instance of a CrlClient in case you
     * have a local cache of the Certificate Revocation List.
     *
     * @param crlEncoded the CRL bytes
     */
    public CrlClientOffline(byte[] crlEncoded) {
        crls.add(crlEncoded);
    }

    /**
     * Creates an instance of a CrlClient in case you
     * have a local cache of the Certificate Revocation List.
     *
     * @param crl a CRL object
     */
    public CrlClientOffline(CRL crl) {
        try {
            crls.add(((X509CRL) crl).getEncoded());
        } catch (Exception ex) {
            throw new PdfException(ex);
        }
    }

    /**
     * Returns the CRL bytes (the parameters are ignored).
     *
     * @see ICrlClient#getEncoded(java.security.cert.X509Certificate, java.lang.String)
     */
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
        return crls;
    }

}
