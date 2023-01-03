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

    // TODO generalize
    public X509Certificate buildAuthorizedOCSPResponderCert()
            throws IOException, CertificateException, AbstractOperatorCreationException {
        IX500Name subjectDnName = FACTORY.createX500Name(subjectDN);
        // Using the current timestamp as the certificate serial number
        BigInteger certSerialNumber = new BigInteger(Long.toString(SystemUtil.getTimeBasedSeed()));
        IContentSigner contentSigner = FACTORY.createJcaContentSignerBuilder(signatureAlgorithm).build(signingKey);
        IJcaX509v3CertificateBuilder certBuilder = FACTORY.createJcaX509v3CertificateBuilder(signingCert,
                certSerialNumber, startDate, endDate, subjectDnName, publicKey);

        // TODO generalize extensions setting
        // Extensions --------------------------

        boolean ca = true;
        addExtension(FACTORY.createExtension().getBasicConstraints(), true, FACTORY.createBasicConstraints(ca),
                certBuilder);
        addExtension(FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspNoCheck(), false, FACTORY.createDERNull(),
                certBuilder);

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
