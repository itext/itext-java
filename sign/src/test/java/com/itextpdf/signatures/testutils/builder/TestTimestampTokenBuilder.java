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
package com.itextpdf.signatures.testutils.builder;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;
import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponseGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenGenerator;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.signatures.DigestAlgorithms;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestTimestampTokenBuilder {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SIGN_ALG = "SHA256withRSA";

    // just a more or less random oid of timestamp policy
    private static final String POLICY_OID = "1.3.6.1.4.1.45794.1.1";

    private List<Certificate> tsaCertificateChain;
    private PrivateKey tsaPrivateKey;

    public TestTimestampTokenBuilder(List<Certificate> tsaCertificateChain, PrivateKey tsaPrivateKey) {
        if (tsaCertificateChain.isEmpty()) {
            throw new IllegalArgumentException("tsaCertificateChain shall not be empty");
        }
        this.tsaCertificateChain = tsaCertificateChain;
        this.tsaPrivateKey = tsaPrivateKey;
    }

    public byte[] createTimeStampToken(ITimeStampRequest request)
            throws AbstractOperatorCreationException, AbstractTSPException, IOException, CertificateEncodingException {
        ITimeStampTokenGenerator tsTokGen = createTimeStampTokenGenerator(tsaPrivateKey,
                tsaCertificateChain.get(0), SIGN_ALG, "SHA1", POLICY_OID);
        tsTokGen.setAccuracySeconds(1);

        // TODO setting this is somewhat wrong. Acrobat and openssl recognize timestamp tokens generated with this
        //  line as corrupted
        // openssl error message: 2304:error:2F09506F:time stamp routines:INT_TS_RESP_VERIFY_TOKEN:tsa name
        // mismatch:ts_rsp_verify.c:476:
//        tsTokGen.setTSA(new GeneralName(new X500Name(PrincipalUtil.getIssuerX509Principal(tsCertificate).getName())));

        tsTokGen.addCertificates(FACTORY.createJcaCertStore(tsaCertificateChain));

        // should be unique for every timestamp
        BigInteger serialNumber = new BigInteger(String.valueOf(SystemUtil.getTimeBasedSeed()));
        Date genTime = DateTimeUtil.getCurrentTimeDate();
        ITimeStampToken tsToken = tsTokGen.generate(request, serialNumber, genTime);
        return tsToken.getEncoded();
    }

    public byte[] createTSAResponse(byte[] requestBytes, String signatureAlgorithm, String allowedDigest) {
        try {
            String digestForTsSigningCert = DigestAlgorithms.getAllowedDigest(allowedDigest);
            ITimeStampTokenGenerator tokenGenerator = createTimeStampTokenGenerator(tsaPrivateKey,
                    tsaCertificateChain.get(0), signatureAlgorithm, allowedDigest, POLICY_OID);

            Set<String> algorithms = new HashSet<>(Collections.singletonList(digestForTsSigningCert));
            ITimeStampResponseGenerator generator = FACTORY.createTimeStampResponseGenerator(tokenGenerator,
                    algorithms);
            ITimeStampRequest request = FACTORY.createTimeStampRequest(requestBytes);
            return generator.generate(request, request.getNonce(), new Date()).getEncoded();
        } catch (Exception e) {
            return null;
        }
    }

    private static ITimeStampTokenGenerator createTimeStampTokenGenerator(PrivateKey pk, Certificate cert,
            String signatureAlgorithm, String allowedDigest, String policyOid)
            throws AbstractTSPException, AbstractOperatorCreationException, CertificateEncodingException {
        IContentSigner signer = FACTORY.createJcaContentSignerBuilder(signatureAlgorithm).build(pk);
        IDigestCalculatorProvider digestCalcProviderProvider = FACTORY.createJcaDigestCalculatorProviderBuilder()
                .build();
        ISignerInfoGenerator siGen =
                FACTORY.createJcaSignerInfoGeneratorBuilder(digestCalcProviderProvider)
                        .build(signer, (X509Certificate) cert);

        String digestForTsSigningCert = DigestAlgorithms.getAllowedDigest(allowedDigest);
        IDigestCalculator dgCalc = digestCalcProviderProvider.get(
                FACTORY.createAlgorithmIdentifier(FACTORY.createASN1ObjectIdentifier(digestForTsSigningCert)));
        IASN1ObjectIdentifier policy = FACTORY.createASN1ObjectIdentifier(policyOid);
        return FACTORY.createTimeStampTokenGenerator(siGen, dgCalc, policy);
    }
}
