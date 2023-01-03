/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
}
