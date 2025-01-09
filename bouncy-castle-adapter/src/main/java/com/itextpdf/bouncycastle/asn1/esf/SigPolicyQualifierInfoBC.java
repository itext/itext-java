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
package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.DERIA5StringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

/**
 * Wrapper class for {@link SigPolicyQualifierInfo}.
 */
public class SigPolicyQualifierInfoBC extends ASN1EncodableBC implements ISigPolicyQualifierInfo {
    /**
     * Creates new wrapper instance for {@link SigPolicyQualifierInfo}.
     *
     * @param qualifierInfo {@link SigPolicyQualifierInfo} to be wrapped
     */
    public SigPolicyQualifierInfoBC(SigPolicyQualifierInfo qualifierInfo) {
        super(qualifierInfo);
    }

    /**
     * Creates new wrapper instance for {@link SigPolicyQualifierInfo}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param string           DERIA5String wrapper
     */
    public SigPolicyQualifierInfoBC(IASN1ObjectIdentifier objectIdentifier, IDERIA5String string) {
        this(new SigPolicyQualifierInfo(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((DERIA5StringBC) string).getDerIA5String()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigPolicyQualifierInfo}.
     */
    public SigPolicyQualifierInfo getSigPolicyQualifierInfo() {
        return (SigPolicyQualifierInfo) getEncodable();
    }
}
