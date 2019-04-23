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
    private X509Certificate issuerCert;
    private PrivateKey issuerPrivateKey;
    private CertificateStatus certificateStatus = CertificateStatus.GOOD;
    private Calendar thisUpdate = DateTimeUtil.getCurrentTimeCalendar();
    private Calendar nextUpdate = DateTimeUtil.getCurrentTimeCalendar();

    public TestOcspResponseBuilder(X509Certificate issuerCert, PrivateKey issuerPrivateKey) throws CertificateEncodingException {
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

    public byte[] makeOcspResponse(byte[] requestBytes) throws IOException, CertificateException, OperatorCreationException, OCSPException {
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

        X509CertificateHolder[] chain = {new JcaX509CertificateHolder(issuerCert)};
        ContentSigner signer = new JcaContentSignerBuilder(SIGN_ALG).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(issuerPrivateKey);
        BasicOCSPResp ocspResponse = responseBuilder.build(signer, chain, time);
//        return new OCSPRespBuilder().build(ocspResult, ocspResponse).getEncoded();
        return ocspResponse.getEncoded();
    }
}
