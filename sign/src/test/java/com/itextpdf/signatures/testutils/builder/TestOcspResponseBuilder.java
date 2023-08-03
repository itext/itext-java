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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPRespBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.utils.DateTimeUtil;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class TestOcspResponseBuilder {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SIGN_ALG = "SHA256withRSA";

    private IBasicOCSPRespBuilder responseBuilder;
    private X509Certificate issuerCert;
    private PrivateKey issuerPrivateKey;
    private ICertificateStatus certificateStatus;
    private Calendar thisUpdate = DateTimeUtil.getCurrentTimeCalendar();
    private Calendar nextUpdate = DateTimeUtil.getCurrentTimeCalendar();

    public TestOcspResponseBuilder(X509Certificate issuerCert, PrivateKey issuerPrivateKey,
            ICertificateStatus certificateStatus) throws CertificateEncodingException, IOException {
        this.issuerCert = issuerCert;
        this.issuerPrivateKey = issuerPrivateKey;
        this.certificateStatus = certificateStatus;
        IX500Name subjectDN = FACTORY.createX500Name(issuerCert);
        thisUpdate = DateTimeUtil.addDaysToCalendar(thisUpdate, -1);
        nextUpdate = DateTimeUtil.addDaysToCalendar(nextUpdate, 30);
        responseBuilder = FACTORY.createBasicOCSPRespBuilder(FACTORY.createRespID(subjectDN));
    }

    public TestOcspResponseBuilder(X509Certificate issuerCert, PrivateKey issuerPrivateKey)
            throws CertificateEncodingException, IOException {
        this(issuerCert, issuerPrivateKey, FACTORY.createCertificateStatus().getGood());
    }

    public X509Certificate getIssuerCert() {
        return issuerCert;
    }

    public void setCertificateStatus(ICertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public void setThisUpdate(Calendar thisUpdate) {
        this.thisUpdate = thisUpdate;
    }

    public void setNextUpdate(Calendar nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public byte[] makeOcspResponse(byte[] requestBytes) throws IOException, CertificateException,
            AbstractOperatorCreationException, AbstractOCSPException {
        IBasicOCSPResp ocspResponse = makeOcspResponseObject(requestBytes);
        return ocspResponse.getEncoded();
    }

    public IBasicOCSPResp makeOcspResponseObject(byte[] requestBytes) throws CertificateException,
            AbstractOperatorCreationException, AbstractOCSPException, IOException {
        IOCSPReq ocspRequest = FACTORY.createOCSPReq(requestBytes);
        IReq[] requestList = ocspRequest.getRequestList();

        IExtension extNonce = ocspRequest.getExtension(FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspNonce());
        if (!FACTORY.isNullExtension(extNonce)) {
            responseBuilder.setResponseExtensions(FACTORY.createExtensions(extNonce));
        }

        for (IReq req : requestList) {
            responseBuilder.addResponse(req.getCertID(), certificateStatus, thisUpdate.getTime(),
                    nextUpdate.getTime(), FACTORY.createNullExtensions());
        }

        Date time = DateTimeUtil.getCurrentTimeDate();

        IX509CertificateHolder[] chain = {FACTORY.createJcaX509CertificateHolder(issuerCert)};
        IContentSigner signer = FACTORY.createJcaContentSignerBuilder(SIGN_ALG).setProvider(FACTORY.getProviderName())
                .build(issuerPrivateKey);
        return responseBuilder.build(signer, chain, time);
    }
}
