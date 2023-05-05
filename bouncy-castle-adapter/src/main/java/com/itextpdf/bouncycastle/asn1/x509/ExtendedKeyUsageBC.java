/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtendedKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;

/**
 * Wrapper class for {@link ExtendedKeyUsage}.
 */
public class ExtendedKeyUsageBC extends ASN1EncodableBC implements IExtendedKeyUsage {
    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param extendedKeyUsage {@link ExtendedKeyUsage} to be wrapped
     */
    public ExtendedKeyUsageBC(ExtendedKeyUsage extendedKeyUsage) {
        super(extendedKeyUsage);
    }

    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param purposeId KeyPurposeId wrapper
     */
    public ExtendedKeyUsageBC(IKeyPurposeId purposeId) {
        super(new ExtendedKeyUsage(((KeyPurposeIdBC) purposeId).getKeyPurposeId()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ExtendedKeyUsage}.
     */
    public ExtendedKeyUsage getExtendedKeyUsage() {
        return (ExtendedKeyUsage) getEncodable();
    }
}
