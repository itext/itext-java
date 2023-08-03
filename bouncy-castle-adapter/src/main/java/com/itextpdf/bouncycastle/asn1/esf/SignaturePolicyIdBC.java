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
package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;
import org.bouncycastle.asn1.esf.SignaturePolicyId;

/**
 * Wrapper class for {@link SignaturePolicyId}.
 */
public class SignaturePolicyIdBC extends ASN1EncodableBC implements ISignaturePolicyId {
    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param signaturePolicyId {@link SignaturePolicyId} to be wrapped
     */
    public SignaturePolicyIdBC(SignaturePolicyId signaturePolicyId) {
        super(signaturePolicyId);
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param algAndValue      OtherHashAlgAndValue wrapper
     * @param policyQualifiers SigPolicyQualifierInfo array
     */
    public SignaturePolicyIdBC(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue, SigPolicyQualifierInfo... policyQualifiers) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBC) algAndValue).getOtherHashAlgAndValue(),
                new SigPolicyQualifiers(policyQualifiers)));
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param algAndValue      OtherHashAlgAndValue wrapper
     */
    public SignaturePolicyIdBC(IASN1ObjectIdentifier objectIdentifier, IOtherHashAlgAndValue algAndValue) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBC) algAndValue).getOtherHashAlgAndValue()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SignaturePolicyId}.
     */
    public SignaturePolicyId getSignaturePolicyId() {
        return (SignaturePolicyId) getEncodable();
    }
}
