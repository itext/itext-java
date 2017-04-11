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
        BigInteger nonce = BigInteger.valueOf(SystemUtil.getSystemTimeMillis());
        TimeStampRequest request = tsqGenerator.generate(new ASN1ObjectIdentifier(DigestAlgorithms.getAllowedDigest(DIGEST_ALG)), imprint, nonce);

        return new TestTimestampTokenBuilder(tsaCertificateChain, tsaPrivateKey).createTimeStampToken(request);
    }
}
