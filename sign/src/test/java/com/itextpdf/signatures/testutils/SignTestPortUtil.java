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
package com.itextpdf.signatures.testutils;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.kernel.pdf.PdfEncryption;
import com.itextpdf.signatures.BouncyCastleDigest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * Util class for internal usage only.
 */
public class SignTestPortUtil {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static ICertificateID generateCertificateId(X509Certificate issuerCert, BigInteger serialNumber,
                                                       IAlgorithmIdentifier digestAlgorithmIdentifier)
            throws AbstractOperatorCreationException, CertificateEncodingException, AbstractOCSPException {
        return BOUNCY_CASTLE_FACTORY.createCertificateID(
                BOUNCY_CASTLE_FACTORY.createJcaDigestCalculatorProviderBuilder().build().get(digestAlgorithmIdentifier),
                BOUNCY_CASTLE_FACTORY.createJcaX509CertificateHolder(issuerCert), serialNumber);
    }

    public static IOCSPReq generateOcspRequestWithNonce(ICertificateID id) throws IOException, AbstractOCSPException {
        IOCSPReqBuilder gen = BOUNCY_CASTLE_FACTORY.createOCSPReqBuilder();
        gen.addRequest(id);

        IExtension ext = BOUNCY_CASTLE_FACTORY.createExtension(
                BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspNonce(), false,
                BOUNCY_CASTLE_FACTORY.createDEROctetString(
                        BOUNCY_CASTLE_FACTORY.createDEROctetString(PdfEncryption.generateNewDocumentId()).getEncoded()));
        gen.setRequestExtensions(BOUNCY_CASTLE_FACTORY.createExtensions(ext));
        return gen.build();
    }

    public static MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException {
        return new BouncyCastleDigest().getMessageDigest(hashAlgorithm);
    }

    public static CRL parseCrlFromStream(InputStream input) throws CertificateException, CRLException {
        return CertificateFactory.getInstance("X.509").generateCRL(input);
    }

    public static KeyPairGenerator buildRSA2048KeyPairGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen;
    }

    public static <T> T getFirstElement(Collection<T> collection) {
        return collection.iterator().next();
    }
}
