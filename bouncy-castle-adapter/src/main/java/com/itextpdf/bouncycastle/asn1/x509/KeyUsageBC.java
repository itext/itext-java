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
package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;

import org.bouncycastle.asn1.x509.KeyUsage;

/**
 * Wrapper class for {@link KeyUsage}.
 */
public class KeyUsageBC extends ASN1EncodableBC implements IKeyUsage {
    private static final KeyUsageBC INSTANCE = new KeyUsageBC(null);

    /**
     * Creates new wrapper instance for {@link KeyUsage}.
     *
     * @param keyUsage {@link KeyUsage} to be wrapped
     */
    public KeyUsageBC(KeyUsage keyUsage) {
        super(keyUsage);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link KeyUsageBC} instance.
     */
    public static KeyUsageBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link KeyUsage}.
     */
    public KeyUsage getKeyUsage() {
        return (KeyUsage) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDigitalSignature() {
        return KeyUsage.digitalSignature;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNonRepudiation() {
        return KeyUsage.nonRepudiation;
    }
}
