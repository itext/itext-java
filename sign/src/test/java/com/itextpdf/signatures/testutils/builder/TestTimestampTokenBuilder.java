/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.kernel.crypto.DigestAlgorithms;

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
