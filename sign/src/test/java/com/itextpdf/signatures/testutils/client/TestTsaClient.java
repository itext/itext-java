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
package com.itextpdf.signatures.testutils.client;

import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestTimestampTokenBuilder;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;

public class TestTsaClient implements ITSAClient {
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
        TimeStampRequestGenerator tsqGenerator = new TimeStampRequestGenerator();
        tsqGenerator.setCertReq(true);
        BigInteger nonce = BigInteger.valueOf(SystemUtil.getTimeBasedSeed());
        TimeStampRequest request = tsqGenerator.generate(new ASN1ObjectIdentifier(DigestAlgorithms.getAllowedDigest(DIGEST_ALG)), imprint, nonce);

        return new TestTimestampTokenBuilder(tsaCertificateChain, tsaPrivateKey).createTimeStampToken(request);
    }
}
