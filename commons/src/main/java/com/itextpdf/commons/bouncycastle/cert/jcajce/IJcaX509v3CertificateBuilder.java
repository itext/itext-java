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
package com.itextpdf.commons.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.AbstractCertIOException;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

/**
 * This interface represents the wrapper for JcaX509v3CertificateBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaX509v3CertificateBuilder {
    /**
     * Calls actual {@code build} method for the wrapped JcaX509v3CertificateBuilder object.
     *
     * @param contentSigner ContentSigner wrapper
     *
     * @return {IX509CertificateHolder} wrapper for built X509CertificateHolder object.
     */
    IX509CertificateHolder build(IContentSigner contentSigner);

    /**
     * Calls actual {@code addExtension} method for the wrapped JcaX509v3CertificateBuilder object.
     *
     * @param extensionOID   wrapper for the OID defining the extension type
     * @param critical       true if the extension is critical, false otherwise
     * @param extensionValue wrapped ASN.1 structure that forms the extension's value
     *
     * @return {@link IJcaX509v3CertificateBuilder} this wrapper object.
     *
     * @throws AbstractCertIOException CertIOException wrapper.
     */
    IJcaX509v3CertificateBuilder addExtension(IASN1ObjectIdentifier extensionOID, boolean critical,
            IASN1Encodable extensionValue) throws AbstractCertIOException;
}
