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
package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;

import java.math.BigInteger;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

/**
 * Wrapper class for {@link IssuerAndSerialNumber}.
 */
public class IssuerAndSerialNumberBCFips extends ASN1EncodableBCFips implements IIssuerAndSerialNumber {
    /**
     * Creates new wrapper instance for {@link IssuerAndSerialNumber}.
     *
     * @param issuerAndSerialNumber {@link IssuerAndSerialNumber} to be wrapped
     */
    public IssuerAndSerialNumberBCFips(IssuerAndSerialNumber issuerAndSerialNumber) {
        super(issuerAndSerialNumber);
    }

    /**
     * Creates new wrapper instance for {@link IssuerAndSerialNumber}.
     *
     * @param issuer X500Name wrapper to create {@link IssuerAndSerialNumber}
     * @param value  BigInteger to create {@link IssuerAndSerialNumber}
     */
    public IssuerAndSerialNumberBCFips(IX500Name issuer, BigInteger value) {
        super(new IssuerAndSerialNumber(((X500NameBCFips) issuer).getX500Name(), value));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link IssuerAndSerialNumber}.
     */
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return (IssuerAndSerialNumber) getEncodable();
    }
}
