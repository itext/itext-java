package com.itextpdf.signatures.testutils.builder;

import com.itextpdf.io.util.DateTimeUtil;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.Req;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class TestOcspResponseBuilder {

    private static final String SIGN_ALG = "SHA256withRSA";

    private BasicOCSPRespBuilder responseBuilder;
    private X509Certificate caCert;
    private CertificateStatus certificateStatus = CertificateStatus.GOOD;
    private Calendar thisUpdate = DateTimeUtil.getCurrentTimeCalendar();
    private Calendar nextUpdate = DateTimeUtil.getCurrentTimeCalendar();

    public TestOcspResponseBuilder(X509Certificate caCert) throws CertificateEncodingException {
        this.caCert = caCert;
        X500Name issuerDN = new X500Name(PrincipalUtil.getIssuerX509Principal(caCert).getName());
        thisUpdate = DateTimeUtil.addDaysToCalendar(thisUpdate, -1);
        nextUpdate = DateTimeUtil.addDaysToCalendar(nextUpdate, 30);
        responseBuilder = new BasicOCSPRespBuilder(new RespID(issuerDN));
    }

    public void setCertificateStatus(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public void setThisUpdate(Calendar thisUpdate) {
        this.thisUpdate = thisUpdate;
    }

    public void setNextUpdate(Calendar nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public byte[] makeOcspResponse(byte[] requestBytes, PrivateKey caPrivateKey) throws IOException, CertificateException, OperatorCreationException, OCSPException {
        OCSPReq ocspRequest = new OCSPReq(requestBytes);
        Req[] requestList = ocspRequest.getRequestList();

        Extension extNonce = ocspRequest.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        if (extNonce != null) {
            responseBuilder.setResponseExtensions(new Extensions(extNonce));
        }

        for (Req req : requestList) {
            responseBuilder.addResponse(req.getCertID(), certificateStatus, thisUpdate.getTime(), nextUpdate.getTime(), null);
        }


        Date time = DateTimeUtil.getCurrentTimeDate();

        X509CertificateHolder[] chain = {new JcaX509CertificateHolder(caCert)};
        ContentSigner signer = new JcaContentSignerBuilder(SIGN_ALG).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(caPrivateKey);
        BasicOCSPResp ocspResponse = responseBuilder.build(signer, chain, time);
//        return new OCSPRespBuilder().build(ocspResult, ocspResponse).getEncoded();
        return ocspResponse.getEncoded();
    }
}
