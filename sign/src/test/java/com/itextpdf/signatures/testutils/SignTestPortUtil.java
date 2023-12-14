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
package com.itextpdf.signatures.testutils;

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
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class SignTestPortUtil {
    public static CertificateID generateCertificateId(X509Certificate issuerCert, BigInteger serialNumber, AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException, CertificateEncodingException, OCSPException {
        return new CertificateID(
                new JcaDigestCalculatorProviderBuilder().build().get(digestAlgorithmIdentifier),
                new JcaX509CertificateHolder(issuerCert), serialNumber);
    }

    public static OCSPReq generateOcspRequestWithNonce(CertificateID id) throws IOException, OCSPException {
        OCSPReqBuilder gen = new OCSPReqBuilder();
        gen.addRequest(id);

        Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(new DEROctetString(PdfEncryption.generateNewDocumentId()).getEncoded()));
        gen.setRequestExtensions(new Extensions(new Extension[]{ext}));
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
}
