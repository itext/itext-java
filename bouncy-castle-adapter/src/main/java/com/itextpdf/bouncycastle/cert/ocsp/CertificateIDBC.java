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
package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorProviderBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.math.BigInteger;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;

/**
 * Wrapper class for {@link CertificateID}.
 */
public class CertificateIDBC implements ICertificateID {
    private static final CertificateIDBC INSTANCE = new CertificateIDBC(null);

    private static final AlgorithmIdentifierBC HASH_SHA1 = new AlgorithmIdentifierBC(CertificateID.HASH_SHA1);

    private final CertificateID certificateID;

    /**
     * Creates new wrapper instance for {@link CertificateID}.
     *
     * @param certificateID {@link CertificateID} to be wrapped
     */
    public CertificateIDBC(CertificateID certificateID) {
        this.certificateID = certificateID;
    }

    /**
     * Creates new wrapper instance for {@link CertificateID}.
     *
     * @param digestCalculator  DigestCalculator wrapper to create {@link CertificateID}
     * @param certificateHolder X509CertificateHolder wrapper to create {@link CertificateID}
     * @param bigInteger        BigInteger to create {@link CertificateID}
     * @throws OCSPExceptionBC if {@link OCSPException} occurs during {@link CertificateID} instance creation.
     */
    public CertificateIDBC(IDigestCalculator digestCalculator,
            IX509CertificateHolder certificateHolder, BigInteger bigInteger) throws OCSPExceptionBC {
        try {
            this.certificateID = new CertificateID(
                    ((DigestCalculatorBC) digestCalculator).getDigestCalculator(),
                    ((X509CertificateHolderBC) certificateHolder).getCertificateHolder(),
                    bigInteger);
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link CertificateIDBC} instance.
     */
    public static CertificateIDBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CertificateID}.
     */
    public CertificateID getCertificateID() {
        return certificateID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getHashAlgOID() {
        return new ASN1ObjectIdentifierBC(certificateID.getHashAlgOID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getHashSha1() {
        return HASH_SHA1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesIssuer(IX509CertificateHolder certificateHolder,
            IDigestCalculatorProvider provider) throws OCSPExceptionBC {
        try {
            return certificateID.matchesIssuer(
                    ((X509CertificateHolderBC) certificateHolder).getCertificateHolder(),
                    ((DigestCalculatorProviderBC) provider).getCalculatorProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getSerialNumber() {
        return certificateID.getSerialNumber();
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
        CertificateIDBC that = (CertificateIDBC) o;
        return Objects.equals(certificateID, that.certificateID);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateID);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateID.toString();
    }
}
