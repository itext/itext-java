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
package com.itextpdf.signatures.testutils.cert;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAuthorityKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.cert.AbstractCertIOException;
import com.itextpdf.commons.bouncycastle.cert.IX509ExtensionUtils;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509v3CertificateBuilder;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.SystemUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class TestCertificateBuilder {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    // requires corresponding key pairs to be used in this class
    private static final String signatureAlgorithm = "SHA256WithRSA";

    private PublicKey publicKey;
    private X509Certificate signingCert;
    private PrivateKey signingKey;
    private String subjectDN;
    private Date startDate;
    private Date endDate;

    public TestCertificateBuilder(PublicKey publicKey, X509Certificate signingCert, PrivateKey signingKey,
            String subjectDN) {
        this.publicKey = publicKey;
        this.signingCert = signingCert;
        this.signingKey = signingKey;
        this.subjectDN = subjectDN;
        this.startDate = DateTimeUtil.getCurrentTimeDate();
        this.endDate = DateTimeUtil.addDaysToDate(startDate, 365 * 100);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public X509Certificate buildAuthorizedOCSPResponderCert(boolean checkRevData)
            throws IOException, CertificateException, AbstractOperatorCreationException {
        IX500Name subjectDnName = FACTORY.createX500Name(subjectDN);
        // Using the current timestamp as the certificate serial number
        BigInteger certSerialNumber = new BigInteger(Long.toString(SystemUtil.getTimeBasedSeed()));
        IContentSigner contentSigner = FACTORY.createJcaContentSignerBuilder(signatureAlgorithm).build(signingKey);
        IJcaX509v3CertificateBuilder certBuilder = FACTORY.createJcaX509v3CertificateBuilder(signingCert,
                certSerialNumber, startDate, endDate, subjectDnName, publicKey);

        boolean ca = true;
        addExtension(FACTORY.createExtension().getBasicConstraints(), true, FACTORY.createBasicConstraints(ca),
                certBuilder);
        if (!checkRevData) {
            addExtension(FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspNoCheck(), false, FACTORY.createDERNull(),
                    certBuilder);
        }

        addExtension(FACTORY.createExtension().getKeyUsage(), false, FACTORY.createKeyUsage(FACTORY.createKeyUsage().
                getDigitalSignature() | FACTORY.createKeyUsage().getNonRepudiation()), certBuilder);

        addExtension(FACTORY.createExtension().getExtendedKeyUsage(), false,
                FACTORY.createExtendedKeyUsage(FACTORY.createKeyPurposeId().getIdKpOCSPSigning()),
                certBuilder);

        IDigestCalculatorProvider digestCalcProviderProvider = FACTORY.createJcaDigestCalculatorProviderBuilder()
                .build();
        String sha1DigestForPublicKey = "1.3.14.3.2.26";
        IX509ExtensionUtils x509ExtensionUtils = FACTORY.createX509ExtensionUtils(digestCalcProviderProvider.get(
                FACTORY.createAlgorithmIdentifier(FACTORY.createASN1ObjectIdentifier(sha1DigestForPublicKey))));
        byte[] signinigPublicKeyEncoded = signingCert.getPublicKey().getEncoded();
        ISubjectPublicKeyInfo issuerPublicKeyInfo = FACTORY.createSubjectPublicKeyInfo(signinigPublicKeyEncoded);
        IAuthorityKeyIdentifier authKeyIdentifier = x509ExtensionUtils.createAuthorityKeyIdentifier(
                issuerPublicKeyInfo);
        addExtension(FACTORY.createExtension().getAuthorityKeyIdentifier(), false, authKeyIdentifier,
                certBuilder);

        byte[] publicKeyEncoded = signingCert.getPublicKey().getEncoded();
        ISubjectPublicKeyInfo subjectPublicKeyInfo = FACTORY.createSubjectPublicKeyInfo(publicKeyEncoded);
        ISubjectKeyIdentifier subjectKeyIdentifier = x509ExtensionUtils.createSubjectKeyIdentifier(
                subjectPublicKeyInfo);
        addExtension(FACTORY.createExtension().getSubjectKeyIdentifier(), false, subjectKeyIdentifier, certBuilder);

        // -------------------------------------

        return FACTORY.createJcaX509CertificateConverter().setProvider(FACTORY.getProvider())
                .getCertificate(certBuilder.build(contentSigner));
    }

    private static void addExtension(IASN1ObjectIdentifier extensionOID, boolean critical,
            IASN1Encodable extensionValue, IJcaX509v3CertificateBuilder certBuilder) throws AbstractCertIOException {
        certBuilder.addExtension(extensionOID, critical, extensionValue);
    }
}
