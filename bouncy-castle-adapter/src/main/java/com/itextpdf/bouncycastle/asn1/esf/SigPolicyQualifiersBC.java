/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;

/**
 * Wrapper class for {@link SigPolicyQualifiers}.
 */
public class SigPolicyQualifiersBC extends ASN1EncodableBC implements ISigPolicyQualifiers {
    /**
     * Creates new wrapper instance for {@link SigPolicyQualifiers}.
     *
     * @param policyQualifiers {@link SigPolicyQualifiers} to be wrapped
     */
    public SigPolicyQualifiersBC(SigPolicyQualifiers policyQualifiers) {
        super(policyQualifiers);
    }

    /**
     * Creates new wrapper instance for {@link SigPolicyQualifiers}.
     *
     * @param qualifierInfo SigPolicyQualifierInfo array
     */
    public SigPolicyQualifiersBC(SigPolicyQualifierInfo... qualifierInfo) {
        super(new SigPolicyQualifiers(qualifierInfo));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigPolicyQualifiers}.
     */
    public SigPolicyQualifiers getSigPolityQualifiers() {
        return (SigPolicyQualifiers) getEncodable();
    }
}
