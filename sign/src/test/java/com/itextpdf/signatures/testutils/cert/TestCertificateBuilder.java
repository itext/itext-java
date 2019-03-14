/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.io.util.SystemUtil;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class TestCertificateBuilder {
    private static final String signatureAlgorithm = "SHA256WithRSA"; // requires corresponding key pairs to be used in this class

    private PublicKey publicKey;
    private X509Certificate signingCert;
    private PrivateKey signingKey;
    private String subjectDN;
    private Date startDate;
    private Date endDate;

    public TestCertificateBuilder(PublicKey publicKey, X509Certificate signingCert, PrivateKey signingKey, String subjectDN) {
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
    public X509Certificate buildAuthorizedOCSPResponderCert() throws IOException, CertificateException, OperatorCreationException {
        X500Name subjectDnName = new X500Name(subjectDN);
        BigInteger certSerialNumber = new BigInteger(Long.toString(SystemUtil.getTimeBasedSeed())); // Using the current timestamp as the certificate serial number
        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(signingKey);
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(signingCert, certSerialNumber, startDate, endDate, subjectDnName, publicKey);

        // TODO generalize extensions setting
        // Extensions --------------------------

        boolean ca = true;
        addExtension(Extension.basicConstraints, true, new BasicConstraints(ca),
                certBuilder);

        addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck, false, DERNull.INSTANCE,
                certBuilder);

        addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation),
                certBuilder);

        addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_OCSPSigning),
                certBuilder);

        DigestCalculatorProvider digestCalcProviderProvider = new JcaDigestCalculatorProviderBuilder().build();
        String sha1DigestForPublicKey = "1.3.14.3.2.26";
        X509ExtensionUtils x509ExtensionUtils = new X509ExtensionUtils(digestCalcProviderProvider.get(new AlgorithmIdentifier(new ASN1ObjectIdentifier(sha1DigestForPublicKey))));
        byte[] signinigPublicKeyEncoded = signingCert.getPublicKey().getEncoded();
        SubjectPublicKeyInfo issuerPublicKeyInfo = new SubjectPublicKeyInfo(ASN1Sequence.getInstance(signinigPublicKeyEncoded));
        AuthorityKeyIdentifier authKeyIdentifier = x509ExtensionUtils.createAuthorityKeyIdentifier(issuerPublicKeyInfo);
        addExtension(Extension.authorityKeyIdentifier, false, authKeyIdentifier,
                certBuilder);

        byte[] publicKeyEncoded = signingCert.getPublicKey().getEncoded();
        SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(ASN1Sequence.getInstance(publicKeyEncoded));
        SubjectKeyIdentifier subjectKeyIdentifier = x509ExtensionUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo);
        addExtension(Extension.subjectKeyIdentifier, false, subjectKeyIdentifier,
                certBuilder);

        // -------------------------------------

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certBuilder.build(contentSigner));
    }

    private static void addExtension(ASN1ObjectIdentifier extensionOID, boolean critical, ASN1Encodable extensionValue, JcaX509v3CertificateBuilder certBuilder) throws CertIOException {
        certBuilder.addExtension(extensionOID, critical, extensionValue);
    }
}
