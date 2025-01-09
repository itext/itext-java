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
package com.itextpdf.bouncycastle.cert.jcajce;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.bouncycastle.cert.CertIOExceptionBC;
import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.operator.ContentSignerBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509v3CertificateBuilder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;

/**
 * Wrapper class for {@link JcaX509v3CertificateBuilder}.
 */
public class JcaX509v3CertificateBuilderBC implements IJcaX509v3CertificateBuilder {
    private final JcaX509v3CertificateBuilder certificateBuilder;

    /**
     * Creates new wrapper instance for {@link JcaX509v3CertificateBuilder}.
     *
     * @param certificateBuilder {@link JcaX509v3CertificateBuilder} to be wrapped
     */
    public JcaX509v3CertificateBuilderBC(JcaX509v3CertificateBuilder certificateBuilder) {
        this.certificateBuilder = certificateBuilder;
    }

    /**
     * Creates new wrapper instance for {@link JcaX509v3CertificateBuilder}.
     *
     * @param signingCert      X509Certificate to create {@link JcaX509v3CertificateBuilder}
     * @param certSerialNumber BigInteger to create {@link JcaX509v3CertificateBuilder}
     * @param startDate        start date to create {@link JcaX509v3CertificateBuilder}
     * @param endDate          end date to create {@link JcaX509v3CertificateBuilder}
     * @param subjectDnName    X500Name wrapper to create {@link JcaX509v3CertificateBuilder}
     * @param publicKey        PublicKey to create {@link JcaX509v3CertificateBuilder}
     */
    public JcaX509v3CertificateBuilderBC(X509Certificate signingCert, BigInteger certSerialNumber, Date startDate,
            Date endDate, IX500Name subjectDnName, PublicKey publicKey) {
        this(new JcaX509v3CertificateBuilder(signingCert, certSerialNumber, startDate, endDate,
                ((X500NameBC) subjectDnName).getX500Name(), publicKey));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaX509v3CertificateBuilder}.
     */
    public JcaX509v3CertificateBuilder getCertificateBuilder() {
        return certificateBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX509CertificateHolder build(IContentSigner contentSigner) {
        return new X509CertificateHolderBC(certificateBuilder.build(
                ((ContentSignerBC) contentSigner).getContentSigner()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaX509v3CertificateBuilder addExtension(IASN1ObjectIdentifier extensionOID, boolean critical,
            IASN1Encodable extensionValue) throws CertIOExceptionBC {
        try {
            certificateBuilder.addExtension(((ASN1ObjectIdentifierBC) extensionOID).getASN1ObjectIdentifier(),
                    critical, ((ASN1EncodableBC) extensionValue).getEncodable());
            return this;
        } catch (CertIOException e) {
            throw new CertIOExceptionBC(e);
        }
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaX509v3CertificateBuilderBC that = (JcaX509v3CertificateBuilderBC) o;
        return Objects.equals(certificateBuilder, that.certificateBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateBuilder.toString();
    }
}
