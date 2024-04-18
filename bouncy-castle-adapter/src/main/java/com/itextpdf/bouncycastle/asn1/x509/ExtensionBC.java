/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import org.bouncycastle.asn1.x509.Extension;

/**
 * Wrapper class for {@link Extension}.
 */
public class ExtensionBC extends ASN1EncodableBC implements IExtension {
    private static final ExtensionBC INSTANCE = new ExtensionBC(null);

    private static final ASN1ObjectIdentifierBC CRL_DISTRIBUTION_POINTS =
            new ASN1ObjectIdentifierBC(Extension.cRLDistributionPoints);

    private static final ASN1ObjectIdentifierBC ISSUING_DISTRIBUTION_POINT =
            new ASN1ObjectIdentifierBC(Extension.issuingDistributionPoint);

    private static final ASN1ObjectIdentifierBC AUTHORITY_INFO_ACCESS =
            new ASN1ObjectIdentifierBC(Extension.authorityInfoAccess);

    private static final ASN1ObjectIdentifierBC BASIC_CONSTRAINTS =
            new ASN1ObjectIdentifierBC(Extension.basicConstraints);

    private static final ASN1ObjectIdentifierBC KEY_USAGE =
            new ASN1ObjectIdentifierBC(Extension.keyUsage);

    private static final ASN1ObjectIdentifierBC EXTENDED_KEY_USAGE =
            new ASN1ObjectIdentifierBC(Extension.extendedKeyUsage);

    private static final ASN1ObjectIdentifierBC AUTHORITY_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBC(Extension.authorityKeyIdentifier);

    private static final ASN1ObjectIdentifierBC SUBJECT_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBC(Extension.subjectKeyIdentifier);

    /**
     * Creates new wrapper instance for {@link Extension}.
     *
     * @param extension {@link Extension} to be wrapped
     */
    public ExtensionBC(Extension extension) {
        super(extension);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link ExtensionBC} instance.
     */
    public static ExtensionBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link Extension}.
     */
    public Extension getExtension() {
        return (Extension) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getCRlDistributionPoints() {
        return CRL_DISTRIBUTION_POINTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIssuingDistributionPoint() {
        return ISSUING_DISTRIBUTION_POINT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getAuthorityInfoAccess() {
        return AUTHORITY_INFO_ACCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getBasicConstraints() {
        return BASIC_CONSTRAINTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getKeyUsage() {
        return KEY_USAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getExtendedKeyUsage() {
        return EXTENDED_KEY_USAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getAuthorityKeyIdentifier() {
        return AUTHORITY_KEY_IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getSubjectKeyIdentifier() {
        return SUBJECT_KEY_IDENTIFIER;
    }
}
