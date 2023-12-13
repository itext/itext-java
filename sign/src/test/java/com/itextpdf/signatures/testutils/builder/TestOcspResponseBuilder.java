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

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class TestOcspResponseBuilder {

    private static final String SIGN_ALG = "SHA256withRSA";

    private BasicOCSPRespBuilder responseBuilder;
    private X509Certificate issuerCert;
    private PrivateKey issuerPrivateKey;
    private CertificateStatus certificateStatus = CertificateStatus.GOOD;
    private Calendar thisUpdate = DateTimeUtil.getCurrentTimeCalendar();
    private Calendar nextUpdate = DateTimeUtil.getCurrentTimeCalendar();

    public TestOcspResponseBuilder(X509Certificate issuerCert, PrivateKey issuerPrivateKey)
            throws CertificateEncodingException {
        this.issuerCert = issuerCert;
        this.issuerPrivateKey = issuerPrivateKey;
        X500Name subjectDN = new X500Name(PrincipalUtil.getSubjectX509Principal(issuerCert).getName());
        thisUpdate = DateTimeUtil.addDaysToCalendar(thisUpdate, -1);
        nextUpdate = DateTimeUtil.addDaysToCalendar(nextUpdate, 30);
        responseBuilder = new BasicOCSPRespBuilder(new RespID(subjectDN));
    }

    public X509Certificate getIssuerCert() {
        return issuerCert;
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

    public byte[] makeOcspResponse(byte[] requestBytes) throws IOException, CertificateException,
            OperatorCreationException, OCSPException {
        BasicOCSPResp ocspResponse = makeOcspResponseObject(requestBytes);
        return ocspResponse.getEncoded();
    }

    public BasicOCSPResp makeOcspResponseObject(byte[] requestBytes) throws IOException, CertificateException,
            OperatorCreationException, OCSPException {
        OCSPReq ocspRequest = new OCSPReq(requestBytes);
        Req[] requestList = ocspRequest.getRequestList();

        Extension extNonce = ocspRequest.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        if (extNonce != null) {
            responseBuilder.setResponseExtensions(new Extensions(extNonce));
        }

        for (Req req : requestList) {
            responseBuilder.addResponse(req.getCertID(), certificateStatus, thisUpdate.getTime(),
                    nextUpdate.getTime(), null);
        }

        Date time = DateTimeUtil.getCurrentTimeDate();

        X509CertificateHolder[] chain = {new JcaX509CertificateHolder(issuerCert)};
        ContentSigner signer = new JcaContentSignerBuilder(SIGN_ALG).setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(issuerPrivateKey);
        return responseBuilder.build(signer, chain, time);
    }
}
