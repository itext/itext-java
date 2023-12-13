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
