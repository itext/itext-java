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
package com.itextpdf.signatures.testutils.client;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestTimestampTokenBuilder;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

public class TestTsaClient implements ITSAClient {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String DIGEST_ALG = "SHA256";
    private final PrivateKey tsaPrivateKey;
    private List<Certificate> tsaCertificateChain;

    public TestTsaClient(List<Certificate> tsaCertificateChain, PrivateKey tsaPrivateKey) {
        this.tsaCertificateChain = tsaCertificateChain;
        this.tsaPrivateKey = tsaPrivateKey;
    }

    @Override
    public int getTokenSizeEstimate() {
        return 4096;
    }

    @Override
    public MessageDigest getMessageDigest() throws GeneralSecurityException {
        return SignTestPortUtil.getMessageDigest(DIGEST_ALG);
    }

    @Override
    public byte[] getTimeStampToken(byte[] imprint) throws Exception {
        ITimeStampRequestGenerator tsqGenerator = BOUNCY_CASTLE_FACTORY.createTimeStampRequestGenerator();
        tsqGenerator.setCertReq(true);
        BigInteger nonce = BigInteger.valueOf(SystemUtil.getTimeBasedSeed());
        ITimeStampRequest request = tsqGenerator.generate(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(DigestAlgorithms.getAllowedDigest(DIGEST_ALG)), imprint, nonce);

        return new TestTimestampTokenBuilder(tsaCertificateChain, tsaPrivateKey).createTimeStampToken(request);
    }
}
