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
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;

import org.bouncycastle.asn1.x509.KeyUsage;

/**
 * Wrapper class for {@link KeyUsage}.
 */
public class KeyUsageBCFips extends ASN1EncodableBCFips implements IKeyUsage {
    private static final KeyUsageBCFips INSTANCE = new KeyUsageBCFips(null);

    /**
     * Creates new wrapper instance for {@link KeyUsage}.
     *
     * @param keyUsage {@link KeyUsage} to be wrapped
     */
    public KeyUsageBCFips(KeyUsage keyUsage) {
        super(keyUsage);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link KeyUsageBCFips} instance.
     */
    public static KeyUsageBCFips getInstance() {
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
