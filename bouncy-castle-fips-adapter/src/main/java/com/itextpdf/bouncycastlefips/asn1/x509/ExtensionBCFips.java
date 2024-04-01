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
package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import org.bouncycastle.asn1.x509.Extension;

/**
 * Wrapper class for {@link Extension}.
 */
public class ExtensionBCFips extends ASN1EncodableBCFips implements IExtension {
    private static final ExtensionBCFips INSTANCE = new ExtensionBCFips(null);

    private static final ASN1ObjectIdentifierBCFips CRL_DISTRIBUTION_POINTS =
            new ASN1ObjectIdentifierBCFips(Extension.cRLDistributionPoints);

    private static final ASN1ObjectIdentifierBCFips ISSUING_DISTRIBUTION_POINT =
            new ASN1ObjectIdentifierBCFips(Extension.issuingDistributionPoint);

    private static final ASN1ObjectIdentifierBCFips AUTHORITY_INFO_ACCESS =
            new ASN1ObjectIdentifierBCFips(Extension.authorityInfoAccess);

    private static final ASN1ObjectIdentifierBCFips BASIC_CONSTRAINTS =
            new ASN1ObjectIdentifierBCFips(Extension.basicConstraints);

    private static final ASN1ObjectIdentifierBCFips KEY_USAGE =
            new ASN1ObjectIdentifierBCFips(Extension.keyUsage);

    private static final ASN1ObjectIdentifierBCFips EXTENDED_KEY_USAGE =
            new ASN1ObjectIdentifierBCFips(Extension.extendedKeyUsage);

    private static final ASN1ObjectIdentifierBCFips AUTHORITY_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBCFips(Extension.authorityKeyIdentifier);

    private static final ASN1ObjectIdentifierBCFips SUBJECT_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBCFips(Extension.subjectKeyIdentifier);

    /**
     * Creates new wrapper instance for {@link Extension}.
     *
     * @param extension {@link Extension} to be wrapped
     */
    public ExtensionBCFips(Extension extension) {
        super(extension);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link ExtensionBCFips} instance.
     */
    public static ExtensionBCFips getInstance() {
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
