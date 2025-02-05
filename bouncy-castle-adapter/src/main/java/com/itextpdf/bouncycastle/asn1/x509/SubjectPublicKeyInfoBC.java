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
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * Wrapper class for {@link SubjectPublicKeyInfo}.
 */
public class SubjectPublicKeyInfoBC extends ASN1EncodableBC implements ISubjectPublicKeyInfo {
    /**
     * Creates new wrapper instance for {@link SubjectPublicKeyInfo}.
     *
     * @param subjectPublicKeyInfo {@link SubjectPublicKeyInfo} to be wrapped
     */
    public SubjectPublicKeyInfoBC(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        super(subjectPublicKeyInfo);
    }

    /**
     * Creates new wrapper instance for {@link SubjectPublicKeyInfo}.
     *
     * @param obj to get {@link SubjectPublicKeyInfo} instance to be wrapped
     */
    public SubjectPublicKeyInfoBC(Object obj) {
        super(SubjectPublicKeyInfo.getInstance(obj));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SubjectPublicKeyInfo}.
     */
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return (SubjectPublicKeyInfo) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getAlgorithm() {
        return new AlgorithmIdentifierBC(getSubjectPublicKeyInfo().getAlgorithm());
    }
}
