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
package com.itextpdf.signatures.testutils.builder;

import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.signatures.DigestAlgorithms;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenGenerator;

public class TestTimestampTokenBuilder {
    private static final String SIGN_ALG = "SHA256withRSA";

    private List<Certificate> tsaCertificateChain;
    private PrivateKey tsaPrivateKey;

    public TestTimestampTokenBuilder(List<Certificate> tsaCertificateChain, PrivateKey tsaPrivateKey) {
        if (tsaCertificateChain.isEmpty()) {
            throw new IllegalArgumentException("tsaCertificateChain shall not be empty");
        }
        this.tsaCertificateChain = tsaCertificateChain;
        this.tsaPrivateKey = tsaPrivateKey;
    }

    public byte[] createTimeStampToken(TimeStampRequest request) throws OperatorCreationException, TSPException, IOException, CertificateEncodingException {
        ContentSigner signer = new JcaContentSignerBuilder(SIGN_ALG).build(tsaPrivateKey);
        DigestCalculatorProvider digestCalcProviderProvider = new JcaDigestCalculatorProviderBuilder().build();

        SignerInfoGenerator siGen =
                new JcaSignerInfoGeneratorBuilder(digestCalcProviderProvider)
                        .build(signer, (X509Certificate) tsaCertificateChain.get(0));

        // just a more or less random oid of timestamp policy
        ASN1ObjectIdentifier policy = new ASN1ObjectIdentifier("1.3.6.1.4.1.45794.1.1");

        String digestForTsSigningCert = DigestAlgorithms.getAllowedDigest("SHA1");
        DigestCalculator dgCalc = digestCalcProviderProvider.get(new AlgorithmIdentifier(new ASN1ObjectIdentifier(digestForTsSigningCert)));
        TimeStampTokenGenerator tsTokGen = new TimeStampTokenGenerator(siGen, dgCalc, policy);
        tsTokGen.setAccuracySeconds(1);

        // TODO setting this is somewhat wrong. Acrobat and openssl recognize timestamp tokens generated with this line as corrupted
        // openssl error message: 2304:error:2F09506F:time stamp routines:INT_TS_RESP_VERIFY_TOKEN:tsa name mismatch:ts_rsp_verify.c:476:
//        tsTokGen.setTSA(new GeneralName(new X500Name(PrincipalUtil.getIssuerX509Principal(tsCertificate).getName())));

        tsTokGen.addCertificates(new JcaCertStore(tsaCertificateChain));

        // should be unique for every timestamp
        BigInteger serialNumber = new BigInteger(String.valueOf(SystemUtil.getTimeBasedSeed()));
        Date genTime = DateTimeUtil.getCurrentTimeDate();
        TimeStampToken tsToken = tsTokGen.generate(request, serialNumber, genTime);
        return tsToken.getEncoded();
    }
}
