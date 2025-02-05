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
package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;

/**
 * Wrapper class for {@link OtherHashAlgAndValue}.
 */
public class OtherHashAlgAndValueBCFips extends ASN1EncodableBCFips implements IOtherHashAlgAndValue {
    /**
     * Creates new wrapper instance for {@link OtherHashAlgAndValue}.
     *
     * @param otherHashAlgAndValue {@link OtherHashAlgAndValue} to be wrapped
     */
    public OtherHashAlgAndValueBCFips(OtherHashAlgAndValue otherHashAlgAndValue) {
        super(otherHashAlgAndValue);
    }

    /**
     * Creates new wrapper instance for {@link OtherHashAlgAndValue}.
     *
     * @param algorithmIdentifier AlgorithmIdentifier wrapper
     * @param octetString         ASN1OctetString wrapper
     */
    public OtherHashAlgAndValueBCFips(IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        this(new OtherHashAlgAndValue(
                ((AlgorithmIdentifierBCFips) algorithmIdentifier).getAlgorithmIdentifier(),
                ((ASN1OctetStringBCFips) octetString).getOctetString()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OtherHashAlgAndValue}.
     */
    public OtherHashAlgAndValue getOtherHashAlgAndValue() {
        return (OtherHashAlgAndValue) getEncodable();
    }
}
