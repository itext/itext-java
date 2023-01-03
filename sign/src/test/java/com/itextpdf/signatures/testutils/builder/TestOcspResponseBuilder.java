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
