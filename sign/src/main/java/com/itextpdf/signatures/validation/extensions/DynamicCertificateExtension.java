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
package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

/**
 * Certificate extension which is populated with additional dynamically changing validation related information.
 */
public class DynamicCertificateExtension extends CertificateExtension {

    private int certificateChainSize;

    /**
     * Create new instance of {@link CertificateExtension} using provided extension OID and value.
     *
     * @param extensionOid   {@link String}, which represents extension OID
     * @param extensionValue {@link IASN1Primitive}, which represents extension value
     */
    public DynamicCertificateExtension(String extensionOid, IASN1Primitive extensionValue) {
        super(extensionOid, extensionValue);
    }

    /**
     * Sets amount of certificates currently present in the chain.
     *
     * @param certificateChainSize amount of certificates currently present in the chain
     *
     * @return this {@link DynamicCertificateExtension} instance
     */
    public DynamicCertificateExtension withCertificateChainSize(int certificateChainSize) {
        this.certificateChainSize = certificateChainSize;
        return this;
    }

    /**
     * Gets amount of certificates currently present in the chain.
     *
     * @return amount of certificates currently present in the chain
     */
    public int getCertificateChainSize() {
        return certificateChainSize;
    }
}
