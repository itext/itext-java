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
package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtendedKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;

/**
 * Wrapper class for {@link ExtendedKeyUsage}.
 */
public class ExtendedKeyUsageBCFips extends ASN1EncodableBCFips implements IExtendedKeyUsage {
    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param extendedKeyUsage {@link ExtendedKeyUsage} to be wrapped
     */
    public ExtendedKeyUsageBCFips(ExtendedKeyUsage extendedKeyUsage) {
        super(extendedKeyUsage);
    }

    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param purposeId KeyPurposeId wrapper
     */
    public ExtendedKeyUsageBCFips(IKeyPurposeId purposeId) {
        super(new ExtendedKeyUsage(((KeyPurposeIdBCFips) purposeId).getKeyPurposeId()));
    }

    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param purposeIds KeyPurposeId wrappers array
     */
    public ExtendedKeyUsageBCFips(IKeyPurposeId[] purposeIds) {
        super(new ExtendedKeyUsage(unwrapPurposeIds(purposeIds)));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ExtendedKeyUsage}.
     */
    public ExtendedKeyUsage getExtendedKeyUsage() {
        return (ExtendedKeyUsage) getEncodable();
    }

    private static KeyPurposeId[] unwrapPurposeIds(IKeyPurposeId[] purposeIds) {
        KeyPurposeId[] purposeIdsUnwrapped = new KeyPurposeId[purposeIds.length];
        for (int i = 0; i < purposeIds.length; ++i) {
            purposeIdsUnwrapped[i] = ((KeyPurposeIdBCFips) purposeIds[i]).getKeyPurposeId();
        }
        return purposeIdsUnwrapped;
    }
}
